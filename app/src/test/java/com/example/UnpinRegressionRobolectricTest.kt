package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Personnel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UnpinRegressionRobolectricTest {

  @get:Rule
  val composeTestRule = createEmptyComposeRule()

  private fun getOffsetDateMillis(daysOffset: Int): Long {
    val c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_YEAR, daysOffset)
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c.timeInMillis
  }

  @Test
  fun `unpinning a 6th-ranked personnel on Dashboard keeps them visible in the list and active in DB`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val dbFile = context.getDatabasePath("personnel_leaves_db")
    dbFile.parentFile?.mkdirs()
    if (dbFile.exists()) dbFile.delete()

    val db = AppDatabase.getDatabase(context)
    runBlocking {
      val dao = db.personnelDao()
      for (i in 1..5) {
        dao.insert(
          Personnel(
            id = i.toLong(),
            fullName = "فرد رقم $i",
            fullNameLatin = "Person $i",
            rank = "نقيب - Capitaine",
            registrationNumber = "DZ-100$i",
            startDateMillis = getOffsetDateMillis(-10),
            returnDateMillis = getOffsetDateMillis(i),
            isReturnedToDuty = false,
            isPinned = false
          )
        )
      }
      // 6th person is on duty (naturally sorted 6th after the 5 on leave), initially pinned to top
      dao.insert(
        Personnel(
          id = 6L,
          fullName = "فؤاد السادس",
          fullNameLatin = "Fouad Six",
          rank = "عريف - Caporal",
          registrationNumber = "DZ-6006",
          startDateMillis = getOffsetDateMillis(-30),
          returnDateMillis = getOffsetDateMillis(-20),
          isReturnedToDuty = true,
          isPinned = true,
          pinnedAtMillis = 10_000L
        )
      )
    }

    ActivityScenario.launch(MainActivity::class.java).use { scenario ->
      composeTestRule.waitForIdle()
      assertEquals(Lifecycle.State.RESUMED, scenario.state)

      composeTestRule.waitUntil(timeoutMillis = 5_000L) {
        composeTestRule.onAllNodesWithTag("dashboard_lazy_column").fetchSemanticsNodes().isNotEmpty()
      }

      // Unpinned personnel (1..5) must NOT appear on Dashboard; only pinned person 6 appears
      composeTestRule.onNodeWithTag("dashboard_lazy_column")
        .performScrollToNode(hasTestTag("personnel_item_6"))
      assertEquals(0, composeTestRule.onAllNodesWithTag("personnel_item_1").fetchSemanticsNodes().size)
      composeTestRule.waitUntil(timeoutMillis = 5_000L) {
        composeTestRule.onAllNodesWithTag("pinned_badge_6").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithTag("dashboard_lazy_column")
        .performScrollToNode(hasTestTag("pinned_badge_6"))
      composeTestRule.onNodeWithTag("pinned_badge_6").performClick()

      // Wait until DB updates and pin badge disappears
      composeTestRule.waitUntil(timeoutMillis = 5_000L) {
        composeTestRule.onAllNodesWithTag("pinned_badge_6").fetchSemanticsNodes().isEmpty()
      }

      // Dashboard now shows ONLY pinned persons: unpinning person 6 removes them from Dashboard
      composeTestRule.onNodeWithTag("dashboard_lazy_column")
        .performScrollToNode(hasTestTag("dashboard_pinned_empty_card"))
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("dashboard_pinned_empty_card").assertIsDisplayed()

      // Navigating to Personnel tab confirms person 6 is still visible in all records and active in DB
      composeTestRule.onNodeWithTag("bottom_nav_records").performClick()
      composeTestRule.waitUntil(timeoutMillis = 5_000L) {
        composeTestRule.onAllNodesWithTag("records_lazy_column").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithTag("records_lazy_column")
        .performScrollToNode(hasTestTag("personnel_item_6"))
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("personnel_item_6").assertIsDisplayed()
      composeTestRule.onNodeWithText("فؤاد السادس").assertIsDisplayed()

      val active = runBlocking { db.personnelDao().getAllPersonnel().first() }
      assertEquals(6, active.size)
      val person6 = active.first { it.id == 6L }
      assertFalse(person6.isPinned)
      assertFalse(person6.isDeleted)
    }
  }
}
