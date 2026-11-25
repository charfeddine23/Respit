package com.example

import android.content.Context
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
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

            // 1. Arabic (RTL): Menu button removed; Top Bar places Logo+Title on Start (Right) and [Theme] [Notifications] on End (Left).
            // Left->Right physical order in Arabic: [Theme] [Notifications] ... [Logo + ريسبيت]
            composeRule.onNodeWithTag("top_bar_glass_capsule").assertExists()
            composeRule.onNodeWithTag("header_menu_button").assertDoesNotExist()
            composeRule.onNodeWithText("ريسبيت").assertExists()
            val logoBoundsAr = composeRule.onNodeWithTag("top_bar_app_logo", useUnmergedTree = true).getBoundsInRoot()
            assertTrue(
                "Expected enlarged top bar logo width >= 50.dp (was ${logoBoundsAr.right - logoBoundsAr.left})",
                (logoBoundsAr.right - logoBoundsAr.left) >= 50.dp
            )
            val titleBoundsAr = composeRule.onNodeWithTag("app_title_row").getBoundsInRoot()
            val themeBoundsAr = composeRule.onNodeWithTag("header_theme_toggle").getBoundsInRoot()
            val notifBoundsAr = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected theme (${themeBoundsAr.right}) left of notifications (${notifBoundsAr.left}) in Arabic",
                themeBoundsAr.right <= notifBoundsAr.left + 1.dp
            )
            assertTrue(
                "Expected notifications (${notifBoundsAr.right}) left of app_title_row (${titleBoundsAr.left}) in Arabic",
                notifBoundsAr.right <= titleBoundsAr.left + 1.dp
            )

            // 2. Dashboard StatisticsSection shows Analytics button
            composeRule.onNodeWithTag("dashboard_analytics_button").assertExists().performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("analytics_dashboard_screen").assertExists()
            composeRule.onNodeWithTag("analytics_back_button").performClick()
            composeRule.waitForIdle()

            // 3. Settings is accessed via the bottom dock 4th tab (bottom_nav_settings)
            composeRule.onNodeWithTag("bottom_nav_settings").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("side_navigation_drawer").assertExists()

            // 4. Open Language Settings, scroll to English, and switch -> Top Bar mirrors to LTR: [Logo + Respite (Left)] ... [Notifications] [Theme (Right)]
            composeRule.onNodeWithTag("settings_nav_language").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("lang_option_en").performScrollTo().performClick()
            composeRule.waitForIdle()

            composeRule.onNodeWithText("Respite").assertExists()
            val titleBoundsEn = composeRule.onNodeWithTag("app_title_row").getBoundsInRoot()
            val themeBoundsEn = composeRule.onNodeWithTag("header_theme_toggle").getBoundsInRoot()
            val notifBoundsEn = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected app_title_row (${titleBoundsEn.right}) left of notifications (${notifBoundsEn.left}) in English",
                titleBoundsEn.right <= notifBoundsEn.left + 1.dp
            )
            assertTrue(
                "Expected notifications (${notifBoundsEn.right}) left of theme (${themeBoundsEn.left}) in English",
                notifBoundsEn.right <= themeBoundsEn.left + 1.dp
            )

            // 5. Return to Dashboard and verify Pinned Persons section header renders in English
            composeRule.onNodeWithTag("bottom_nav_dashboard").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("dashboard_lazy_column")
                .performScrollToNode(hasTestTag("dashboard_pinned_persons_header"))
            composeRule.waitForIdle()
            composeRule.onNodeWithText("Pinned Persons").assertExists()
        }

        settings.language = "ar"
    }
}
