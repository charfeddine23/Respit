package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class AppThemeMode(val code: String, val titleAr: String) {
    LIGHT("light", "الوضع النهاري (افتراضي)"),
    DARK("dark", "الوضع الليلي");

    companion object {
        fun fromCode(code: String): AppThemeMode {
            return entries.find { it.code == code } ?: LIGHT
        }
    }
}

enum class AppThemeStyle(val code: String, val titleAr: String) {
    GLASSMORPHISM("glassmorphism", "الزجاج الشفاف (Glassmorphism)"),
    NEUMORPHISM("neumorphism", "النيومورفيزم (Neumorphism)");

    companion object {
        fun fromCode(code: String): AppThemeStyle {
            return entries.find { it.code == code } ?: GLASSMORPHISM
        }
    }
}

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_THEME_STYLE = "pref_theme_style"
        private const val KEY_DARK_MODE = "pref_dark_mode"
        private const val KEY_DUTY_THRESHOLD = "pref_duty_threshold_days"
        private const val KEY_PIN_ENABLED = "pref_pin_enabled"
        private const val KEY_PIN_CODE = "pref_pin_code"
        private const val KEY_SECURITY_LOCK_TYPE = "pref_security_lock_type"
        private const val KEY_PATTERN_CODE = "pref_pattern_code"
        private const val KEY_BIOMETRIC_ENABLED = "pref_biometric_enabled"
        private const val KEY_FINGERPRINT_ENABLED = "pref_fingerprint_enabled"
        private const val KEY_PASSKEY_ENABLED = "pref_passkey_enabled"
        private const val KEY_LANGUAGE = "pref_language"
        private const val KEY_PINNED_NOTIFICATIONS = "pref_pinned_notifications"
        private const val KEY_DISMISSED_NOTIFICATIONS = "pref_dismissed_notifications"
        private const val KEY_FOCUS_MODE = "pref_focus_mode"
        private const val KEY_DRIVE_SYNC_ENABLED = "pref_drive_sync_enabled"
        private const val KEY_LAST_DRIVE_BACKUP = "pref_last_drive_backup_time"
        private const val KEY_DYNAMIC_ANIMATIONS = "pref_dynamic_animations_enabled"
        private const val KEY_LIVING_LUXURY_MODE = "pref_living_luxury_mode"
        private const val KEY_NO_SCREENSHOT_ENABLED = "pref_no_screenshot_enabled"
        private const val KEY_THEME_ENGINE_ID = "pref_theme_engine_id"
        private const val KEY_FIT_TO_SCREEN = "pref_fit_to_screen_enabled"
        const val DEFAULT_DUTY_THRESHOLD = 45
    }

    private fun safeGetString(key: String, default: String?): String? =
        try { prefs.getString(key, default) } catch (_: ClassCastException) { default }

    private fun safeGetBoolean(key: String, default: Boolean): Boolean =
        try { prefs.getBoolean(key, default) } catch (_: ClassCastException) { default }

    private fun safeGetInt(key: String, default: Int): Int =
        try { prefs.getInt(key, default) } catch (_: ClassCastException) { default }

    private fun safeGetLong(key: String, default: Long): Long =
        try { prefs.getLong(key, default) } catch (_: ClassCastException) { default }

    private fun safeGetStringSet(key: String, default: Set<String>): Set<String> =
        try { prefs.getStringSet(key, default) ?: default } catch (_: ClassCastException) { default }

    var themeMode: AppThemeMode
        get() {
            val code = safeGetString(KEY_THEME_MODE, null)
            return if (code != null) {
                AppThemeMode.fromCode(code)
            } else {
                // Default to LIGHT mode unless explicitly saved as dark mode
                if (prefs.contains(KEY_DARK_MODE)) {
                    if (safeGetBoolean(KEY_DARK_MODE, false)) AppThemeMode.DARK else AppThemeMode.LIGHT
                } else {
                    AppThemeMode.LIGHT
                }
            }
        }
        set(value) {
            prefs.edit()
                .putString(KEY_THEME_MODE, value.code)
                .putBoolean(KEY_DARK_MODE, value == AppThemeMode.DARK)
                .apply()
        }

    var isDarkMode: Boolean
        get() = themeMode == AppThemeMode.DARK
        set(value) {
            themeMode = if (value) AppThemeMode.DARK else AppThemeMode.LIGHT
        }

    var dutyThresholdDays: Int
        get() = safeGetInt(KEY_DUTY_THRESHOLD, DEFAULT_DUTY_THRESHOLD)
        set(value) = prefs.edit().putInt(KEY_DUTY_THRESHOLD, value).apply()

    var themeStyle: AppThemeStyle
        get() {
            val code = safeGetString(KEY_THEME_STYLE, null)
            return if (code != null) AppThemeStyle.fromCode(code) else AppThemeStyle.NEUMORPHISM
        }
        set(value) {
            prefs.edit().putString(KEY_THEME_STYLE, value.code).apply()
        }

    var selectedThemeEngineId: String
        get() = safeGetString(KEY_THEME_ENGINE_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_THEME_ENGINE_ID, value).apply()

    var isPinEnabled: Boolean
        get() = safeGetBoolean(KEY_PIN_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_PIN_ENABLED, value).apply()

    var pinCode: String
        get() = safeGetString(KEY_PIN_CODE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()

    var securityLockType: String
        get() = safeGetString(KEY_SECURITY_LOCK_TYPE, "PIN") ?: "PIN"
        set(value) = prefs.edit().putString(KEY_SECURITY_LOCK_TYPE, value).apply()

    var patternCode: String
        get() = safeGetString(KEY_PATTERN_CODE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PATTERN_CODE, value).apply()

    var isBiometricEnabled: Boolean
        get() = safeGetBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var isFingerprintEnabled: Boolean
        get() = safeGetBoolean(KEY_FINGERPRINT_ENABLED, false) || safeGetBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) {
            prefs.edit()
                .putBoolean(KEY_FINGERPRINT_ENABLED, value)
                .putBoolean(KEY_BIOMETRIC_ENABLED, value)
                .apply()
        }

    var isPasskeyEnabled: Boolean
        get() = safeGetBoolean(KEY_PASSKEY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_PASSKEY_ENABLED, value).apply()

    var language: String
        get() = safeGetString(KEY_LANGUAGE, "ar") ?: "ar"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var pinnedNotificationIds: Set<String>
        get() = safeGetStringSet(KEY_PINNED_NOTIFICATIONS, emptySet()).toSet()
        set(value) = prefs.edit().putStringSet(KEY_PINNED_NOTIFICATIONS, value).apply()

    var dismissedNotificationIds: Set<String>
        get() = safeGetStringSet(KEY_DISMISSED_NOTIFICATIONS, emptySet()).toSet()
        set(value) = prefs.edit().putStringSet(KEY_DISMISSED_NOTIFICATIONS, value).apply()

    var isFocusMode: Boolean
        get() = safeGetBoolean(KEY_FOCUS_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_FOCUS_MODE, value).apply()

    var isGoogleDriveSyncEnabled: Boolean
        get() = safeGetBoolean(KEY_DRIVE_SYNC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_DRIVE_SYNC_ENABLED, value).apply()

    var lastGoogleDriveBackupTime: Long
        get() = safeGetLong(KEY_LAST_DRIVE_BACKUP, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_DRIVE_BACKUP, value).apply()

    var isDynamicAnimationsEnabled: Boolean
        get() = safeGetBoolean(KEY_DYNAMIC_ANIMATIONS, true) // default true for lively UI
        set(value) = prefs.edit().putBoolean(KEY_DYNAMIC_ANIMATIONS, value).apply()

    var isLivingLuxuryMode: Boolean
        get() = safeGetBoolean(KEY_LIVING_LUXURY_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_LIVING_LUXURY_MODE, value).apply()

    var isNoScreenshotEnabled: Boolean
        get() = safeGetBoolean(KEY_NO_SCREENSHOT_ENABLED, false) // Disabled by default; toggle in Security Settings
        set(value) = prefs.edit().putBoolean(KEY_NO_SCREENSHOT_ENABLED, value).apply()

    var isFitToScreenEnabled: Boolean
        get() = safeGetBoolean(KEY_FIT_TO_SCREEN, false)
        set(value) = prefs.edit().putBoolean(KEY_FIT_TO_SCREEN, value).apply()

    var recentSearches: List<String>
        get() {
            val raw = safeGetString("pref_recent_searches", null) ?: return emptyList()
            return try {
                val array = org.json.JSONArray(raw)
                val list = mutableListOf<String>()
                for (i in 0 until array.length()) {
                    val item = array.optString(i, "")
                    if (item.isNotBlank()) list.add(item)
                }
                list.take(5)
            } catch (e: Exception) {
                emptyList()
            }
        }
        set(value) {
            val array = org.json.JSONArray()
            value.take(5).forEach { array.put(it) }
            prefs.edit().putString("pref_recent_searches", array.toString()).apply()
        }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return
        val current = recentSearches.toMutableList()
        current.remove(trimmed)
        current.add(0, trimmed)
        recentSearches = current.take(5)
    }

    fun removeRecentSearch(query: String) {
        val current = recentSearches.toMutableList()
        current.remove(query)
        recentSearches = current
    }

    fun clearRecentSearches() {
        recentSearches = emptyList()
    }
}

