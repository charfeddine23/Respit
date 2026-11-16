package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.Personnel
import com.example.ui.components.PersonnelCard
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ComposeUiRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

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
  fun `pinned PersonnelCard shows pin icon beside name without Pinned on Top text and displays Days Served`() {
    val person = Personnel(
      id = 42L,
      fullName = "عبد القادر بن علي",
      fullNameLatin = "Abdelkader Benali",
      rank = "نقيب - Capitaine",
      registrationNumber = "DZ-424242",
      startDateMillis = getOffsetDateMillis(-16),
      returnDateMillis = getOffsetDateMillis(-6),
      isReturnedToDuty = true,
      isPinned = true,
      pinnedAtMillis = System.currentTimeMillis()
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        PersonnelCard(
          personnel = person,
          dutyThresholdDays = 45,
          onConfirmDuty = {},
          onEdit = {},
          onDelete = {},
          animationsEnabled = false,
          currentLang = AppLanguage.ENGLISH,
          effectiveDaysSinceDuty = 6L
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("pinned_badge_42").assertIsDisplayed()
    composeTestRule.onNodeWithText("Pinned on Top", substring = true).assertDoesNotExist()
    composeTestRule.onNodeWithText("مثبت في الأعلى", substring = true).assertDoesNotExist()
    composeTestRule.onNodeWithText("Épinglé en haut", substring = true).assertDoesNotExist()

    // Expand card to verify Days Served displays correctly
    composeTestRule.onNodeWithText("Abdelkader Benali").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Days Served: 6 days").assertIsDisplayed()
  }

  @Test
  fun `MainActivity launches cleanly on empty database with zero default personnel`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val dbFile = context.getDatabasePath("personnel_leaves_db")
    if (dbFile.exists()) dbFile.delete()

    val controller = Robolectric.buildActivity(MainActivity::class.java)
    controller.setup()
    assertTrue(!controller.get().isFinishing)

    // Wait briefly and verify database starts completely empty with 0 default personnel and 0 leave records
    val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
    val db = com.example.data.AppDatabase.getDatabase(context, scope)
    val (personnelCount, leavesCount) = runBlocking {
      db.personnelDao().getPersonnelCount().first() to db.leaveRecordDao().getLeavesCount().first()
    }
    assertEquals(0, personnelCount)
    assertEquals(0, leavesCount)
    controller.pause().stop().destroy()
  }
}
