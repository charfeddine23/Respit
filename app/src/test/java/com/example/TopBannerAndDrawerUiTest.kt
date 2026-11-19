package com.example

import android.content.Context
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.data.SettingsManager
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TopBannerAndDrawerUiTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun `arabic mirrors top banner RTL disables left right swipe to open settings and english mirrors back LTR`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val settings = SettingsManager(context)
        settings.language = "ar"

        ActivityScenario.launch(MainActivity::class.java).use {
            composeRule.waitForIdle()

            // 1. Arabic (RTL): Menu button removed; Liquid Glass top_nav_panel renders Left->Right: [Logo + ريسبيت] ... [Theme] [Notifications (Far Right)]
            composeRule.onNodeWithTag("top_nav_panel").assertExists()
            composeRule.onNodeWithTag("header_menu_button").assertDoesNotExist()
            composeRule.onNodeWithText("ريسبيت").assertExists()
            val titleBoundsAr = composeRule.onNodeWithTag("app_title_row").getBoundsInRoot()
            val themeBoundsAr = composeRule.onNodeWithTag("header_theme_toggle").getBoundsInRoot()
            val notifBoundsAr = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected app_title_row (${titleBoundsAr.right}) left of theme (${themeBoundsAr.left}) in Arabic",
                titleBoundsAr.right <= themeBoundsAr.left + 1.dp
            )
            assertTrue(
                "Expected theme (${themeBoundsAr.right}) left of notifications (${notifBoundsAr.left}) in Arabic",
                themeBoundsAr.right <= notifBoundsAr.left + 1.dp
            )

            // 2. Dashboard StatisticsSection shows Analytics button in place of "Registered in system"
            composeRule.onNodeWithTag("dashboard_analytics_button").assertExists().performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("analytics_dashboard_screen").assertExists()
            composeRule.onNodeWithTag("analytics_back_button").performClick()
            composeRule.waitForIdle()

            // 3. Settings is now accessed via the bottom dock 4th tab (bottom_nav_settings)
            composeRule.onNodeWithTag("bottom_nav_settings").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("side_navigation_drawer").assertExists()

            // 4. Open Language Settings, scroll to English, and switch -> Top Banner mirrors to LTR: [Notifications (Far Left)] [Theme] ... [Respite + Logo]
            composeRule.onNodeWithTag("settings_nav_language").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("lang_option_en").performScrollTo().performClick()
            composeRule.waitForIdle()

            composeRule.onNodeWithText("Respite").assertExists()
            val titleBoundsEn = composeRule.onNodeWithTag("app_title_row").getBoundsInRoot()
            val themeBoundsEn = composeRule.onNodeWithTag("header_theme_toggle").getBoundsInRoot()
            val notifBoundsEn = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected notifications (${notifBoundsEn.right}) left of theme (${themeBoundsEn.left}) in English",
                notifBoundsEn.right <= themeBoundsEn.left + 1.dp
            )
            assertTrue(
                "Expected theme (${themeBoundsEn.right}) left of app_title_row (${titleBoundsEn.left}) in English",
                themeBoundsEn.right <= titleBoundsEn.left + 1.dp
            )
        }

        settings.language = "ar"
    }
}
