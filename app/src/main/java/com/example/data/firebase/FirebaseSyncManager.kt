package com.example.data.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.Personnel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class CloudSyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

class FirebaseSyncManager(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val auth: FirebaseAuth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser = _currentUser.asStateFlow()

    private val _syncStatus = MutableStateFlow(CloudSyncStatus.IDLE)
    val syncStatus = _syncStatus.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage = _syncMessage.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(0L)
    val lastSyncTime = _lastSyncTime.asStateFlow()

    private val firestore: FirebaseFirestore by lazy {
        val databaseId = try {
            context.getString(R.string.firestore_database_id)
        } catch (e: Exception) {
            "ai-studio-android-c410e9f1-087e-4b10-b984-d5a026942d03"
        }
        FirebaseFirestore.getInstance(databaseId)
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signInWithGoogle(
        activity: Activity,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            onError("Web Client ID is missing in google-services.json")
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = auth.signInWithCredential(authCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        _currentUser.value = user
                        withContext(Dispatchers.Main) {
                            onSuccess(user)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onError("User authentication failed")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Unexpected credential format received")
                    }
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("Auth", "Google Sign-In flow cancelled by user: ${e.message}")
            } catch (e: Exception) {
                Log.e("Auth", "Google Sign-In failed", e)
                withContext(Dispatchers.Main) {
                    onError(e.localizedMessage ?: "Google Sign-In failed")
                }
            }
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        scope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w("Auth", "Failed to clear credential state: ${e.message}")
            } finally {
                auth.signOut()
                _currentUser.value = null
                _syncStatus.value = CloudSyncStatus.IDLE
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }

    suspend fun uploadAllPersonnel(personnelList: List<Personnel>): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext Pair(false, "User not signed in with Google")
        _syncStatus.value = CloudSyncStatus.SYNCING
        try {
            val userPersonnelCol = firestore.collection("users").document(uid).collection("personnel")
            for (p in personnelList) {
                val docRef = userPersonnelCol.document(p.id.toString())
                val payload = hashMapOf<String, Any>(
                    "userId" to uid,
                    "fullName" to p.fullName,
                    "rank" to p.rank,
                    "registrationNumber" to p.registrationNumber,
                    "startDateMillis" to p.startDateMillis,
                    "returnDateMillis" to p.returnDateMillis,
                    "isReturnedToDuty" to p.isReturnedToDuty,
                    "leaveType" to p.leaveType,
                    "wilaya" to p.wilaya,
                    "notes" to p.notes,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            _syncStatus.value = CloudSyncStatus.SUCCESS
            _lastSyncTime.value = System.currentTimeMillis()
            _syncMessage.value = "تمت المزامنة السحابية بنجاح (${personnelList.size} فرد)"
            Pair(true, null)
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Upload failed", e)
            _syncStatus.value = CloudSyncStatus.ERROR
            _syncMessage.value = e.localizedMessage
            Pair(false, e.localizedMessage)
        }
    }

    suspend fun downloadRemotePersonnel(): Pair<List<Personnel>?, String?> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext Pair(null, "User not signed in with Google")
        _syncStatus.value = CloudSyncStatus.SYNCING
        try {
            val snapshot = firestore.collection("users").document(uid).collection("personnel").get().await()
            val list = mutableListOf<Personnel>()
            for (doc in snapshot.documents) {
                val id = doc.id.toLongOrNull() ?: System.currentTimeMillis()
                val fullName = doc.getString("fullName") ?: ""
                val rank = doc.getString("rank") ?: ""
                val regNumber = doc.getString("registrationNumber") ?: ""
                val startMillis = doc.getLong("startDateMillis") ?: System.currentTimeMillis()
                val returnMillis = doc.getLong("returnDateMillis") ?: System.currentTimeMillis()
                val isReturned = doc.getBoolean("isReturnedToDuty") ?: false
                val leaveType = doc.getString("leaveType") ?: "إجازة عادية"
                val wilaya = doc.getString("wilaya") ?: "الجزائر"
                val notes = doc.getString("notes") ?: ""

                if (fullName.isNotBlank()) {
                    list.add(
                        Personnel(
                            id = id,
                            fullName = fullName,
                            rank = rank,
                            registrationNumber = regNumber,
                            startDateMillis = startMillis,
                            returnDateMillis = returnMillis,
                            isReturnedToDuty = isReturned,
                            leaveType = leaveType,
                            wilaya = wilaya,
                            notes = notes
                        )
                    )
                }
            }
            _syncStatus.value = CloudSyncStatus.SUCCESS
            _lastSyncTime.value = System.currentTimeMillis()
            _syncMessage.value = "تم جلب ${list.size} فرد من Firebase"
            Pair(list, null)
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Download failed", e)
            _syncStatus.value = CloudSyncStatus.ERROR
            _syncMessage.value = e.localizedMessage
            Pair(null, e.localizedMessage)
        }
    }

    suspend fun deleteRemotePersonnel(personnelId: Long): Boolean = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext false
        try {
            firestore.collection("users").document(uid).collection("personnel").document(personnelId.toString()).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Delete remote failed", e)
            false
        }
    }

    suspend fun saveSinglePersonnel(p: Personnel): Boolean = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext false
        try {
            val docRef = firestore.collection("users").document(uid).collection("personnel").document(p.id.toString())
            val payload = hashMapOf<String, Any>(
                "userId" to uid,
                "fullName" to p.fullName,
                "rank" to p.rank,
                "registrationNumber" to p.registrationNumber,
                "startDateMillis" to p.startDateMillis,
                "returnDateMillis" to p.returnDateMillis,
                "isReturnedToDuty" to p.isReturnedToDuty,
                "leaveType" to p.leaveType,
                "wilaya" to p.wilaya,
                "notes" to p.notes,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Save single remote failed", e)
            false
        }
    }
}
