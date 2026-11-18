package com.example

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
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

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `AddEditPersonnelDialog displays 5 quick duration chips and applies 48h 72h 4d 8d 10d offsets`() {
    var savedStart = 0L
    var savedReturn = 0L

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.AddEditPersonnelDialog(
          personnelToEdit = null,
          onDismiss = {},
          onSave = { _, _, _, _, _, startMillis, returnMillis, _, _, _, _ ->
            savedStart = startMillis
            savedReturn = returnMillis
          },
          currentLang = AppLanguage.ARABIC,
          animationsEnabled = false
        )
      }
    }

    composeTestRule.waitForIdle()

    // Enter required name and registration number
    composeTestRule.onNodeWithTag("full_name_input").performTextInput("محمد أمين")
    composeTestRule.onNodeWithTag("reg_number_input").performTextInput("DZ-100200")

    composeTestRule.waitForIdle()

    // Scroll the form down to expose the schedule & quick duration section inside the Dialog
    composeTestRule.onNodeWithTag("dialog_form_scroll", useUnmergedTree = true).performTouchInput {
      swipeUp(startY = 550f, endY = 50f)
    }
    composeTestRule.waitForIdle()

    // Verify all 5 quick duration labels render cleanly and are displayed
    listOf(
      2 to "48 ساعة",
      3 to "72 ساعة",
      4 to "4 أيام",
      8 to "8 أيام",
      10 to "10 أيام"
    ).forEach { (days, label) ->
      composeTestRule.onNodeWithTag("quick_duration_chip_$days")
        .assertIsDisplayed()
        .assert(hasText(label))
    }

    // Select 48 ساعة -> +2 days
    composeTestRule.onNodeWithTag("quick_duration_chip_2").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("2 يوم").assertIsDisplayed()

    // Select 72 ساعة -> +3 days
    composeTestRule.onNodeWithTag("quick_duration_chip_3").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("3 يوم").assertIsDisplayed()

    // Select 8 أيام -> +8 days and save
    composeTestRule.onNodeWithTag("quick_duration_chip_8").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("8 يوم").assertIsDisplayed()

    composeTestRule.onNodeWithTag("save_personnel_button").performClick()
    composeTestRule.waitForIdle()

    val calendarSpanDays = (savedReturn - savedStart + java.util.concurrent.TimeUnit.HOURS.toMillis(12)) /
      java.util.concurrent.TimeUnit.DAYS.toMillis(1)
    assertEquals(7L, calendarSpanDays)
    assertEquals(8L, Personnel.inclusiveLeaveDays(savedStart, savedReturn))
  }

  @Test
  fun `inclusive leave days counts both start and end date e_g 2026-01-01 to 2026-01-08 equals 8 days`() {
    val startCal = Calendar.getInstance().apply {
      set(2026, Calendar.JANUARY, 1, 0, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val endCal = Calendar.getInstance().apply {
      set(2026, Calendar.JANUARY, 8, 0, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val sameDayCal = Calendar.getInstance().apply {
      set(2026, Calendar.JANUARY, 1, 23, 30, 0)
      set(Calendar.MILLISECOND, 0)
    }

    assertEquals(8L, Personnel.inclusiveLeaveDays(startCal.timeInMillis, endCal.timeInMillis))
    assertEquals(1L, Personnel.inclusiveLeaveDays(startCal.timeInMillis, sameDayCal.timeInMillis))

    val sample = Personnel(
      fullName = "أحمد",
      rank = "نقيب - Capitaine",
      registrationNumber = "DZ-99",
      startDateMillis = startCal.timeInMillis,
      returnDateMillis = endCal.timeInMillis
    )
    assertEquals(8L, sample.totalLeaveDays)
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `redesigned FilterSortDialog renders structured controls and selects status sort and rank`() {
    var selectedSort = com.example.ui.SortOption.RETURN_SOON
    var selectedStatus = "ALL"
    var selectedRank = "ALL"
    var dismissed = false

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.FilterSortDialog(
          selectedSort = selectedSort,
          onSelectSort = { selectedSort = it },
          selectedStatus = selectedStatus,
          onSelectStatus = { selectedStatus = it },
          selectedRank = selectedRank,
          onSelectRank = { selectedRank = it },
          uniqueRanks = listOf("نقيب - Capitaine", "ملازم - Lieutenant"),
          stats = com.example.ui.DashboardStats(total = 10, atHome = 4, returningToday = 1, overdue = 2, onDuty = 3, eligible = 1),
          onResetFilters = {
            selectedSort = com.example.ui.SortOption.RETURN_SOON
            selectedStatus = "ALL"
            selectedRank = "ALL"
          },
          onDismiss = { dismissed = true },
          animationsEnabled = false,
          currentLang = AppLanguage.ARABIC
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("filter_sort_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_status_OVERDUE").performClick()
    composeTestRule.waitForIdle()
    assertEquals("OVERDUE", selectedStatus)

    composeTestRule.onNodeWithTag("filter_sort_scroll").performTouchInput {
      swipeUp(startY = (bottom - 16f).coerceAtLeast(centerY), endY = top + 16f)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("filter_sort_NAME_ASC").performClick()
    composeTestRule.waitForIdle()
    assertEquals(com.example.ui.SortOption.NAME_ASC, selectedSort)

    composeTestRule.onNodeWithTag("filter_sort_scroll").performTouchInput {
      swipeUp(startY = (bottom - 16f).coerceAtLeast(centerY), endY = top + 16f)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("filter_rank_نقيب - Capitaine").performScrollTo()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("filter_rank_نقيب - Capitaine").performClick()
    composeTestRule.waitForIdle()
    assertEquals("نقيب - Capitaine", selectedRank)

    composeTestRule.onNodeWithTag("filter_sort_apply_button").performClick()
    composeTestRule.waitForIdle()
    assertTrue(dismissed)
  }

  @Test
  fun `AppBottomNavigationPanel renders Liquid Glass dock and switches pages cleanly`() {
    var selectedPage = com.example.ui.components.AppNavPage.DASHBOARD

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.AppBottomNavigationPanel(
          selectedPage = selectedPage,
          onSelectPage = { selectedPage = it },
          notificationsCount = 2,
          isDarkMode = false,
          isNeumorphic = false,
          animationsEnabled = false,
          currentLang = AppLanguage.ENGLISH
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("bottom_nav_panel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_nav_dashboard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_nav_records").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    assertEquals(com.example.ui.components.AppNavPage.RECORDS, selectedPage)

    composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    assertEquals(com.example.ui.components.AppNavPage.SETTINGS, selectedPage)
  }

  @Test
  fun `StatisticsSection replaces registered system badge with clickable Analytics button`() {
    var analyticsOpened = false
    val stats = com.example.ui.DashboardStats(total = 5, atHome = 2, returningToday = 1, overdue = 0)

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.StatisticsSection(
          stats = stats,
          currentLang = AppLanguage.ENGLISH,
          selectedStatus = "ALL",
          onSelectStatus = {},
          onOpenAnalytics = { analyticsOpened = true },
          isDarkMode = false,
          isNeumorphic = false,
          animationsEnabled = false
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Registered in system", substring = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("dashboard_analytics_button").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    assertTrue(analyticsOpened)
  }

  @Test
  fun `adaptive launcher icon with monochrome layer and internal app logo resolve cleanly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val launcherDrawable = androidx.core.content.ContextCompat.getDrawable(context, R.mipmap.ic_launcher)
    val roundDrawable = androidx.core.content.ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)
    val appLogo = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.app_logo)

    assertTrue("Launcher icon must resolve", launcherDrawable != null)
    assertTrue("Round launcher icon must resolve", roundDrawable != null)
    assertTrue("Internal app logo must resolve", appLogo != null)

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      assertTrue(launcherDrawable is android.graphics.drawable.AdaptiveIconDrawable)
      val adaptive = launcherDrawable as android.graphics.drawable.AdaptiveIconDrawable
      assertTrue("Adaptive foreground must exist", adaptive.foreground != null)
      assertTrue("Adaptive background must exist", adaptive.background != null)
      assertTrue("Adaptive monochrome layer must exist for themed icons", adaptive.monochrome != null)
    }
  }
}
