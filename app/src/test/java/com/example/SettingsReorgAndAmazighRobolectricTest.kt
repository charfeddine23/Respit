package com.example

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
import com.example.data.LeaveRecord
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.DashboardStats
import com.example.ui.UndoAction
import com.example.ui.components.SideNavigationDrawerContent
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import com.example.util.AppStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsReorgAndAmazighRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `AppLanguage AMAZIGH resolves codes localization rank wilaya leaveType and personnel names`() {
        assertEquals(AppLanguage.AMAZIGH, AppLanguage.fromCode("zgh"))
        assertEquals(AppLanguage.AMAZIGH, AppLanguage.fromCode("kab"))
        assertEquals(AppLanguage.AMAZIGH, AppLanguage.fromCode("ber"))
        assertFalse(AppLanguage.AMAZIGH.isRtl)
        assertEquals("Tamaziɣt (DZ)", AppLanguage.AMAZIGH.displayName)

        assertEquals("Tutlayt", AppStrings.get("settings_cat_language", AppLanguage.AMAZIGH))
        assertEquals("Ugar", AppStrings.get("settings_cat_more", AppLanguage.AMAZIGH))
        assertEquals("Asteɛfu aseggasan", formatLeaveType("عطلة سنوية", AppLanguage.AMAZIGH))
        assertEquals("Capitaine", formatRank("نقيب - Capitaine", AppLanguage.AMAZIGH))
        assertEquals("Alger", formatWilaya("16 - الجزائر - Alger", AppLanguage.AMAZIGH))

        val person = Personnel(
            fullName = "كريم بن علي",
            fullNameLatin = "Karim Benali",
            rank = "نقيب - Capitaine",
            registrationNumber = "DZ-99",
            startDateMillis = 0L,
            returnDateMillis = 0L
        )
        assertEquals("Karim Benali", person.getLocalizedName(AppLanguage.AMAZIGH))

        val record = LeaveRecord(
            personnelName = "كريم بن علي",
            personnelNameLatin = "Karim Benali",
            rank = "نقيب - Capitaine",
            registrationNumber = "DZ-99",
            leaveType = "عطلة سنوية",
            startDateMillis = 0L,
            returnDateMillis = 0L,
            durationDays = 10L
        )
        assertEquals("Karim Benali", record.getDisplayName(AppLanguage.AMAZIGH))
    }

    @Test
    fun `drawer home hides version shows ordered 7 categories and opens all subpages cleanly`() {
        var selectedLang = AppLanguage.ENGLISH
        var trashOpened = false

        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                SideNavigationDrawerContent(
                    currentLang = AppLanguage.ENGLISH,
                    isDarkMode = false,
                    lastUndo = UndoAction.Deleted(
                        Personnel(
                            id = 1L,
                            fullName = "يوسف",
                            fullNameLatin = "Youssef",
                            rank = "رائد - Commandant",
                            registrationNumber = "DZ-101",
                            startDateMillis = 0L,
                            returnDateMillis = 0L
                        )
                    ),
                    stats = DashboardStats(total = 5, atHome = 2, returningToday = 1, overdue = 1),
                    onSelectLanguage = { selectedLang = it },
                    onUndo = {},
                    onCheckNotifications = {},
                    onExportPdf = {},
                    onOpenBackup = {},
                    onOpenRoomDatabase = {},
                    onToggleTheme = {},
                    onOpenDeveloper = {},
                    onResetSampleData = {},
                    currentThemeMode = AppThemeMode.LIGHT,
                    currentThemeStyle = AppThemeStyle.GLASSMORPHISM,
                    isBiometricEnabled = true,
                    trashedCount = 3,
                    onOpenTrashBin = { trashOpened = true }
                )
            }
        }

        composeRule.waitForIdle()

        // 1. Header card exists & NO version number shown underneath App Name
        composeRule.onNodeWithTag("drawer_app_header_card").assertIsDisplayed()
        composeRule.onNodeWithText("Respite Pulse").assertIsDisplayed()
        composeRule.onNodeWithText("Version:", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText(AppStrings.get("app_version", AppLanguage.ENGLISH)).assertDoesNotExist()

        // 2. Ordered 7 main buttons in Settings Home
        val langBounds = composeRule.onNodeWithTag("drawer_language_item").getBoundsInRoot()
        val appBounds = composeRule.onNodeWithTag("drawer_appearance_item").getBoundsInRoot()
        val secBounds = composeRule.onNodeWithTag("drawer_security_item").getBoundsInRoot()
        val notifBounds = composeRule.onNodeWithTag("drawer_notifications_item").getBoundsInRoot()
        val trashBounds = composeRule.onNodeWithTag("drawer_trash_bin_item").getBoundsInRoot()
        val backupBounds = composeRule.onNodeWithTag("drawer_backup_restore_item").getBoundsInRoot()
        composeRule.onNodeWithTag("drawer_more_item").performScrollTo()
        composeRule.waitForIdle()
        val moreBounds = composeRule.onNodeWithTag("drawer_more_item").getBoundsInRoot()

        assertTrue(langBounds.bottom <= appBounds.top + 1.dp)
        assertTrue(appBounds.bottom <= secBounds.top + 1.dp)
        assertTrue(secBounds.bottom <= notifBounds.top + 1.dp)
        assertTrue(notifBounds.bottom <= trashBounds.top + 1.dp)
        assertTrue(trashBounds.bottom <= backupBounds.top + 1.dp)
        assertTrue(backupBounds.bottom <= moreBounds.top + 1.dp)

        // 3. Language subpage & Algerian Amazigh selection
        composeRule.onNodeWithTag("drawer_language_item").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer_language_page").assertIsDisplayed()
        composeRule.onNodeWithTag("lang_option_zgh").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        assertEquals(AppLanguage.AMAZIGH, selectedLang)
        composeRule.onNodeWithTag("drawer_settings_back_button").performClick()
        composeRule.waitForIdle()

        // 4. Appearance subpage contains all 7 appearance items
        composeRule.onNodeWithTag("drawer_appearance_item").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer_appearance_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_theme_toggle").assertIsDisplayed()
        composeRule.onNodeWithText(AppStrings.get("theme_title", AppLanguage.ENGLISH)).assertIsDisplayed()
        composeRule.onNodeWithText(AppStrings.get("theme_style_title", AppLanguage.ENGLISH)).assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_theme_engine_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_dynamic_animations_toggle").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_living_luxury_toggle").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_focus_mode_toggle").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_settings_back_button").performClick()
        composeRule.waitForIdle()

        // 5. Security subpage
        composeRule.onNodeWithTag("drawer_security_item").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer_security_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_biometric_lock_toggle").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_lock_now_button").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_no_screenshot_toggle").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_settings_back_button").performClick()
        composeRule.waitForIdle()

        // 6. Notifications subpage
        composeRule.onNodeWithTag("drawer_notifications_item").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer_notifications_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_threshold_slider").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_check_notifications_btn").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_settings_back_button").performClick()
        composeRule.waitForIdle()

        // 7. Backup & Restore subpage
        composeRule.onNodeWithTag("drawer_backup_restore_item").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer_backup_restore_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_backup_item").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_pdf_item").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_room_db_item").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_drive_sync_toggle").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drive_backup_btn").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drive_restore_btn").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_settings_back_button").performClick()
        composeRule.waitForIdle()

        // 8. More subpage contains all remaining features
        composeRule.onNodeWithTag("drawer_more_item").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer_more_page").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_undo_item").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_update_item").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_developer_item").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_reset_item").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_developer_whatsapp_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_settings_back_button").performClick()
        composeRule.waitForIdle()

        // 9. Trash Bin click
        composeRule.onNodeWithTag("drawer_trash_bin_item").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(trashOpened)
    }
}
