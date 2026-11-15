package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

sealed class AppUpdateState {
    data object Idle : AppUpdateState()
    data object Checking : AppUpdateState()
    data class UpdateAvailable(
        val versionName: String,
        val releaseTitle: String,
        val releaseNotes: String,
        val downloadUrl: String,
        val fileName: String,
        val fileSizeBytes: Long
    ) : AppUpdateState()

    data class Downloading(
        val versionName: String,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val progress: Float
    ) : AppUpdateState()

    data class ReadyToInstall(
        val versionName: String,
        val apkFile: File,
        val requiresPermission: Boolean = false
    ) : AppUpdateState()

    data class UpToDate(
        val currentVersion: String
    ) : AppUpdateState()

    data class Error(
        val messageKey: String,
        val detail: String? = null
    ) : AppUpdateState()
}

enum class InstallLaunchResult {
    LAUNCHED_SYSTEM_INSTALLER,
    PERMISSION_REQUIRED,
    FAILED
}

object AppUpdateManager {
    const val GITHUB_REPO_URL = "https://github.com/charfeddine23/Respit"
    private const val GITHUB_LATEST_RELEASE_API = "https://api.github.com/repos/charfeddine23/Respit/releases/latest"
    private const val GITHUB_RELEASES_LIST_API = "https://api.github.com/repos/charfeddine23/Respit/releases?per_page=10"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    suspend fun checkLatestRelease(currentVersion: String): AppUpdateState = withContext(Dispatchers.IO) {
        try {
            val latestRequest = Request.Builder()
                .url(GITHUB_LATEST_RELEASE_API)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "RespitePulse-Android-Updater")
                .build()

            client.newCall(latestRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyStr = response.body?.string().orEmpty()
                    if (bodyStr.isNotBlank()) {
                        return@withContext parseReleaseObject(JSONObject(bodyStr), currentVersion)
                    }
                } else if (response.code != 404) {
                    return@withContext AppUpdateState.Error(
                        messageKey = "update_check_failed",
                        detail = "HTTP ${response.code}"
                    )
                }
            }

