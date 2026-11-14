package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppDatabase
import com.example.data.AppNotification
import com.example.data.AppThemeMode
import com.example.data.AppThemeStyle
import com.example.data.LeaveRecord
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.PersonnelRepository
import com.example.data.SettingsManager
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.data.normalizeRank
import com.example.data.normalizeWilaya
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppUpdateManager
import com.example.util.AppUpdateState
import com.example.util.InstallLaunchResult
import com.example.util.LeaveNotificationHelper
import com.example.util.PdfExporter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class SortOption(val titleAr: String) {
    RETURN_SOON("الأقرب موعد دخول"),
    OVERDUE_FIRST("المتأخرون أولاً"),
    START_DESC("الأحدث استفادة"),
    NAME_ASC("الاسم (أ - ي)"),
    REG_ASC("رقم التسجيل");

    fun getTitle(lang: AppLanguage): String = when (this) {
        RETURN_SOON -> AppStrings.get("sort_return_soon", lang)
        OVERDUE_FIRST -> AppStrings.get("sort_overdue_first", lang)
        START_DESC -> AppStrings.get("sort_start_desc", lang)
        NAME_ASC -> AppStrings.get("sort_name_asc", lang)
        REG_ASC -> AppStrings.get("sort_reg_asc", lang)
    }
}

data class DashboardStats(
    val total: Int = 0,
    val atHome: Int = 0,
    val returningToday: Int = 0,
    val overdue: Int = 0,
    val onDuty: Int = 0,
    val upcoming: Int = 0,
    val eligible: Int = 0
)

sealed class UndoAction(val description: String) {
    data class Deleted(val personnel: Personnel) : UndoAction("تراجع عن حذف فرد")
    data class Updated(val previous: Personnel) : UndoAction("تراجع عن تعديل بيانات")
    data class Added(val id: Long) : UndoAction("تراجع عن إضافة فرد")
    data class ReturnedToDuty(val previous: Personnel) : UndoAction("تراجع عن استئناف العمل")
    data class Imported(val previousList: List<Personnel>) : UndoAction("تراجع عن استيراد البيانات")

    fun getDescription(lang: AppLanguage): String = when (this) {
        is Deleted -> AppStrings.get("undo_deleted", lang)
        is Updated -> AppStrings.get("undo_updated", lang)
        is Added -> AppStrings.get("undo_added", lang)
        is ReturnedToDuty -> AppStrings.get("undo_returned", lang)
        is Imported -> AppStrings.get("undo_imported", lang)
    }
}

class PersonnelViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PersonnelRepository
    private val settingsManager: SettingsManager = SettingsManager(application)

    val searchQuery = MutableStateFlow("")
    val selectedRank = MutableStateFlow("ALL")
    val selectedStatus = MutableStateFlow("ALL")
    val selectedWilaya = MutableStateFlow("ALL")
    val sortOption = MutableStateFlow(SortOption.RETURN_SOON)

    // User preferences & settings state
    val currentThemeMode = MutableStateFlow(settingsManager.themeMode)
    val currentThemeStyle = MutableStateFlow(settingsManager.themeStyle)
    val isDarkMode = MutableStateFlow(settingsManager.isDarkMode)
    val dutyThresholdDays = MutableStateFlow(settingsManager.dutyThresholdDays)
    val isBiometricEnabled = MutableStateFlow(settingsManager.isBiometricEnabled)
    val isPinEnabled = MutableStateFlow(settingsManager.isBiometricEnabled)
    val pinCode = MutableStateFlow("")
    val securityLockType = MutableStateFlow("BIOMETRIC")
    val patternCode = MutableStateFlow("")
    val isPasskeyEnabled = MutableStateFlow(false)
    val isLocked = MutableStateFlow(settingsManager.isBiometricEnabled)
    val currentLanguage = MutableStateFlow(AppLanguage.fromCode(settingsManager.language))
    val isFocusMode = MutableStateFlow(settingsManager.isFocusMode)

    fun toggleFocusMode() {
        val next = !isFocusMode.value
        isFocusMode.value = next
        settingsManager.isFocusMode = next
    }

    fun setFocusMode(enabled: Boolean) {
        isFocusMode.value = enabled
        settingsManager.isFocusMode = enabled
    }

    // Dynamic UI Animations state (Master toggle for weak device optimization)
    val isDynamicAnimationsEnabled = MutableStateFlow(settingsManager.isDynamicAnimationsEnabled)

    fun setDynamicAnimationsEnabled(enabled: Boolean) {
        isDynamicAnimationsEnabled.value = enabled
        settingsManager.isDynamicAnimationsEnabled = enabled
    }

    // Google Drive Sync state
    val isDriveSyncEnabled = MutableStateFlow(settingsManager.isGoogleDriveSyncEnabled)
    val lastDriveBackupTime = MutableStateFlow(settingsManager.lastGoogleDriveBackupTime)

    fun setDriveSyncEnabled(enabled: Boolean) {
        isDriveSyncEnabled.value = enabled
        settingsManager.isGoogleDriveSyncEnabled = enabled
    }

    fun updateLastDriveBackup(time: Long = System.currentTimeMillis()) {
        lastDriveBackupTime.value = time
        settingsManager.lastGoogleDriveBackupTime = time
    }

    // Recent Searches state
    val recentSearches = MutableStateFlow<List<String>>(settingsManager.recentSearches)

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length >= 2) {
            settingsManager.addRecentSearch(trimmed)
            recentSearches.value = settingsManager.recentSearches
        }
    }

    fun removeRecentSearch(query: String) {
        settingsManager.removeRecentSearch(query)
        recentSearches.value = settingsManager.recentSearches
    }

    fun clearRecentSearches() {
        settingsManager.clearRecentSearches()
        recentSearches.value = emptyList()
    }

    // Undo action state
    val lastUndoAction = MutableStateFlow<UndoAction?>(null)

    // In-App Update state (Strict in-app check, download & system install; no external redirects)
    private val _appUpdateState = MutableStateFlow<AppUpdateState>(AppUpdateState.Idle)
    val appUpdateState: StateFlow<AppUpdateState> = _appUpdateState.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    private var updateDownloadJob: Job? = null
    private var lastAvailableUpdate: AppUpdateState.UpdateAvailable? = null
    private var hasAutoCheckedUpdate = false

    val allPersonnel: StateFlow<List<Personnel>>
    val allLeaves: StateFlow<List<LeaveRecord>>
    val personnelCount: StateFlow<Int>
    val leavesCount: StateFlow<Int>

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = PersonnelRepository(db.personnelDao(), db.leaveRecordDao())
        allPersonnel = repository.allPersonnel
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allLeaves = repository.allLeaves
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        personnelCount = repository.personnelCount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        leavesCount = repository.leavesCount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    }

    private data class FilterParams(
        val query: String,
        val rank: String,
        val status: String,
        val wilaya: String,
        val sort: SortOption,
        val threshold: Int,
        val lang: AppLanguage
    )

    private val filterParams = combine(
        searchQuery,
        selectedRank,
        selectedStatus,
        selectedWilaya,
        combine(sortOption, dutyThresholdDays, currentLanguage) { s, t, l -> Triple(s, t, l) }
    ) { query, rankFilter, statusFilter, wilayaFilter, (sort, threshold, lang) ->
        FilterParams(query, rankFilter, statusFilter, wilayaFilter, sort, threshold, lang)
    }

    val filteredPersonnel: StateFlow<List<Personnel>> = combine(
        allPersonnel,
        filterParams
    ) { personnelList, params ->
        var list = personnelList

        // Search query
        val q = params.query.trim().lowercase(Locale.getDefault())
        if (q.isNotEmpty()) {
            list = list.filter {
                it.fullName.lowercase(Locale.getDefault()).contains(q) ||
                        it.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                        it.rank.lowercase(Locale.getDefault()).contains(q) ||
                        formatRank(it.rank, params.lang).lowercase(Locale.getDefault()).contains(q) ||
                        it.wilaya.lowercase(Locale.getDefault()).contains(q) ||
                        formatWilaya(it.wilaya, params.lang, includeCode = true).lowercase(Locale.getDefault()).contains(q)
            }
        }

        // Rank filter
        if (params.rank != "ALL") {
            list = list.filter { it.rank == params.rank }
        }

        // Status filter
        if (params.status != "ALL") {
            if (params.status == "ELIGIBLE") {
                list = list.filter { it.isEligibleForLeave(params.threshold) }
            } else {
                list = list.filter { it.status.name == params.status }
            }
        }

        // Wilaya filter
        if (params.wilaya != "ALL") {
            list = list.filter { it.wilaya == params.wilaya }
        }

        // Sorting
        when (params.sort) {
            SortOption.RETURN_SOON -> list.sortedWith(
                compareBy<Personnel> {
                    when (it.status) {
                        LeaveStatus.RETURNING_TODAY -> 0
                        LeaveStatus.AT_HOME -> 1
                        LeaveStatus.OVERDUE -> 2
                        LeaveStatus.UPCOMING -> 3
                        LeaveStatus.ON_DUTY -> 4
                    }
                }.thenBy { it.returnDateMillis }
            )
            SortOption.OVERDUE_FIRST -> list.sortedWith(
                compareBy<Personnel> {
                    when (it.status) {
                        LeaveStatus.OVERDUE -> 0
                        LeaveStatus.RETURNING_TODAY -> 1
                        LeaveStatus.AT_HOME -> 2
                        LeaveStatus.UPCOMING -> 3
                        LeaveStatus.ON_DUTY -> 4
                    }
                }.thenByDescending { it.daysOverdue }
            )
            SortOption.START_DESC -> list.sortedByDescending { it.startDateMillis }
            SortOption.NAME_ASC -> list.sortedBy { it.fullName }
            SortOption.REG_ASC -> list.sortedBy { it.registrationNumber }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<DashboardStats> = combine(allPersonnel, dutyThresholdDays) { list, threshold ->
        val total = list.size
        var atHome = 0
        var returningToday = 0
        var overdue = 0
        var onDuty = 0
        var upcoming = 0
        var eligible = 0

        list.forEach { p ->
            when (p.status) {
                LeaveStatus.AT_HOME -> atHome++
                LeaveStatus.RETURNING_TODAY -> returningToday++
                LeaveStatus.OVERDUE -> overdue++
                LeaveStatus.ON_DUTY -> {
                    onDuty++
                    if (p.isEligibleForLeave(threshold)) {
                        eligible++
                    }
                }
                LeaveStatus.UPCOMING -> upcoming++
            }
        }

        DashboardStats(
            total = total,
            atHome = atHome,
            returningToday = returningToday,
            overdue = overdue,
            onDuty = onDuty,
            upcoming = upcoming,
            eligible = eligible
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    val uniqueRanks: StateFlow<List<String>> = allPersonnel.combine(allPersonnel) { list, _ ->
        list.map { it.rank }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uniqueWilayas: StateFlow<List<String>> = allPersonnel.combine(allPersonnel) { list, _ ->
        list.map { it.wilaya }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleTheme() {
        val next = !isDarkMode.value
        val nextMode = if (next) AppThemeMode.DARK else AppThemeMode.LIGHT
        setThemeMode(nextMode)
    }

    fun setThemeMode(mode: AppThemeMode) {
        currentThemeMode.value = mode
        settingsManager.themeMode = mode
        if (mode == AppThemeMode.DARK) {
            isDarkMode.value = true
        } else if (mode == AppThemeMode.LIGHT) {
            isDarkMode.value = false
        }
    }

    fun setLanguage(lang: AppLanguage) {
        currentLanguage.value = lang
        settingsManager.language = lang.code
    }

    fun checkUpcomingNotifications(): Int {
        return LeaveNotificationHelper.checkApproachingLeaves(
            getApplication(),
            allPersonnel.value,
            currentLanguage.value.code
        )
    }

    val pinnedNotificationIds = MutableStateFlow<Set<String>>(settingsManager.pinnedNotificationIds)
    val dismissedNotificationIds = MutableStateFlow<Set<String>>(settingsManager.dismissedNotificationIds)

    val notifications: StateFlow<List<AppNotification>> = combine(
        allPersonnel,
        pinnedNotificationIds,
        dismissedNotificationIds,
        dutyThresholdDays,
        currentLanguage
    ) { list, pinned, dismissed, threshold, lang ->
        val result = mutableListOf<AppNotification>()
        val today = Personnel.toStartOfDay(System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        list.forEach { p ->
            val startDay = Personnel.toStartOfDay(p.startDateMillis)
            val returnDay = Personnel.toStartOfDay(p.returnDateMillis)
            val daysUntilStart = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(startDay - today)
            val daysUntilReturn = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(returnDay - today)
            val localizedRank = formatRank(p.rank, lang)
            val localizedLeaveType = formatLeaveType(p.leaveType, lang)

            // 1. Returning Today
            if (!p.isReturnedToDuty && p.status == LeaveStatus.RETURNING_TODAY) {
                val notifId = "ret_today_${p.id}"
                if (notifId !in dismissed) {
                    val title = when (lang) {
                        AppLanguage.FRENCH -> "Retour de congé aujourd'hui"
                        AppLanguage.ENGLISH -> "Returning to Duty Today"
                        else -> "موعد الدخول واستئناف العمل اليوم"
                    }
                    val msg = when (lang) {
                        AppLanguage.FRENCH -> "$localizedRank ${p.fullName} doit reprendre le service aujourd'hui (${sdf.format(Date(p.returnDateMillis))})."
                        AppLanguage.ENGLISH -> "$localizedRank ${p.fullName} is scheduled to resume duty today (${sdf.format(Date(p.returnDateMillis))})."
                        else -> "على $localizedRank / ${p.fullName} الالتحاق واستئناف الخدمة اليوم (${sdf.format(Date(p.returnDateMillis))})."
                    }
                    result.add(
                        AppNotification(
                            id = notifId,
                            title = title,
                            message = msg,
                            timestamp = p.returnDateMillis,
                            personnelId = p.id,
                            type = "RETURNING_TODAY",
                            isPinned = notifId in pinned,
                            formattedDate = sdf.format(Date(p.returnDateMillis))
                        )
                    )
                }
            }

            // 2. Overdue
            if (!p.isReturnedToDuty && p.status == LeaveStatus.OVERDUE) {
                val notifId = "overdue_${p.id}"
                if (notifId !in dismissed) {
                    val title = when (lang) {
                        AppLanguage.FRENCH -> "Retard de reprise de service"
                        AppLanguage.ENGLISH -> "Overdue Return to Duty"
                        else -> "تنبيه: تأخر عن موعد الدخول"
                    }
                    val msg = when (lang) {
                        AppLanguage.FRENCH -> "$localizedRank ${p.fullName} a dépassé la date de retour (${p.getCountdownText(lang)})."
                        AppLanguage.ENGLISH -> "$localizedRank ${p.fullName} is overdue for duty return (${p.getCountdownText(lang)})."
                        else -> "تأخر $localizedRank / ${p.fullName} عن موعد الالتحاق المقرر (${p.getCountdownText(lang)})."
                    }
                    result.add(
                        AppNotification(
                            id = notifId,
                            title = title,
                            message = msg,
                            timestamp = p.returnDateMillis,
                            personnelId = p.id,
                            type = "OVERDUE",
                            isPinned = notifId in pinned,
                            formattedDate = sdf.format(Date(p.returnDateMillis))
                        )
                    )
                }
            }

            // 3. Approaching return (1 or 2 days left)
            if (!p.isReturnedToDuty && daysUntilReturn in 1..2) {
                val notifId = "appr_ret_${p.id}"
                if (notifId !in dismissed) {
                    val title = when (lang) {
                        AppLanguage.FRENCH -> "Retour de congé imminent"
                        AppLanguage.ENGLISH -> "Duty Return Approaching"
                        else -> "تنبيه: اقتراب موعد استئناف العمل"
                    }
                    val msg = when (lang) {
                        AppLanguage.FRENCH -> "Il reste $daysUntilReturn jour(s) pour $localizedRank ${p.fullName} (${sdf.format(Date(p.returnDateMillis))})."
                        AppLanguage.ENGLISH -> "$daysUntilReturn day(s) remaining for $localizedRank ${p.fullName} (${sdf.format(Date(p.returnDateMillis))})."
                        else -> {
                            val timeNotice = if (daysUntilReturn == 1L) "متبقي يوم واحد (غداً)" else "متبقي يومان (خلال يومين)"
                            "$timeNotice لعودة $localizedRank / ${p.fullName} للعمل (${sdf.format(Date(p.returnDateMillis))})."
                        }
                    }
                    result.add(
                        AppNotification(
                            id = notifId,
                            title = title,
                            message = msg,
                            timestamp = p.returnDateMillis,
                            personnelId = p.id,
                            type = "APPROACHING_RETURN",
                            isPinned = notifId in pinned,
                            formattedDate = sdf.format(Date(p.returnDateMillis))
                        )
                    )
                }
            }

            // 4. Approaching start of leave (starts within 0..2 days)
            if (!p.isReturnedToDuty && daysUntilStart in 0..2 && today <= startDay) {
                val notifId = "appr_start_${p.id}"
                if (notifId !in dismissed) {
                    val title = when (lang) {
                        AppLanguage.FRENCH -> "Départ en congé imminent"
                        AppLanguage.ENGLISH -> "Upcoming Leave"
                        else -> "تنبيه: موعد انطلاق الإجازة"
                    }
                    val msg = when (lang) {
                        AppLanguage.FRENCH -> "$localizedRank ${p.fullName} commence son congé le ${sdf.format(Date(p.startDateMillis))} ($localizedLeaveType)."
                        AppLanguage.ENGLISH -> "$localizedRank ${p.fullName} starts leave on ${sdf.format(Date(p.startDateMillis))} ($localizedLeaveType)."
                        else -> "يبدأ $localizedRank / ${p.fullName} إجازته ($localizedLeaveType) بتاريخ ${sdf.format(Date(p.startDateMillis))}."
                    }
                    result.add(
                        AppNotification(
                            id = notifId,
                            title = title,
                            message = msg,
                            timestamp = p.startDateMillis,
                            personnelId = p.id,
                            type = "APPROACHING_START",
                            isPinned = notifId in pinned,
                            formattedDate = sdf.format(Date(p.startDateMillis))
                        )
                    )
                }
            }

            // 5. Eligible for leave
            if (p.isReturnedToDuty && p.daysSinceDutyReturn >= threshold) {
                val notifId = "eligible_${p.id}"
                if (notifId !in dismissed) {
                    val title = when (lang) {
                        AppLanguage.FRENCH -> "Éligible au congé"
                        AppLanguage.ENGLISH -> "Eligible for Leave"
                        else -> "استحقاق إجازة جديدة"
                    }
                    val msg = when (lang) {
                        AppLanguage.FRENCH -> "$localizedRank ${p.fullName} a accompli ${p.daysSinceDutyReturn} jours de service (seuil : $threshold jours)."
                        AppLanguage.ENGLISH -> "$localizedRank ${p.fullName} has completed ${p.daysSinceDutyReturn} duty days (threshold: $threshold days)."
                        else -> "قضى $localizedRank / ${p.fullName} مدة ${p.daysSinceDutyReturn} يوم في الخدمة وتجاوز عتبة الاستحقاق ($threshold يوم)."
                    }
                    result.add(
                        AppNotification(
                            id = notifId,
                            title = title,
                            message = msg,
                            timestamp = System.currentTimeMillis(),
                            personnelId = p.id,
                            type = "ELIGIBLE",
                            isPinned = notifId in pinned,
                            formattedDate = sdf.format(Date())
                        )
                    )
                }
            }
        }

        result.sortedWith(
            compareByDescending<AppNotification> { it.isPinned }
                .thenBy {
                    when (it.type) {
                        "OVERDUE" -> 0
                        "RETURNING_TODAY" -> 1
                        "APPROACHING_RETURN" -> 2
                        "APPROACHING_START" -> 3
                        "ELIGIBLE" -> 4
                        else -> 5
                    }
                }
                .thenByDescending { it.timestamp }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = notifications
        .combine(pinnedNotificationIds) { list, _ -> list.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun togglePinNotification(id: String) {
        val current = settingsManager.pinnedNotificationIds.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        settingsManager.pinnedNotificationIds = current
        pinnedNotificationIds.value = current
    }

    fun dismissNotification(id: String) {
        val dismissed = settingsManager.dismissedNotificationIds.toMutableSet()
        dismissed.add(id)
        settingsManager.dismissedNotificationIds = dismissed
        dismissedNotificationIds.value = dismissed

        val pinned = settingsManager.pinnedNotificationIds.toMutableSet()
        if (pinned.contains(id)) {
            pinned.remove(id)
            settingsManager.pinnedNotificationIds = pinned
            pinnedNotificationIds.value = pinned
        }
    }

    fun clearAllNotifications() {
        val allIds = notifications.value.map { it.id }.toSet()
        val dismissed = settingsManager.dismissedNotificationIds.toMutableSet()
        dismissed.addAll(allIds)
        settingsManager.dismissedNotificationIds = dismissed
        dismissedNotificationIds.value = dismissed

        val pinned = settingsManager.pinnedNotificationIds.toMutableSet()
        pinned.removeAll(allIds)
        settingsManager.pinnedNotificationIds = pinned
        pinnedNotificationIds.value = pinned
    }

    fun restoreDismissedNotifications() {
        settingsManager.dismissedNotificationIds = emptySet()
        dismissedNotificationIds.value = emptySet()
    }

    fun setThemeStyle(style: AppThemeStyle) {
        settingsManager.themeStyle = style
        currentThemeStyle.value = style
    }

    fun setDutyThreshold(days: Int) {
        val safeDays = days.coerceIn(5, 365)
        dutyThresholdDays.value = safeDays
        settingsManager.dutyThresholdDays = safeDays
    }

    fun setBiometricLock(enabled: Boolean) {
        settingsManager.isBiometricEnabled = enabled
        settingsManager.isPinEnabled = enabled
        isBiometricEnabled.value = enabled
        isPinEnabled.value = enabled
        if (!enabled) {
            isLocked.value = false
        }
    }

    fun setPinCode(newPin: String, enabled: Boolean) {
        setBiometricLock(enabled)
    }

    fun setSecuritySettings(
        enabled: Boolean,
        lockType: String = "BIOMETRIC",
        pin: String = "",
        pattern: String = "",
        biometric: Boolean = enabled,
        passkey: Boolean = false
    ) {
        setBiometricLock(enabled || biometric)
    }

    fun verifyPin(enteredPin: String): Boolean {
        isLocked.value = false
        return true
    }

    fun verifyPattern(enteredPattern: String): Boolean {
        isLocked.value = false
        return true
    }

    fun unlockWithBiometric(): Boolean {
        isLocked.value = false
        return true
    }

    fun unlockWithPasskey(): Boolean {
        isLocked.value = false
        return true
    }

    fun lockApp() {
        if (isBiometricEnabled.value) {
            isLocked.value = true
        }
    }

    fun undo() {
        val action = lastUndoAction.value ?: return
        viewModelScope.launch {
            when (action) {
                is UndoAction.Deleted -> {
                    repository.insert(action.personnel)
                }
                is UndoAction.Updated -> {
                    repository.update(action.previous)
                }
                is UndoAction.Added -> {
                    val p = repository.getPersonnelById(action.id)
                    if (p != null) {
                        repository.delete(p)
                    }
                }
                is UndoAction.ReturnedToDuty -> {
                    repository.update(action.previous)
                }
                is UndoAction.Imported -> {
                    repository.clearAndReset(action.previousList)
                }
            }
            lastUndoAction.value = null
        }
    }

    fun savePersonnel(
        id: Long = 0,
        fullName: String,
        rank: String,
        registrationNumber: String,
        startDateMillis: Long,
        returnDateMillis: Long,
        isReturnedToDuty: Boolean,
        leaveType: String,
        wilaya: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (id != 0L) {
                val previous = repository.getPersonnelById(id)
                if (previous != null) {
                    lastUndoAction.value = UndoAction.Updated(previous)
                }
            }

            val entity = Personnel(
                id = id,
                fullName = fullName,
                rank = normalizeRank(rank),
                registrationNumber = registrationNumber,
                startDateMillis = startDateMillis,
                returnDateMillis = returnDateMillis,
                isReturnedToDuty = isReturnedToDuty,
                leaveType = leaveType,
                wilaya = normalizeWilaya(wilaya),
                notes = notes
            )
            val savedEntity = if (id == 0L) {
                val newId = repository.insert(entity)
                lastUndoAction.value = UndoAction.Added(newId)
                LeaveNotificationHelper.notifyLeaveApproved(
                    getApplication(),
                    entity.copy(id = newId),
                    currentLanguage.value.code
                )
            } else {
                repository.update(entity)
                LeaveNotificationHelper.notifyLeaveApproved(
                    getApplication(),
                    entity,
                    currentLanguage.value.code
                )
            }
        }
    }

    fun confirmReturnToDuty(id: Long) {
        viewModelScope.launch {
            val previous = repository.getPersonnelById(id)
            if (previous != null) {
                lastUndoAction.value = UndoAction.ReturnedToDuty(previous)
            }
            repository.confirmReturnToDuty(id)
        }
    }

    fun grantNewLeave(id: Long, startDateMillis: Long, returnDateMillis: Long) {
        viewModelScope.launch {
            val previous = repository.getPersonnelById(id)
            if (previous != null) {
                lastUndoAction.value = UndoAction.Updated(previous)
                LeaveNotificationHelper.notifyLeaveApproved(
                    getApplication(),
                    previous.copy(startDateMillis = startDateMillis, returnDateMillis = returnDateMillis),
                    currentLanguage.value.code
                )
            }
            repository.grantNewLeave(id, startDateMillis, returnDateMillis)
        }
    }

    fun deletePersonnel(personnel: Personnel) {
        viewModelScope.launch {
            lastUndoAction.value = UndoAction.Deleted(personnel)
            repository.delete(personnel)
        }
    }

    fun exportPdf(context: Context) {
        val list = if (filteredPersonnel.value.isNotEmpty()) filteredPersonnel.value else allPersonnel.value
        PdfExporter.generateAndSharePdf(context, list, currentLanguage.value)
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            fun getOffsetDateMillis(daysOffset: Int): Long {
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_YEAR, daysOffset)
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)
                return c.timeInMillis
            }

            val seed = listOf(
                Personnel(
                    fullName = "عبد الرحمن بلقاسمي",
                    rank = "ملازم أول - Lieutenant",
                    registrationNumber = "DZ-160492",
                    startDateMillis = getOffsetDateMillis(-7),
                    returnDateMillis = getOffsetDateMillis(3),
                    isReturnedToDuty = false,
                    leaveType = "عطلة سنوية",
                    wilaya = "16 - الجزائر - Alger",
                    notes = "الكتيبة الأولى - السرية الثانية"
                ),
                Personnel(
                    fullName = "محمد الأمين يحياوي",
                    rank = "مساعد رئيسي - Adjudant-Major",
                    registrationNumber = "DZ-318920",
                    startDateMillis = getOffsetDateMillis(-4),
                    returnDateMillis = getOffsetDateMillis(6),
                    isReturnedToDuty = false,
                    leaveType = "إجازة",
                    wilaya = "31 - وهران - Oran",
                    notes = "قسم التسيير الإداري"
                ),
                Personnel(
                    fullName = "خالد بن عيسى الدراجي",
                    rank = "رقيب أول - Sergent-Chef",
                    registrationNumber = "DZ-254100",
                    startDateMillis = getOffsetDateMillis(-10),
                    returnDateMillis = getOffsetDateMillis(0),
                    isReturnedToDuty = false,
                    leaveType = "إجازة قصيرة",
                    wilaya = "25 - قسنطينة - Constantine",
                    notes = "سلاح الإشارة والمواصلات"
                ),
                Personnel(
                    fullName = "ياسين بوجمعة صوالحي",
                    rank = "مساعد أول - Adjudant-Chef",
                    registrationNumber = "DZ-057812",
                    startDateMillis = getOffsetDateMillis(-12),
                    returnDateMillis = getOffsetDateMillis(-2),
                    isReturnedToDuty = false,
                    leaveType = "نقاهة",
                    wilaya = "05 - باتنة - Batna",
                    notes = "فصيل الحراسة والتدخل"
                ),
                Personnel(
                    fullName = "حمزة بن زروال الماجري",
                    rank = "عريف أول - Caporal-Chef",
                    registrationNumber = "DZ-194321",
                    startDateMillis = getOffsetDateMillis(-60),
                    returnDateMillis = getOffsetDateMillis(-50),
                    isReturnedToDuty = true,
                    leaveType = "عطلة سنوية",
                    wilaya = "19 - سطيف - Sétif",
                    notes = "الإسناد اللوجيستي (مستحق للإجازة)"
                ),
                Personnel(
                    fullName = "فاروق لعريبي الورتيلاني",
                    rank = "نقيب - Capitaine",
                    registrationNumber = "DZ-098871",
                    startDateMillis = getOffsetDateMillis(3),
                    returnDateMillis = getOffsetDateMillis(13),
                    isReturnedToDuty = false,
                    leaveType = "إجازة",
                    wilaya = "09 - البليدة - Blida",
                    notes = "قيادة الأركان"
                )
            )
            repository.clearAndReset(seed)
        }
    }

    fun generateCsvContent(): String {
        val lang = currentLanguage.value
        val list = allPersonnel.value
        val sb = StringBuilder()
        sb.append('\uFEFF')
        val hRank = AppStrings.get("pdf_col_rank", lang)
        val hName = AppStrings.get("pdf_col_name", lang)
        val hWilaya = AppStrings.get("pdf_col_wilaya", lang)
        val hReg = AppStrings.get("pdf_col_reg", lang)
        val hStart = AppStrings.get("pdf_col_start", lang)
        val hReturn = AppStrings.get("pdf_col_return", lang)
        val hDuration = "${AppStrings.get("leave_duration", lang).trimEnd(':', ' ')} (${AppStrings.get("days", lang)})"
        val hStatus = AppStrings.get("status_col_header", lang).trimEnd(':', ' ')
        val hCountdown = AppStrings.get("pdf_col_status", lang)
        val hNotes = AppStrings.get("notes_plain", lang)
        sb.append("\"$hRank\",\"$hName\",\"$hWilaya\",\"$hReg\",\"$hStart\",\"$hReturn\",\"$hDuration\",\"$hStatus\",\"$hCountdown\",\"$hNotes\"\r\n")
        list.forEach { p ->
            val notesClean = p.notes.replace("\"", "\"\"")
            val rFormatted = formatRank(p.rank, lang)
            val wFormatted = formatWilaya(p.wilaya, lang, includeCode = false)
            val sFormatted = p.status.getCompactBadgeLabel(lang)
            val cFormatted = p.getCountdownText(lang)
            sb.append("\"$rFormatted\",\"${p.fullName}\",\"$wFormatted\",\"${p.registrationNumber}\",\"${p.formattedStartDate}\",\"${p.formattedReturnDate}\",${p.totalLeaveDays},\"$sFormatted\",\"$cFormatted\",\"$notesClean\"\r\n")
        }
        return sb.toString()
    }

    fun generateJsonContent(): String {
        val list = allPersonnel.value
        val sb = StringBuilder("[\n")
        list.forEachIndexed { i, p ->
            sb.append("  {\n")
            sb.append("    \"id\": ${p.id},\n")
            sb.append("    \"fullName\": \"${p.fullName}\",\n")
            sb.append("    \"rank\": \"${p.rank}\",\n")
            sb.append("    \"wilaya\": \"${p.wilaya}\",\n")
            sb.append("    \"registrationNumber\": \"${p.registrationNumber}\",\n")
            sb.append("    \"startDateMillis\": ${p.startDateMillis},\n")
            sb.append("    \"returnDateMillis\": ${p.returnDateMillis},\n")
            sb.append("    \"startDate\": \"${p.formattedStartDate}\",\n")
            sb.append("    \"returnDate\": \"${p.formattedReturnDate}\",\n")
            sb.append("    \"totalDays\": ${p.totalLeaveDays},\n")
            sb.append("    \"isReturnedToDuty\": ${p.isReturnedToDuty},\n")
            sb.append("    \"status\": \"${p.status.name}\",\n")
            sb.append("    \"countdownText\": \"${p.countdownTextAr}\",\n")
            sb.append("    \"leaveType\": \"${p.leaveType}\",\n")
            sb.append("    \"notes\": \"${p.notes.replace("\"", "\\\"")}\"\n")
            sb.append("  }${if (i < list.size - 1) "," else ""}\n")
        }
        sb.append("]")
        return sb.toString()
    }

    fun importPersonnelFromJson(jsonString: String, replaceAll: Boolean = false): Pair<Boolean, String> {
        val lang = currentLanguage.value
        return try {
            val trimmed = jsonString.trim()
            val array = if (trimmed.startsWith("{")) {
                val rootObj = JSONObject(trimmed)
                rootObj.optJSONArray("records") 
                    ?: rootObj.optJSONArray("personnel") 
                    ?: rootObj.optJSONArray("data") 
                    ?: JSONArray()
            } else {
                JSONArray(trimmed)
            }
            if (array.length() == 0) {
                return Pair(false, AppStrings.get("import_empty_error", lang))
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val importedList = mutableListOf<Personnel>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val fullName = obj.optString("fullName", "").ifBlank {
                    obj.optString("name", "غير محدد")
                }
                val rank = normalizeRank(obj.optString("rank", "جندي - Djoundi / Soldat"))
                val regNumber = obj.optString("registrationNumber", "").ifBlank {
                    obj.optString("regNumber", "DZ-000000")
                }
                val wilaya = normalizeWilaya(obj.optString("wilaya", "16 - الجزائر - Alger"))
                val leaveType = obj.optString("leaveType", "عطلة سنوية")
                val notes = obj.optString("notes", "")
                val isReturnedToDuty = obj.optBoolean("isReturnedToDuty", false)

                var startMillis = obj.optLong("startDateMillis", 0L)
                if (startMillis == 0L) {
                    val dateStr = obj.optString("startDate", "")
                    startMillis = if (dateStr.isNotBlank()) {
                        sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
                    } else {
                        System.currentTimeMillis()
                    }
                }

                var returnMillis = obj.optLong("returnDateMillis", 0L)
                if (returnMillis == 0L) {
                    val dateStr = obj.optString("returnDate", "")
                    returnMillis = if (dateStr.isNotBlank()) {
                        sdf.parse(dateStr)?.time ?: (startMillis + 10 * 86400000L)
                    } else {
                        startMillis + 10 * 86400000L
                    }
                }

                val id = if (replaceAll) obj.optLong("id", 0L) else 0L

                importedList.add(
                    Personnel(
                        id = id,
                        fullName = fullName,
                        rank = rank,
                        registrationNumber = regNumber,
                        startDateMillis = startMillis,
                        returnDateMillis = returnMillis,
                        isReturnedToDuty = isReturnedToDuty,
                        leaveType = leaveType,
                        wilaya = wilaya,
                        notes = notes
                    )
                )
            }

            viewModelScope.launch {
                val current = allPersonnel.value
                lastUndoAction.value = UndoAction.Imported(current)
                if (replaceAll) {
                    repository.clearAndReset(importedList)
                } else {
                    repository.insertAll(importedList)
                }
            }

            Pair(true, "${AppStrings.get("import_success_prefix", lang)} ${importedList.size} ${AppStrings.get("import_records_unit", lang)}")
        } catch (e: Exception) {
            Pair(false, "${AppStrings.get("import_json_error", lang)} ${e.localizedMessage ?: AppStrings.get("import_invalid_data", lang)}")
        }
    }

    // ==========================================
    // Room Local Database Management Functions
    // ==========================================

    fun insertLeaveRecord(leave: LeaveRecord) {
        viewModelScope.launch {
            repository.insertLeave(leave)
        }
    }

    fun updateLeaveRecord(leave: LeaveRecord) {
        viewModelScope.launch {
            repository.updateLeave(leave)
        }
    }

    fun deleteLeaveRecord(leave: LeaveRecord) {
        viewModelScope.launch {
            repository.deleteLeave(leave)
        }
    }

    fun deleteLeaveRecordById(id: Long) {
        viewModelScope.launch {
            repository.deleteLeaveById(id)
        }
    }

    fun deletePersonnelFromRoom(personnel: Personnel) {
        viewModelScope.launch {
            repository.delete(personnel)
        }
    }

    fun clearAllRoomData() {
        viewModelScope.launch {
            repository.clearPersonnel()
            repository.clearLeaves()
        }
    }

    fun syncLeavesFromPersonnel() {
        viewModelScope.launch {
            val list = allPersonnel.value
            val currentLeaves = allLeaves.value
            val existingIds = currentLeaves.map { it.personnelId }.toSet()

            val lang = currentLanguage.value
            val newLeaves = mutableListOf<LeaveRecord>()
            for (p in list) {
                // Generate a leave entry for each personnel in leave or with duty return
                val dur = p.totalLeaveDays
                val statusStr = p.status.getCompactBadgeLabel(lang)
                newLeaves.add(
                    LeaveRecord(
                        personnelId = p.id,
                        personnelName = p.fullName,
                        rank = p.rank,
                        registrationNumber = p.registrationNumber,
                        leaveType = p.leaveType,
                        startDateMillis = p.startDateMillis,
                        returnDateMillis = p.returnDateMillis,
                        durationDays = dur,
                        statusText = statusStr,
                        destinationWilaya = p.wilaya,
                        notes = p.notes
                    )
                )
            }
            if (newLeaves.isNotEmpty()) {
                repository.insertAllLeaves(newLeaves)
            }
        }
    }

    fun seedRoomWithDefaults() {
        viewModelScope.launch {
            fun getOffsetDateMillis(daysOffset: Int): Long {
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_YEAR, daysOffset)
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)
                return c.timeInMillis
            }

            val seedP = listOf(
                Personnel(
                    fullName = "عبد الرحمن بلقاسمي",
                    rank = "ملازم أول - Lieutenant",
                    registrationNumber = "DZ-160492",
                    startDateMillis = getOffsetDateMillis(-7),
                    returnDateMillis = getOffsetDateMillis(3),
                    isReturnedToDuty = false,
                    leaveType = "عطلة سنوية",
                    wilaya = "16 - الجزائر - Alger",
                    notes = "الكتيبة الأولى - السرية الثانية"
                ),
                Personnel(
                    fullName = "محمد الأمين يحياوي",
                    rank = "مساعد رئيسي - Adjudant-Major",
                    registrationNumber = "DZ-318920",
                    startDateMillis = getOffsetDateMillis(-4),
                    returnDateMillis = getOffsetDateMillis(6),
                    isReturnedToDuty = false,
                    leaveType = "إجازة",
                    wilaya = "31 - وهران - Oran",
                    notes = "قسم التسيير الإداري"
                ),
                Personnel(
                    fullName = "خالد بن عيسى الدراجي",
                    rank = "رقيب أول - Sergent-Chef",
                    registrationNumber = "DZ-254100",
                    startDateMillis = getOffsetDateMillis(-10),
                    returnDateMillis = getOffsetDateMillis(0),
                    isReturnedToDuty = false,
                    leaveType = "إجازة قصيرة",
                    wilaya = "25 - قسنطينة - Constantine",
                    notes = "سلاح الإشارة والمواصلات"
                ),
                Personnel(
                    fullName = "ياسين بوجمعة صوالحي",
                    rank = "مساعد أول - Adjudant-Chef",
                    registrationNumber = "DZ-057812",
                    startDateMillis = getOffsetDateMillis(-12),
                    returnDateMillis = getOffsetDateMillis(-2),
                    isReturnedToDuty = false,
                    leaveType = "نقاهة",
                    wilaya = "05 - باتنة - Batna",
                    notes = "فصيل الحراسة والتدخل"
                ),
                Personnel(
                    fullName = "حمزة بن زروال الماجري",
                    rank = "عريف أول - Caporal-Chef",
                    registrationNumber = "DZ-194321",
                    startDateMillis = getOffsetDateMillis(-45),
                    returnDateMillis = getOffsetDateMillis(-35),
                    isReturnedToDuty = true,
                    leaveType = "عطلة سنوية",
                    wilaya = "19 - سطيف - Sétif",
                    notes = "الإسناد اللوجيستي"
                ),
                Personnel(
                    fullName = "فاروق لعريبي الورتيلاني",
                    rank = "نقيب - Capitaine",
                    registrationNumber = "DZ-098871",
                    startDateMillis = getOffsetDateMillis(3),
                    returnDateMillis = getOffsetDateMillis(13),
                    isReturnedToDuty = false,
                    leaveType = "إجازة",
                    wilaya = "09 - البليدة - Blida",
                    notes = "قيادة الأركان"
                )
            )

            val seedL = listOf(
                LeaveRecord(
                    personnelId = 1,
                    personnelName = "عبد الرحمن بلقاسمي",
                    rank = "ملازم أول - Lieutenant",
                    registrationNumber = "DZ-160492",
                    leaveType = "عطلة سنوية",
                    startDateMillis = getOffsetDateMillis(-7),
                    returnDateMillis = getOffsetDateMillis(3),
                    durationDays = 10,
                    statusText = "جارية (بالبيت)",
                    destinationWilaya = "16 - الجزائر - Alger",
                    notes = "عطلة النصف الأول من السنة"
                ),
                LeaveRecord(
                    personnelId = 2,
                    personnelName = "محمد الأمين يحياوي",
                    rank = "مساعد رئيسي - Adjudant-Major",
                    registrationNumber = "DZ-318920",
                    leaveType = "إجازة",
                    startDateMillis = getOffsetDateMillis(-4),
                    returnDateMillis = getOffsetDateMillis(6),
                    durationDays = 10,
                    statusText = "جارية (بالبيت)",
                    destinationWilaya = "31 - وهران - Oran",
                    notes = "إجازة عادية"
                ),
                LeaveRecord(
                    personnelId = 3,
                    personnelName = "خالد بن عيسى الدراجي",
                    rank = "رقيب أول - Sergent-Chef",
                    registrationNumber = "DZ-254100",
                    leaveType = "إجازة قصيرة",
                    startDateMillis = getOffsetDateMillis(-10),
                    returnDateMillis = getOffsetDateMillis(0),
                    durationDays = 10,
                    statusText = "الدخول اليوم",
                    destinationWilaya = "25 - قسنطينة - Constantine",
                    notes = "إجازة مستحقة"
                ),
                LeaveRecord(
                    personnelId = 4,
                    personnelName = "ياسين بوجمعة صوالحي",
                    rank = "مساعد أول - Adjudant-Chef",
                    registrationNumber = "DZ-057812",
                    leaveType = "نقاهة",
                    startDateMillis = getOffsetDateMillis(-12),
                    returnDateMillis = getOffsetDateMillis(-2),
                    durationDays = 10,
                    statusText = "متأخر عن الدخول",
                    destinationWilaya = "05 - باتنة - Batna",
                    notes = "فترة نقاهة طبية"
                ),
                LeaveRecord(
                    personnelId = 5,
                    personnelName = "حمزة بن زروال الماجري",
                    rank = "عريف أول - Caporal-Chef",
                    registrationNumber = "DZ-194321",
                    leaveType = "عطلة سنوية",
                    startDateMillis = getOffsetDateMillis(-45),
                    returnDateMillis = getOffsetDateMillis(-35),
                    durationDays = 10,
                    statusText = "مكتملة (مستأنف للعمل)",
                    destinationWilaya = "19 - سطيف - Sétif",
                    notes = "استأنف العمل بانتظام"
                ),
                LeaveRecord(
                    personnelId = 6,
                    personnelName = "فاروق لعريبي الورتيلاني",
                    rank = "نقيب - Capitaine",
                    registrationNumber = "DZ-098871",
                    leaveType = "إجازة",
                    startDateMillis = getOffsetDateMillis(3),
                    returnDateMillis = getOffsetDateMillis(13),
                    durationDays = 10,
                    statusText = "مبرمجة قريباً",
                    destinationWilaya = "09 - البليدة - Blida",
                    notes = "موافقة القيادة"
                )
            )

            repository.clearAndReset(seedP, seedL)
        }
    }

    // ==========================================
    // In-App GitHub Update & System Installer
    // ==========================================

    fun checkForAppUpdate(manual: Boolean = true) {
        if (!manual && hasAutoCheckedUpdate) return
        if (!manual) hasAutoCheckedUpdate = true

        val current = _appUpdateState.value
        if (current is AppUpdateState.Downloading || current is AppUpdateState.ReadyToInstall) {
            if (manual) _showUpdateDialog.value = true
            return
        }

        if (manual) {
            _showUpdateDialog.value = true
            _appUpdateState.value = AppUpdateState.Checking
        }

        viewModelScope.launch {
            val result = AppUpdateManager.checkLatestRelease(BuildConfig.VERSION_NAME)
            if (result is AppUpdateState.UpdateAvailable) {
                lastAvailableUpdate = result
                _appUpdateState.value = result
                _showUpdateDialog.value = true
            } else if (manual) {
                _appUpdateState.value = result
            }
        }
    }

    fun startAppUpdateDownload() {
        val available = (_appUpdateState.value as? AppUpdateState.UpdateAvailable)
            ?: lastAvailableUpdate
            ?: return

        updateDownloadJob?.cancel()
        _appUpdateState.value = AppUpdateState.Downloading(
            versionName = available.versionName,
            downloadedBytes = 0L,
            totalBytes = available.fileSizeBytes,
            progress = 0f
        )

        val app = getApplication<Application>()
        updateDownloadJob = viewModelScope.launch {
            val result = AppUpdateManager.downloadApkInApp(
                context = app,
                downloadUrl = available.downloadUrl,
                versionName = available.versionName,
                expectedSizeBytes = available.fileSizeBytes,
                onProgress = { downloaded, total, progress ->
                    _appUpdateState.value = AppUpdateState.Downloading(
                        versionName = available.versionName,
                        downloadedBytes = downloaded,
                        totalBytes = total,
                        progress = progress
                    )
                }
            )
            result.fold(
                onSuccess = { apkFile ->
                    val needsPerm = !AppUpdateManager.canInstallPackages(app)
                    _appUpdateState.value = AppUpdateState.ReadyToInstall(
                        versionName = available.versionName,
                        apkFile = apkFile,
                        requiresPermission = needsPerm
                    )
                    if (!needsPerm) {
                        installDownloadedApk()
                    }
                },
                onFailure = { error ->
                    if (error is CancellationException) throw error
                    _appUpdateState.value = AppUpdateState.Error(
                        messageKey = "update_download_failed",
                        detail = error.localizedMessage
                    )
                }
            )
        }
    }

    fun cancelAppUpdateDownload() {
        updateDownloadJob?.cancel()
        updateDownloadJob = null
        _appUpdateState.value = lastAvailableUpdate ?: AppUpdateState.Idle
        if (_appUpdateState.value is AppUpdateState.Idle) {
            _showUpdateDialog.value = false
        }
    }

    fun installDownloadedApk() {
        val ready = _appUpdateState.value as? AppUpdateState.ReadyToInstall ?: return
        val app = getApplication<Application>()
        when (AppUpdateManager.launchSystemPackageInstaller(app, ready.apkFile)) {
            InstallLaunchResult.LAUNCHED_SYSTEM_INSTALLER -> {
                _appUpdateState.value = ready.copy(requiresPermission = false)
            }
            InstallLaunchResult.PERMISSION_REQUIRED -> {
                _appUpdateState.value = ready.copy(requiresPermission = true)
                AppUpdateManager.openInstallPermissionSettings(app)
            }
            InstallLaunchResult.FAILED -> {
                _appUpdateState.value = AppUpdateState.Error(messageKey = "update_install_failed")
            }
        }
    }

    fun refreshInstallPermissionState() {
        val ready = _appUpdateState.value as? AppUpdateState.ReadyToInstall ?: return
        val app = getApplication<Application>()
        if (ready.requiresPermission && AppUpdateManager.canInstallPackages(app)) {
            _appUpdateState.value = ready.copy(requiresPermission = false)
            installDownloadedApk()
        }
    }

    fun dismissUpdateDialog() {
        _showUpdateDialog.value = false
    }
}
