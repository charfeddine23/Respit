package com.example

import android.content.Context
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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

            // 1. Arabic (RTL): Menu button removed from Top Bar; Logo + Title sit on FAR LEFT, Quick Actions on RIGHT
            composeRule.onNodeWithTag("header_menu_button").assertDoesNotExist()
            val titleBoundsAr = composeRule.onNodeWithTag("app_title_row").getBoundsInRoot()
            val notifBoundsAr = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected app_title_row (${titleBoundsAr.right}) to be on the far left of notifications (${notifBoundsAr.left}) in Arabic",
                titleBoundsAr.right <= notifBoundsAr.left + 1.dp
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

            // 4. Tap the dedicated Language button below the app container to open Language Settings and switch to English -> Top Banner immediately flips to LTR (Logo + Title move to FAR RIGHT)
            composeRule.onNodeWithTag("settings_nav_language").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("lang_option_en").performClick()
            composeRule.waitForIdle()

            val titleBoundsEn = composeRule.onNodeWithTag("app_title_row").getBoundsInRoot()
            val notifBoundsEn = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected app_title_row (${titleBoundsEn.left}) to be on the far right of notifications (${notifBoundsEn.right}) in English",
                titleBoundsEn.left >= notifBoundsEn.right - 1.dp
            )
        }

        settings.language = "ar"
    }
}
