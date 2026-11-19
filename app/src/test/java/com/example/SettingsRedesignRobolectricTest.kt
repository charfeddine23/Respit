package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import com.example.data.AppThemeMode
import com.example.data.AppThemeStyle
import com.example.ui.DashboardStats
import com.example.ui.components.SideNavigationDrawerContent
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsRedesignRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `settings root header omits version opens language with Tamazight and organizes all 6 sections`() {
        var currentLang by mutableStateOf(AppLanguage.ARABIC)
        var isDarkMode by mutableStateOf(false)
        var themeMode by mutableStateOf(AppThemeMode.LIGHT)
        var themeStyle by mutableStateOf(AppThemeStyle.GLASSMORPHISM)
        var isFocusMode by mutableStateOf(false)
        var isDynamicAnimations by mutableStateOf(true)
        var isLivingLuxury by mutableStateOf(false)
        var isFitToScreen by mutableStateOf(false)
        var isBiometric by mutableStateOf(false)
        var isNoScreenshot by mutableStateOf(false)
        var threshold by mutableIntStateOf(45)
        var trashClicked = false
        var excelClicked = false
        var pdfClicked = false
        var backupClicked = false
        var roomDbClicked = false
        var resetClicked = false
        var notificationsChecked = false
        var updateClicked = false
        var developerClicked = false

        composeRule.setContent {
            MyApplicationTheme(darkTheme = isDarkMode) {
                SideNavigationDrawerContent(
                    currentLang = currentLang,
                    isDarkMode = isDarkMode,
                    lastUndo = null,
                    stats = DashboardStats(total = 12, atHome = 3, returningToday = 1, overdue = 1),
                    onSelectLanguage = { currentLang = it },
                    onUndo = {},
                    onCheckNotifications = { notificationsChecked = true },
                    onExportPdf = { pdfClicked = true },
                    onExportExcel = { excelClicked = true },
                    onOpenBackup = { backupClicked = true },
                    onOpenRoomDatabase = { roomDbClicked = true },
                    onToggleTheme = {
                        isDarkMode = !isDarkMode
                        themeMode = if (isDarkMode) AppThemeMode.DARK else AppThemeMode.LIGHT
                    },
                    onOpenDeveloper = { developerClicked = true },
                    onCheckUpdate = { updateClicked = true },
                    onResetSampleData = { resetClicked = true },
                    currentThemeMode = themeMode,
                    onSelectThemeMode = {
                        themeMode = it
                        isDarkMode = (it == AppThemeMode.DARK)
                    },
                    currentThemeStyle = themeStyle,
                    onSelectThemeStyle = { themeStyle = it },
                    isFocusMode = isFocusMode,
                    onToggleFocusMode = { isFocusMode = !isFocusMode },
                    dutyThreshold = threshold,
                    onSaveThreshold = { threshold = it },
                    isBiometricEnabled = isBiometric,
                    onToggleBiometric = { isBiometric = it },
                    isNoScreenshotEnabled = isNoScreenshot,
                    onToggleNoScreenshot = { isNoScreenshot = it },
                    isDynamicAnimationsEnabled = isDynamicAnimations,
                    onToggleDynamicAnimations = { isDynamicAnimations = it },
                    isLivingLuxuryMode = isLivingLuxury,
                    onToggleLivingLuxuryMode = { isLivingLuxury = it },
                    isFitToScreenEnabled = isFitToScreen,
                    onToggleFitToScreen = { isFitToScreen = it },
                    trashedCount = 4,
                    onOpenTrashBin = { trashClicked = true },
                    drawerIsOpen = true
                )
            }
        }

        composeRule.waitForIdle()

        // 1. Top Header shows App Title & NO Version number or Tap-to-open-Language text inside header card
        composeRule.onNodeWithTag("drawer_header_language_card").assertIsDisplayed()
        composeRule.onNodeWithText("ريسبيت").assertIsDisplayed()
        composeRule.onNodeWithText("Version:", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("(${BuildConfig.VERSION_CODE})", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("Tap to open Language Settings", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("اضغط هنا لتغيير لغة التطبيق", substring = true).assertDoesNotExist()

        // Verify Language button is directly below the app header card, followed by the remaining categories
        composeRule.onNodeWithTag("settings_nav_language").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("اللغة").assertIsDisplayed()
        val headerBounds = composeRule.onNodeWithTag("drawer_header_language_card").getBoundsInRoot()
        val langNavBounds = composeRule.onNodeWithTag("settings_nav_language").getBoundsInRoot()
        assertTrue(headerBounds.bottom <= langNavBounds.top + 1.dp)

        composeRule.onNodeWithTag("settings_nav_appearance").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("settings_nav_security").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("settings_nav_notifications").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("settings_nav_trash_bin").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("settings_nav_backup_restore").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_update_item").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_developer_item").performScrollTo().assertIsDisplayed()

        val backupBounds = composeRule.onNodeWithTag("settings_nav_backup_restore").getBoundsInRoot()
        val updateBounds = composeRule.onNodeWithTag("drawer_update_item").getBoundsInRoot()
        val devBounds = composeRule.onNodeWithTag("drawer_developer_item").getBoundsInRoot()
        assertTrue(backupBounds.bottom <= updateBounds.top + 1.dp)
        assertTrue(updateBounds.bottom <= devBounds.top + 1.dp)

        composeRule.onNodeWithTag("drawer_update_item").performScrollTo().performClick()
        composeRule.onNodeWithTag("drawer_developer_item").performScrollTo().performClick()
        assertTrue(updateClicked)
        assertTrue(developerClicked)

        // Tap dedicated Language button below header -> opens Language settings with 4 languages including Tamazight
        composeRule.onNodeWithTag("settings_nav_language").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("settings_language_page").assertIsDisplayed()
        composeRule.onNodeWithTag("lang_option_ar").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("lang_option_tzm").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("lang_option_fr").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("lang_option_en").performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithTag("lang_option_tzm").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(AppLanguage.TAMAZIGHT, currentLang)

        composeRule.onNodeWithTag("lang_option_en").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(AppLanguage.ENGLISH, currentLang)

        composeRule.onNodeWithTag("settings_subpage_back_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        // 2. Second Section - Appearance & Theme
        composeRule.onNodeWithTag("settings_nav_appearance").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("settings_appearance_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_theme_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(isDarkMode)
        composeRule.onNodeWithTag("appearance_style_neumorph_btn").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(AppThemeStyle.NEUMORPHISM, themeStyle)
        composeRule.onNodeWithTag("drawer_theme_engine_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_dynamic_animations_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(false, isDynamicAnimations)
        composeRule.onNodeWithTag("drawer_living_luxury_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(isLivingLuxury)
        composeRule.onNodeWithTag("drawer_focus_mode_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(isFocusMode)
        composeRule.onNodeWithTag("drawer_fit_to_screen_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(isFitToScreen)

        composeRule.onNodeWithTag("settings_subpage_back_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        // 3. Third Section - Security
        composeRule.onNodeWithTag("settings_nav_security").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("settings_security_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_biometric_lock_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(isBiometric)
        composeRule.onNodeWithTag("drawer_lock_now_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_no_screenshot_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(isNoScreenshot)

        composeRule.onNodeWithTag("settings_subpage_back_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        // 4. Fourth Section - Notifications & Threshold
        composeRule.onNodeWithTag("settings_nav_notifications").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("settings_notifications_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_notifications_item").performScrollTo().performClick()
        assertTrue(notificationsChecked)
        composeRule.onNodeWithTag("threshold_slider").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("threshold_preset_60").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(60, threshold)

        composeRule.onNodeWithTag("settings_subpage_back_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        // 5. Fifth Section - Trash Bin
        composeRule.onNodeWithTag("settings_nav_trash_bin").performScrollTo().performClick()
        assertTrue(trashClicked)

        // 6. Sixth Section - Backup, Restore & Tools
        composeRule.onNodeWithTag("settings_nav_backup_restore").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("settings_backup_restore_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_update_item").assertDoesNotExist()
        composeRule.onNodeWithTag("drawer_developer_item").assertDoesNotExist()
        composeRule.onNodeWithTag("drawer_backup_item").performScrollTo().performClick()
        composeRule.onNodeWithTag("drawer_pdf_item").performScrollTo().performClick()
        composeRule.onNodeWithTag("drawer_excel_item").performScrollTo().performClick()
        composeRule.onNodeWithTag("drawer_drive_backup_btn").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_drive_restore_btn").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_room_db_item").performScrollTo().performClick()
        composeRule.onNodeWithTag("drawer_reset_item").performScrollTo().performClick()

        assertTrue(backupClicked)
        assertTrue(pdfClicked)
        assertTrue(excelClicked)
        assertTrue(roomDbClicked)
        assertTrue(resetClicked)
    }
}
