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

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `AddNewLeaveDialog selects personnel leave type syncs days with return date and confirms`() {
    val p1 = Personnel(
      id = 10L,
      fullName = "سمير بن عيسى",
      fullNameLatin = "Samir Benaissa",
      rank = "رقيب - Sergent",
      registrationNumber = "DZ-1010",
      startDateMillis = getOffsetDateMillis(-20),
      returnDateMillis = getOffsetDateMillis(-10),
      isReturnedToDuty = true
    )
    val p2 = Personnel(
      id = 20L,
      fullName = "ياسين بوعلام",
      fullNameLatin = "Yacine Boualam",
      rank = "نقيب - Capitaine",
      registrationNumber = "DZ-2020",
      startDateMillis = getOffsetDateMillis(-30),
      returnDateMillis = getOffsetDateMillis(-20),
      isReturnedToDuty = true
    )

    var confirmedId = -1L
    var confirmedType = ""
    var confirmedStart = 0L
    var confirmedReturn = 0L

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.AddNewLeaveDialog(
          personnelList = listOf(p1, p2),
          onDismiss = {},
          onOpenAddPersonnel = {},
          onConfirm = { id, type, start, ret ->
            confirmedId = id
            confirmedType = type
            confirmedStart = start
            confirmedReturn = ret
          },
          currentLang = AppLanguage.ARABIC,
          animationsEnabled = false
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("add_leave_dialog").assertIsDisplayed()

    // Select second person
    composeTestRule.onNodeWithTag("add_leave_personnel_selector").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("add_leave_personnel_item_20").performClick()
    composeTestRule.waitForIdle()

    // Select second leave type
    composeTestRule.onNodeWithTag("add_leave_type_option_1").performClick()
    composeTestRule.waitForIdle()

    // Select 10 days quick chip and verify inclusive days == 10
    composeTestRule.onNodeWithTag("add_leave_form_scroll", useUnmergedTree = true).performTouchInput {
      swipeUp(startY = 550f, endY = 50f)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("add_leave_quick_days_10").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("confirm_add_leave_button").performClick()
    composeTestRule.waitForIdle()

    assertEquals(20L, confirmedId)
    assertEquals(com.example.data.algerianLeaveTypes[1], confirmedType)
    assertEquals(10L, Personnel.inclusiveLeaveDays(confirmedStart, confirmedReturn))
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `PersonnelCard History button opens PersonnelLeaveHistoryDialog with all leaves for that person`() {
    val person = Personnel(
      id = 77L,
      fullName = "كريم منصوري",
      fullNameLatin = "Karim Mansouri",
      rank = "نقيب - Capitaine",
      registrationNumber = "DZ-7700",
      startDateMillis = getOffsetDateMillis(-15),
      returnDateMillis = getOffsetDateMillis(-8),
      isReturnedToDuty = true
    )
    val leaves = listOf(
      com.example.data.LeaveRecord(
        id = 1L,
        personnelId = 77L,
        personnelName = "كريم منصوري",
        personnelNameLatin = "Karim Mansouri",
        rank = "نقيب - Capitaine",
        registrationNumber = "DZ-7700",
        leaveType = com.example.data.algerianLeaveTypes[0],
        startDateMillis = getOffsetDateMillis(-15),
        returnDateMillis = getOffsetDateMillis(-8),
        durationDays = 8L,
        statusText = "مكتملة"
      ),
      com.example.data.LeaveRecord(
        id = 2L,
        personnelId = 77L,
        personnelName = "كريم منصوري",
        personnelNameLatin = "Karim Mansouri",
        rank = "نقيب - Capitaine",
        registrationNumber = "DZ-7700",
        leaveType = com.example.data.algerianLeaveTypes[1],
        startDateMillis = getOffsetDateMillis(-60),
        returnDateMillis = getOffsetDateMillis(-57),
        durationDays = 4L,
        statusText = "مكتملة"
      ),
      com.example.data.LeaveRecord(
        id = 3L,
        personnelId = 999L,
        personnelName = "شخص آخر",
        rank = "رقيب - Sergent",
        registrationNumber = "DZ-9999",
        leaveType = com.example.data.algerianLeaveTypes[0],
        startDateMillis = getOffsetDateMillis(-5),
        returnDateMillis = getOffsetDateMillis(-1),
        durationDays = 5L
      )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        val historyPersonState = androidx.compose.runtime.remember {
          androidx.compose.runtime.mutableStateOf<Personnel?>(null)
        }
        PersonnelCard(
          personnel = person,
          dutyThresholdDays = 45,
          onConfirmDuty = {},
          onEdit = {},
          onDelete = {},
          onViewHistory = { historyPersonState.value = person },
          animationsEnabled = false,
          currentLang = AppLanguage.ARABIC
        )
        historyPersonState.value?.let { target ->
          com.example.ui.components.PersonnelLeaveHistoryDialog(
            personnel = target,
            allLeaves = leaves,
            onDismiss = { historyPersonState.value = null },
            animationsEnabled = false,
            currentLang = AppLanguage.ARABIC
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    // Expand card and click "السجل"
    composeTestRule.onNodeWithText("كريم منصوري").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("personnel_history_btn_77").assertIsDisplayed().assert(hasText("السجل")).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("personnel_leave_history_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("leave_history_item_1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("leave_history_item_2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("leave_history_item_3").assertDoesNotExist()
  }

  @Test
  fun `PersonnelViewModel grantNewLeaveWithType updates personnel profile and records leave history`() {
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val dbFile = context.getDatabasePath("personnel_leaves_db")
    if (dbFile.exists()) dbFile.delete()

    val db = com.example.data.AppDatabase.getDatabase(
      context,
      kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
    )
    val initialPerson = Personnel(
      fullName = "فريد بن سالم",
      fullNameLatin = "Farid Bensalem",
      rank = "ملازم - Lieutenant",
      registrationNumber = "DZ-5555",
      startDateMillis = getOffsetDateMillis(-40),
      returnDateMillis = getOffsetDateMillis(-35),
      isReturnedToDuty = true,
      leaveType = com.example.data.algerianLeaveTypes[0]
    )
    val insertedId = runBlocking { db.personnelDao().insert(initialPerson) }

    val viewModel = com.example.ui.PersonnelViewModel(context)
    val newStart = getOffsetDateMillis(0)
    val newReturn = getOffsetDateMillis(7)
    val newType = com.example.data.algerianLeaveTypes[1]

    viewModel.grantNewLeaveWithType(
      personnelId = insertedId,
      leaveType = newType,
      startDateMillis = newStart,
      returnDateMillis = newReturn
    )

    var updated: Personnel? = null
    var leaves: List<com.example.data.LeaveRecord> = emptyList()
    repeat(30) {
      org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
      runBlocking {
        updated = db.personnelDao().getPersonnelById(insertedId)
        leaves = db.leaveRecordDao().getLeavesForPersonnel(insertedId).first()
      }
      if (updated?.isReturnedToDuty == false && leaves.isNotEmpty()) return@repeat
      Thread.sleep(50)
    }
    assertEquals(false, updated?.isReturnedToDuty)
    assertEquals(newType, updated?.leaveType)
    assertEquals(8L, updated?.totalLeaveDays)
    assertEquals(1, leaves.size)
    assertEquals(newType, leaves.first().leaveType)
    assertEquals(8L, leaves.first().durationDays)
  }
}
