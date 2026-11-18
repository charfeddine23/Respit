package com.example

import android.content.Context
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.data.SettingsManager
import org.junit.Assert.assertEquals
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

            // 1. Arabic (RTL): Menu button sits on the RIGHT side, Notifications on the LEFT side
            val menuBoundsAr = composeRule.onNodeWithTag("header_menu_button").getBoundsInRoot()
            val notifBoundsAr = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected menu button (${menuBoundsAr.left}) to be right of notifications (${notifBoundsAr.right}) in Arabic",
                menuBoundsAr.left > notifBoundsAr.right
            )

            // 2. Swiping left or right on the screen must NOT open the settings drawer
            val initialDrawerBounds = composeRule.onNodeWithTag("side_navigation_drawer", useUnmergedTree = true).getBoundsInRoot()

            composeRule.onNodeWithTag("main_scaffold").performTouchInput { swipeRight() }
            composeRule.waitForIdle()
            val afterSwipeRightBounds = composeRule.onNodeWithTag("side_navigation_drawer", useUnmergedTree = true).getBoundsInRoot()
            assertEquals(initialDrawerBounds.left.value, afterSwipeRightBounds.left.value, 1f)

            composeRule.onNodeWithTag("main_scaffold").performTouchInput { swipeLeft() }
            composeRule.waitForIdle()
            val afterSwipeLeftBounds = composeRule.onNodeWithTag("side_navigation_drawer", useUnmergedTree = true).getBoundsInRoot()
            assertEquals(initialDrawerBounds.left.value, afterSwipeLeftBounds.left.value, 1f)

            // 3. Clicking the Menu button opens the drawer from the right side in Arabic
            composeRule.onNodeWithTag("header_menu_button").performClick()
            composeRule.waitForIdle()
            val openedDrawerBoundsAr = composeRule.onNodeWithTag("side_navigation_drawer", useUnmergedTree = true).getBoundsInRoot()
            val rootBounds = composeRule.onNodeWithTag("main_scaffold").getBoundsInRoot()
            assertTrue(
                "Expected drawer to slide on-screen on right side (${openedDrawerBoundsAr.left} < ${rootBounds.right})",
                openedDrawerBoundsAr.left < rootBounds.right - 50.dp
            )

            // 4. Tap the dedicated Language button below the app container to open Language Settings and switch to English -> Top Banner immediately flips to LTR
            composeRule.onNodeWithTag("settings_nav_language").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("lang_option_en").performClick()
            composeRule.waitForIdle()

            val menuBoundsEn = composeRule.onNodeWithTag("header_menu_button").getBoundsInRoot()
            val notifBoundsEn = composeRule.onNodeWithTag("header_notifications_button").getBoundsInRoot()
            assertTrue(
                "Expected menu button (${menuBoundsEn.right}) to be left of notifications (${notifBoundsEn.left}) in English",
                menuBoundsEn.right < notifBoundsEn.left
            )
        }

        settings.language = "ar"
    }
}