            // Fallback to releases list (handles pre-releases or repos without a 'latest' release)
            val listRequest = Request.Builder()
                .url(GITHUB_RELEASES_LIST_API)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "RespitePulse-Android-Updater")
                .build()

            client.newCall(listRequest).execute().use { listResponse ->
                if (listResponse.isSuccessful) {
                    val arrayStr = listResponse.body?.string().orEmpty()
                    val releases = JSONArray(arrayStr.ifBlank { "[]" })
                    if (releases.length() == 0) {
                        return@withContext AppUpdateState.UpToDate(currentVersion)
                    }
                    // Pick first non-draft release (preferring one with an APK asset)
                    var bestRelease: JSONObject? = null
                    for (i in 0 until releases.length()) {
                        val rel = releases.optJSONObject(i) ?: continue
                        if (rel.optBoolean("draft", false)) continue
                        if (bestRelease == null) bestRelease = rel
                        if (findApkAsset(rel.optJSONArray("assets")) != null) {
                            bestRelease = rel
                            break
                        }
                    }
                    return@withContext if (bestRelease != null) {
                        parseReleaseObject(bestRelease, currentVersion)
                    } else {
                        AppUpdateState.UpToDate(currentVersion)
                    }
                } else if (listResponse.code == 404) {
                    return@withContext AppUpdateState.Error(
                        messageKey = "update_check_failed",
                        detail = "HTTP 404"
                    )
                } else {
                    return@withContext AppUpdateState.Error(
                        messageKey = "update_check_failed",
                        detail = "HTTP ${listResponse.code}"
                    )
                }
            }
        } catch (e: Exception) {
            AppUpdateState.Error(
                messageKey = "update_check_failed",
                detail = e.localizedMessage
            )
        }
    }

    internal fun parseReleaseObject(release: JSONObject, currentVersion: String): AppUpdateState {
        val rawTag = release.optString("tag_name", "").trim()
        val remoteVersion = rawTag.removePrefix("v").removePrefix("V").trim()
        if (remoteVersion.isBlank()) {
            return AppUpdateState.UpToDate(currentVersion)
        }

        if (compareVersions(remoteVersion, currentVersion) <= 0) {
            return AppUpdateState.UpToDate(currentVersion)
        }

        val apkAsset = findApkAsset(release.optJSONArray("assets"))
            ?: return AppUpdateState.Error(messageKey = "update_no_apk_asset")

        val downloadUrl = apkAsset.optString("browser_download_url", "").trim()
        if (downloadUrl.isBlank()) {
            return AppUpdateState.Error(messageKey = "update_no_apk_asset")
        }

        val releaseTitle = release.optString("name", "").ifBlank { "v$remoteVersion" }
        val releaseNotes = release.optString("body", "").trim()
        val fileName = apkAsset.optString("name", "RespitePulse.v$remoteVersion.apk")
        val sizeBytes = apkAsset.optLong("size", 0L)

        return AppUpdateState.UpdateAvailable(
            versionName = remoteVersion,
            releaseTitle = releaseTitle,
            releaseNotes = releaseNotes,
            downloadUrl = downloadUrl,
            fileName = fileName,
            fileSizeBytes = sizeBytes
        )
    }

    private fun findApkAsset(assets: JSONArray?): JSONObject? {
        if (assets == null) return null
        for (i in 0 until assets.length()) {
            val asset = assets.optJSONObject(i) ?: continue
            val name = asset.optString("name", "")
            val url = asset.optString("browser_download_url", "")
            if (name.endsWith(".apk", ignoreCase = true) && url.startsWith("https://")) {
                return asset
            }
        }
        return null
    }

    fun compareVersions(remote: String, local: String): Int {
        val remoteParts = extractVersionParts(remote)
        val localParts = extractVersionParts(local)
        val maxLen = maxOf(remoteParts.size, localParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r != l) return r.compareTo(l)
        }
        return 0
    }

    private fun extractVersionParts(version: String): List<Int> {
        val clean = version.trim().removePrefix("v").removePrefix("V")
            .substringBefore("-")
            .substringBefore("+")
        return clean.split(".")
            .mapNotNull { part ->
                part.filter { it.isDigit() }.toIntOrNull()
            }
    }

    suspend fun downloadApkInApp(
        context: Context,
        downloadUrl: String,
        versionName: String,
        expectedSizeBytes: Long,
        onProgress: (downloadedBytes: Long, totalBytes: Long, progress: Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            // Clear stale cached APKs
            updatesDir.listFiles()?.forEach { file ->
                if (file.isFile) file.delete()
            }

            val safeVersion = versionName.replace(Regex("[^A-Za-z0-9._-]"), "_")
            val tempFile = File(updatesDir, "RespitePulse.v$safeVersion.apk.part")
            val finalFile = File(updatesDir, "RespitePulse.v$safeVersion.apk")

            val request = Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", "RespitePulse-Android-Updater")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IllegalStateException("HTTP ${response.code}"))
                }
                val body = response.body ?: return@withContext Result.failure(IllegalStateException("Empty body"))
                val totalBytes = body.contentLength().takeIf { it > 0L } ?: expectedSizeBytes
                var downloadedBytes = 0L
                val buffer = ByteArray(16 * 1024)

                body.byteStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        var lastReportTime = 0L
                        while (true) {
                            coroutineContext.ensureActive()
                            val read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            downloadedBytes += read

                            val now = System.currentTimeMillis()
                            if (now - lastReportTime >= 80L || (totalBytes > 0L && downloadedBytes >= totalBytes)) {
                                lastReportTime = now
                                val fraction = if (totalBytes > 0L) {
                                    (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                                } else {
                                    0f
                                }
                                onProgress(downloadedBytes, totalBytes, fraction)
                            }
                        }
                        output.flush()
                    }
                }

                if (finalFile.exists()) finalFile.delete()
                if (!tempFile.renameTo(finalFile)) {
                    tempFile.copyTo(finalFile, overwrite = true)
                    tempFile.delete()
                }
                onProgress(downloadedBytes, if (totalBytes > 0L) totalBytes else downloadedBytes, 1f)
                Result.success(finalFile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun canInstallPackages(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun launchSystemPackageInstaller(context: Context, apkFile: File): InstallLaunchResult {
        if (!apkFile.exists()) return InstallLaunchResult.FAILED
        if (!canInstallPackages(context)) {
            return InstallLaunchResult.PERMISSION_REQUIRED
        }
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            InstallLaunchResult.LAUNCHED_SYSTEM_INSTALLER
        } catch (_: Exception) {
            InstallLaunchResult.FAILED
        }
    }

    fun openInstallPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                // Ignore if device has no settings handler
            }
        }
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return "0 MB"
        val mb = bytes / (1024.0 * 1024.0)
        return String.format(java.util.Locale.US, "%.1f MB", mb)
    }
}
