package com.example

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.testTag
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
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.data.Personnel
import com.example.ui.components.BottomDockFadeToBlurOverlay
import com.example.ui.components.PersonnelCard
import com.example.ui.components.TopBarFadeToBlurOverlay
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

    // Scroll to the optional leave button ("إضافة إجازة / عطلة") and open the dedicated leave window
    composeTestRule.onNodeWithTag("open_optional_leave_dialog_btn")
      .performScrollTo()
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("optional_leave_editor_dialog").assertIsDisplayed()

    // Verify all 5 quick duration labels render cleanly inside the secondary leave window
    listOf(
      2 to "48 ساعة",
      3 to "72 ساعة",
      4 to "4 أيام",
      8 to "8 أيام",
      10 to "10 أيام"
    ).forEach { (days, label) ->
      composeTestRule.onNodeWithTag("quick_duration_chip_$days")
        .performScrollTo()
        .assertIsDisplayed()
        .assert(hasText(label))
    }

    // Select 48 ساعة -> +2 days
    composeTestRule.onNodeWithTag("quick_duration_chip_2").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("2 يوم").performScrollTo().assertIsDisplayed()

    // Select 72 ساعة -> +3 days
    composeTestRule.onNodeWithTag("quick_duration_chip_3").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("3 يوم").performScrollTo().assertIsDisplayed()

    // Select 8 أيام -> +8 days, confirm leave dialog, and save personnel
    composeTestRule.onNodeWithTag("quick_duration_chip_8").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("8 يوم").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("confirm_optional_leave_btn").performClick()
    composeTestRule.waitForIdle()

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

    // Select second person via dedicated Liquid Glass picker dialog
    composeTestRule.onNodeWithTag("add_leave_personnel_selector").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("add_leave_personnel_picker_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_leave_personnel_search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_leave_personnel_item_20").performClick()
    composeTestRule.waitForIdle()

    // Select second leave type
    composeTestRule.onNodeWithTag("add_leave_type_option_1").performClick()
    composeTestRule.waitForIdle()

    // Select 10 days quick chip and verify inclusive days == 10
    composeTestRule.onNodeWithTag("add_leave_quick_days_10").performScrollTo()
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
    val newType = com.example.data.algerianLeaveTypes[2]

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

  @Test
  fun `short leave is recorded directly in history without overriding displayed last leave or duty baseline`() {
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val dbFile = context.getDatabasePath("personnel_leaves_db")
    if (dbFile.exists()) dbFile.delete()

    val db = com.example.data.AppDatabase.getDatabase(
      context,
      kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
    )
    val annualStart = getOffsetDateMillis(-60)
    val annualReturn = getOffsetDateMillis(-45)
    val annualType = com.example.data.algerianLeaveTypes[2] // "عطلة سنوية"
    val initialPerson = Personnel(
      fullName = "عمر بن يوسف",
      fullNameLatin = "Omar Benyoucef",
      rank = "نقيب - Capitaine",
      registrationNumber = "DZ-8888",
      startDateMillis = annualStart,
      returnDateMillis = annualReturn,
      isReturnedToDuty = true,
      leaveType = annualType
    )
    val insertedId = runBlocking {
      val id = db.personnelDao().insert(initialPerson)
      db.leaveRecordDao().insert(
        com.example.data.LeaveRecord(
          personnelId = id,
          personnelName = "عمر بن يوسف",
          personnelNameLatin = "Omar Benyoucef",
          rank = "نقيب - Capitaine",
          registrationNumber = "DZ-8888",
          leaveType = annualType,
          startDateMillis = annualStart,
          returnDateMillis = annualReturn,
          durationDays = 16L,
          statusText = "مكتملة"
        )
      )
      id
    }

    val viewModel = com.example.ui.PersonnelViewModel(context)
    val shortType = com.example.data.algerianLeaveTypes[1] // "إجازة قصيرة"
    val shortStart = getOffsetDateMillis(-1)
    val shortReturn = getOffsetDateMillis(1)

    viewModel.grantNewLeaveWithType(
      personnelId = insertedId,
      leaveType = shortType,
      startDateMillis = shortStart,
      returnDateMillis = shortReturn
    )

    var personAfter: Personnel? = null
    var leaves: List<com.example.data.LeaveRecord> = emptyList()
    repeat(30) {
      org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
      runBlocking {
        personAfter = db.personnelDao().getPersonnelById(insertedId)
        leaves = db.leaveRecordDao().getLeavesForPersonnel(insertedId).first()
      }
      if (leaves.size >= 2) return@repeat
      Thread.sleep(50)
    }

    // Recorded directly in history (السجل)
    assertEquals(2, leaves.size)
    assertTrue(leaves.any { it.leaveType == shortType && it.durationDays == 3L })

    // Does NOT display as the last/active leave in the main UI (الواجهة)
    assertEquals(annualType, personAfter?.leaveType)
    assertEquals(annualStart, personAfter?.startDateMillis)
    assertEquals(annualReturn, personAfter?.returnDateMillis)
    assertEquals(true, personAfter?.isReturnedToDuty)

    // Duty reference still uses the prior Annual Leave (45 days), not the Short Leave
    val dutyInfo = personAfter!!.computeDutyReferenceInfo(leaves)
    assertEquals(annualReturn, dutyInfo.lastLeaveReturnMillis)
    assertEquals(45L, dutyInfo.daysSinceReference)
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `AddEditPersonnelDialog allows saving new personnel without optional leave details`() {
    var savedStart = -1L
    var savedReturn = -1L
    var savedReturned = false
    var savedLeaveType = "UNSET"

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.AddEditPersonnelDialog(
          personnelToEdit = null,
          onDismiss = {},
          onSave = { _, _, _, _, _, startMillis, returnMillis, isReturned, leaveType, _, _ ->
            savedStart = startMillis
            savedReturn = returnMillis
            savedReturned = isReturned
            savedLeaveType = leaveType
          },
          currentLang = AppLanguage.ARABIC,
          animationsEnabled = false
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("full_name_input").performTextInput("سمير بلقاسم")
    composeTestRule.onNodeWithTag("reg_number_input").performTextInput("DZ-778899")
    composeTestRule.waitForIdle()

    // Inline quick duration chips should no longer be visible until user opens optional leave dialog
    composeTestRule.onNodeWithTag("quick_duration_chip_8").assertDoesNotExist()
    composeTestRule.onNodeWithTag("open_optional_leave_dialog_btn").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("save_personnel_button").performClick()
    composeTestRule.waitForIdle()

    assertEquals(0L, savedStart)
    assertEquals(0L, savedReturn)
    assertEquals(true, savedReturned)
    assertEquals("", savedLeaveType)
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `AddEditPersonnelDialog luxury wilaya picker filters by number and Arabic name`() {
    var savedWilaya = ""

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.AddEditPersonnelDialog(
          personnelToEdit = null,
          onDismiss = {},
          onSave = { _, _, _, _, _, _, _, _, _, wilaya, _ ->
            savedWilaya = wilaya
          },
          currentLang = AppLanguage.ARABIC,
          animationsEnabled = false
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("full_name_input").performTextInput("عادل زروقي")
    composeTestRule.onNodeWithTag("reg_number_input").performTextInput("DZ-310031")

    composeTestRule.onNodeWithTag("wilaya_selector_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("luxury_wilaya_picker_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("wilaya_dropdown_search_input").performTextInput("وهران")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("wilaya_item_31").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("luxury_wilaya_picker_dialog").assertDoesNotExist()
    composeTestRule.onNodeWithTag("save_personnel_button").performClick()
    composeTestRule.waitForIdle()

    assertTrue(savedWilaya.startsWith("31 - "))
  }

  @Test
  fun `TopBarFadeToBlurOverlay renders cleanly above floating top bar capsule in light and dark modes`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        val layer = rememberGraphicsLayer()
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
        ) {
          TopBarFadeToBlurOverlay(
            modifier = Modifier
              .matchParentSize()
              .testTag("test_top_fade_blur_light"),
            isDarkMode = false,
            backdropLayer = layer
          )
          TopBarFadeToBlurOverlay(
            modifier = Modifier
              .matchParentSize()
              .testTag("test_top_fade_blur_dark"),
            isDarkMode = true,
            backdropLayer = layer
          )
          BottomDockFadeToBlurOverlay(
            modifier = Modifier
              .matchParentSize()
              .testTag("test_bottom_fade_blur_light"),
            isDarkMode = false,
            backdropLayer = layer
          )
          BottomDockFadeToBlurOverlay(
            modifier = Modifier
              .matchParentSize()
              .testTag("test_bottom_fade_blur_dark"),
            isDarkMode = true,
            backdropLayer = layer
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("test_top_fade_blur_light").assertIsDisplayed()
    composeTestRule.onNodeWithTag("test_top_fade_blur_dark").assertIsDisplayed()
    composeTestRule.onNodeWithTag("test_bottom_fade_blur_light").assertIsDisplayed()
    composeTestRule.onNodeWithTag("test_bottom_fade_blur_dark").assertIsDisplayed()
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `RoomDatabaseScreen switches Liquid Glass segmented tabs and opens clear confirmation dialog`() {
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val dbFile = context.getDatabasePath("personnel_leaves_db")
    if (dbFile.exists()) dbFile.delete()

    val viewModel = com.example.ui.PersonnelViewModel(context)

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.screens.RoomDatabaseScreen(
          viewModel = viewModel,
          onBack = {},
          topBarClearance = 16.dp,
          bottomDockClearance = 80.dp
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("room_database_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("room_header_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("room_tab_personnel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("room_tab_leaves").assertIsDisplayed()
    composeTestRule.onNodeWithTag("room_tab_diagnostics").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("room_diagnostics_card").assertIsDisplayed()

    composeTestRule.onNodeWithTag("room_tab_personnel").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("room_clear_tables_btn").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("room_clear_confirm_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("room_clear_cancel_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("room_clear_confirm_dialog").assertDoesNotExist()
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `NotificationScreen displays Liquid Glass shell and opens notification detail popup`() {
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val dbFile = context.getDatabasePath("personnel_leaves_db")
    if (dbFile.exists()) dbFile.delete()

    val db = com.example.data.AppDatabase.getDatabase(
      context,
      kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
    )
    val overduePerson = Personnel(
      fullName = "رشيد بلحاج",
      fullNameLatin = "Rachid Belhadj",
      rank = "نقيب - Capitaine",
      registrationNumber = "1.2026.00001",
      startDateMillis = getOffsetDateMillis(-15),
      returnDateMillis = getOffsetDateMillis(-3),
      isReturnedToDuty = false
    )
    runBlocking { db.personnelDao().insert(overduePerson) }

    val viewModel = com.example.ui.PersonnelViewModel(context)
    repeat(20) {
      org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
      if (viewModel.notifications.value.isNotEmpty()) return@repeat
      Thread.sleep(50)
    }

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.screens.NotificationScreen(
          viewModel = viewModel,
          onBack = {},
          topBarClearance = 16.dp,
          bottomDockClearance = 80.dp
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("notifications_scaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("notifications_back_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("notifications_refresh_button").assertIsDisplayed()

    val firstNotif = viewModel.notifications.value.firstOrNull()
    assertTrue("Expected overdue notification to be generated", firstNotif != null)
    composeTestRule.onNodeWithTag("notification_card_${firstNotif!!.id}").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("notification_detail_popup").assertIsDisplayed()
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `redesigned Liquid Glass dialogs and BiometricLockScreen render cleanly in RTL Dark Mode`() {
    val trashedPerson = Personnel(
      id = 55L,
      fullName = "محمد الورثيلاني",
      fullNameLatin = "Mohamed El Ouertilani",
      rank = "نقيب - Capitaine",
      registrationNumber = "1.2026.00000",
      startDateMillis = getOffsetDateMillis(-10),
      returnDateMillis = getOffsetDateMillis(-2),
      isReturnedToDuty = true,
      isDeleted = true,
      deletedAtMillis = System.currentTimeMillis()
    )

    val showExport = androidx.compose.runtime.mutableStateOf(true)
    val showTrash = androidx.compose.runtime.mutableStateOf(false)
    val showDev = androidx.compose.runtime.mutableStateOf(false)
    val showThemePicker = androidx.compose.runtime.mutableStateOf(false)
    val showConfirm = androidx.compose.runtime.mutableStateOf(false)
    val showBiometric = androidx.compose.runtime.mutableStateOf(false)

    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        if (showExport.value) {
          com.example.ui.components.ExportDialog(
            csvContent = "id,name",
            jsonContent = "[]",
            onExportPdf = {},
            onImportJson = { _, _ -> true to "OK" },
            onDismiss = { showExport.value = false },
            currentLang = AppLanguage.ARABIC,
            isDarkMode = true,
            animationsEnabled = false
          )
        }
        if (showTrash.value) {
          com.example.ui.components.TrashBinDialog(
            trashedPersonnel = listOf(trashedPerson),
            isDarkMode = true,
            currentLang = AppLanguage.ARABIC,
            onRestore = {},
            onRestoreAll = {},
            onDeleteForever = {},
            onEmptyTrash = {},
            onDismiss = { showTrash.value = false },
            animationsEnabled = false
          )
        }
        if (showDev.value) {
          com.example.ui.components.DeveloperInfoDialog(
            currentLang = AppLanguage.ARABIC,
            onDismiss = { showDev.value = false },
            isDarkMode = true,
            animationsEnabled = false
          )
        }
        if (showThemePicker.value) {
          com.example.ui.components.ThemeEnginePickerDialog(
            selectedThemeId = com.example.ui.theme.ThemeEngine.allThemes.first().id,
            isDarkMode = true,
            currentLang = AppLanguage.ARABIC,
            onSelectTheme = {},
            onResetDefault = {},
            onDismiss = { showThemePicker.value = false },
            animationsEnabled = false
          )
        }
        if (showConfirm.value) {
          com.example.ui.components.LiquidGlassConfirmDialog(
            title = "تأكيد",
            message = "هل تريد المتابعة؟",
            confirmText = "نعم",
            cancelText = "إلغاء",
            icon = Icons.Default.Warning,
            isDarkMode = true,
            currentLang = AppLanguage.ARABIC,
            animationsEnabled = false,
            dialogTestTag = "confirm_test_dialog",
            confirmTestTag = "confirm_test_ok",
            cancelTestTag = "confirm_test_cancel",
            onConfirm = { showConfirm.value = false },
            onDismiss = { showConfirm.value = false }
          )
        }
        if (showBiometric.value) {
          com.example.ui.components.BiometricLockScreen(
            onUnlockSuccess = {},
            isDarkMode = true,
            currentLang = AppLanguage.ARABIC,
            animationsEnabled = false
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("export_import_dialog").assertIsDisplayed()
    showExport.value = false
    showTrash.value = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("trash_bin_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("trash_item_55").assertIsDisplayed()

    showTrash.value = false
    showDev.value = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("developer_info_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("developer_whatsapp_button").assertIsDisplayed()

    showDev.value = false
    showThemePicker.value = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("theme_engine_picker_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("theme_engine_search_input").assertIsDisplayed()

    showThemePicker.value = false
    showConfirm.value = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("confirm_test_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirm_test_ok").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("confirm_test_dialog").assertDoesNotExist()

    showBiometric.value = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("biometric_lock_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("biometric_unlock_button").assertIsDisplayed()
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `ExtendLeaveDialog selects quick extension days and returns accurate new return date`() {
    val startMillis = getOffsetDateMillis(-5)
    val returnMillis = getOffsetDateMillis(4)
    val person = Personnel(
      id = 77L,
      fullName = "أمين بوزيدي",
      fullNameLatin = "Amine Bouzidi",
      rank = "نقيب - Capitaine",
      registrationNumber = "1.2026.00077",
      startDateMillis = startMillis,
      returnDateMillis = returnMillis,
      isReturnedToDuty = false,
      leaveType = "عطلة سنوية"
    )

    var confirmedReturnMillis = 0L
    var confirmedAddedDays = 0
    var confirmedReason = ""

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.ExtendLeaveDialog(
          personnel = person,
          onDismiss = {},
          onConfirmExtend = { newReturn, addedDays, reason ->
            confirmedReturnMillis = newReturn
            confirmedAddedDays = addedDays
            confirmedReason = reason
          },
          currentLang = AppLanguage.ARABIC,
          animationsEnabled = false
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("extend_leave_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("extend_preview_card").assertIsDisplayed()

    composeTestRule.onNodeWithTag("extend_quick_days_7").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("extend_reason_input").performScrollTo().performTextInput("ظروف عائلية")
    composeTestRule.onNodeWithTag("extend_confirm_btn").performClick()

    composeTestRule.waitForIdle()
    assertEquals(7, confirmedAddedDays)
    assertEquals("ظروف عائلية", confirmedReason)
    assertEquals(7L, Personnel.signedDaysBetween(returnMillis, confirmedReturnMillis))
  }

  @Test
  fun `PersonnelCard displays extend button for active leave and hides it on duty`() {
    val activePerson = Personnel(
      id = 301L,
      fullName = "كريم منصوري",
      fullNameLatin = "Karim Mansouri",
      rank = "رقيب - Sergent",
      registrationNumber = "1.2026.00301",
      startDateMillis = getOffsetDateMillis(-3),
      returnDateMillis = getOffsetDateMillis(4),
      isReturnedToDuty = false
    )
    val onDutyPerson = Personnel(
      id = 302L,
      fullName = "سالم وهراني",
      fullNameLatin = "Salem Wahrani",
      rank = "رقيب - Sergent",
      registrationNumber = "1.2026.00302",
      startDateMillis = getOffsetDateMillis(-20),
      returnDateMillis = getOffsetDateMillis(-10),
      isReturnedToDuty = true
    )

    var extendedClicked = false
    composeTestRule.setContent {
      MyApplicationTheme {
        androidx.compose.foundation.layout.Column {
          PersonnelCard(
            personnel = activePerson,
            dutyThresholdDays = 40,
            onConfirmDuty = {},
            onEdit = {},
            onDelete = {},
            onExtendLeave = { extendedClicked = true },
            animationsEnabled = false
          )
          PersonnelCard(
            personnel = onDutyPerson,
            dutyThresholdDays = 40,
            onConfirmDuty = {},
            onEdit = {},
            onDelete = {},
            onExtendLeave = {},
            animationsEnabled = false
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("كريم منصوري").performClick()
    composeTestRule.onNodeWithText("سالم وهراني").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("personnel_extend_btn_301").assertIsDisplayed().performClick()
    assertTrue(extendedClicked)
    composeTestRule.onNodeWithTag("personnel_extend_btn_302").assertDoesNotExist()
  }

  @Test
  fun `extendPersonnelLeave updates Personnel and LeaveRecord and supports undo`() {
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val db = com.example.data.AppDatabase.getDatabase(
      context,
      kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
    )
    val startMillis = getOffsetDateMillis(-4)
    val origReturnMillis = getOffsetDateMillis(3)
    val newReturnMillis = getOffsetDateMillis(8)

    val personId = runBlocking {
      val id = db.personnelDao().insert(
        Personnel(
          fullName = "سمير بن عيسى",
          fullNameLatin = "Samir Benaissa",
          rank = "ملازم - Lieutenant",
          registrationNumber = "1.2026.00501",
          startDateMillis = startMillis,
          returnDateMillis = origReturnMillis,
          isReturnedToDuty = false,
          leaveType = "عطلة سنوية"
        )
      )
      db.leaveRecordDao().insert(
        com.example.data.LeaveRecord(
          personnelId = id,
          personnelName = "سمير بن عيسى",
          personnelNameLatin = "Samir Benaissa",
          rank = "ملازم - Lieutenant",
          registrationNumber = "1.2026.00501",
          leaveType = "عطلة سنوية",
          startDateMillis = startMillis,
          returnDateMillis = origReturnMillis,
          durationDays = Personnel.inclusiveLeaveDays(startMillis, origReturnMillis),
          statusText = "في إجازة"
        )
      )
      id
    }

    val viewModel = com.example.ui.PersonnelViewModel(context)
    viewModel.extendPersonnelLeave(personId, newReturnMillis, "تمديد إداري")

    repeat(30) {
      org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
      val updated = runBlocking { db.personnelDao().getPersonnelById(personId) }
      if (updated != null && updated.returnDateMillis == newReturnMillis) return@repeat
      Thread.sleep(40)
    }

    val extendedPerson = runBlocking { db.personnelDao().getPersonnelById(personId) }!!
    assertEquals(newReturnMillis, extendedPerson.returnDateMillis)
    assertTrue(!extendedPerson.isReturnedToDuty)
    assertTrue(extendedPerson.notes.contains("تمديد إداري"))

    val extendedLeaves = runBlocking { db.leaveRecordDao().getLeavesForPersonnel(personId).first() }
    assertTrue(extendedLeaves.any { it.returnDateMillis == newReturnMillis && it.durationDays == 13L })

    // Verify atomic undo restores both Personnel and LeaveRecord
    viewModel.undo()
    repeat(30) {
      org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
      val reverted = runBlocking { db.personnelDao().getPersonnelById(personId) }
      if (reverted != null && reverted.returnDateMillis == origReturnMillis) return@repeat
      Thread.sleep(40)
    }
    val revertedPerson = runBlocking { db.personnelDao().getPersonnelById(personId) }!!
    assertEquals(origReturnMillis, revertedPerson.returnDateMillis)
    val revertedLeaves = runBlocking { db.leaveRecordDao().getLeavesForPersonnel(personId).first() }
    assertTrue(revertedLeaves.any { it.returnDateMillis == origReturnMillis && it.durationDays == 8L })
  }

  @Test
  fun `extendLeaveRecord on short leave updates only LeaveRecord without overwriting Personnel`() {
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val db = com.example.data.AppDatabase.getDatabase(
      context,
      kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
    )
    val mainStart = getOffsetDateMillis(-20)
    val mainReturn = getOffsetDateMillis(-10)
    val shortStart = getOffsetDateMillis(-1)
    val shortReturn = getOffsetDateMillis(1)
    val shortExtendedReturn = getOffsetDateMillis(4)

    val (personId, shortRecord) = runBlocking {
      val id = db.personnelDao().insert(
        Personnel(
          fullName = "فريد قاسمي",
          fullNameLatin = "Farid Kacemi",
          rank = "عريف أول - Caporal-Chef",
          registrationNumber = "1.2026.00601",
          startDateMillis = mainStart,
          returnDateMillis = mainReturn,
          isReturnedToDuty = true,
          leaveType = "عطلة سنوية"
        )
      )
      val rec = com.example.data.LeaveRecord(
        personnelId = id,
        personnelName = "فريد قاسمي",
        personnelNameLatin = "Farid Kacemi",
        rank = "عريف أول - Caporal-Chef",
        registrationNumber = "1.2026.00601",
        leaveType = "إجازة قصيرة",
        startDateMillis = shortStart,
        returnDateMillis = shortReturn,
        durationDays = Personnel.inclusiveLeaveDays(shortStart, shortReturn),
        statusText = "في إجازة"
      )
      val recId = db.leaveRecordDao().insert(rec)
      id to rec.copy(id = recId)
    }

    val viewModel = com.example.ui.PersonnelViewModel(context)
    viewModel.extendLeaveRecord(shortRecord, shortExtendedReturn, "تأجيل السفر")

    repeat(30) {
      org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
      val updated = runBlocking { db.leaveRecordDao().getLeaveById(shortRecord.id) }
      if (updated != null && updated.returnDateMillis == shortExtendedReturn) return@repeat
      Thread.sleep(40)
    }

    val updatedShort = runBlocking { db.leaveRecordDao().getLeaveById(shortRecord.id) }!!
    assertEquals(shortExtendedReturn, updatedShort.returnDateMillis)
    assertEquals(Personnel.inclusiveLeaveDays(shortStart, shortExtendedReturn), updatedShort.durationDays)

    val untouchedPerson = runBlocking { db.personnelDao().getPersonnelById(personId) }!!
    assertEquals(mainReturn, untouchedPerson.returnDateMillis)
    assertTrue(untouchedPerson.isReturnedToDuty)
  }

  @Test
  @Config(qualifiers = "w412dp-h915dp")
  fun `DashboardPillSearchBar DashboardQuickActionRow and AllPersonsSummaryBanner render and respond in English and Arabic`() {
    var filterOpened = false
    var allPersonsOpened = false
    var addPersonnelClicked = false
    var latestSearch = ""
    val stats = com.example.ui.DashboardStats(
      total = 12,
      atHome = 4,
      returningToday = 1,
      overdue = 1,
      onDuty = 6,
      eligible = 2
    )

    composeTestRule.setContent {
      val searchVal = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
      MyApplicationTheme {
        androidx.compose.foundation.layout.Column {
          com.example.ui.components.DashboardPillSearchBar(
            searchQuery = searchVal.value,
            onSearchQueryChange = {
              searchVal.value = it
              latestSearch = it
            },
            onSubmitSearch = {},
            onOpenFilter = { filterOpened = true },
            activeFilterCount = 2,
            isDarkMode = false,
            currentLang = AppLanguage.ENGLISH
          )
          com.example.ui.components.DashboardQuickActionRow(
            urgentCount = 2,
            currentLang = AppLanguage.ENGLISH,
            isDarkMode = false,
            onAddPersonnel = { addPersonnelClicked = true },
            onOpenUrgentAlerts = {},
            onOpenDatabase = {}
          )
          com.example.ui.components.AllPersonsSummaryBanner(
            stats = stats,
            currentLang = AppLanguage.ENGLISH,
            animationsEnabled = false,
            onOpenAllPersons = { allPersonsOpened = true }
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("search_field").assertIsDisplayed().performTextInput("Mila")
    composeTestRule.waitForIdle()
    assertEquals("Mila", latestSearch)

    composeTestRule.onNodeWithTag("filter_sort_dialog_button").assertIsDisplayed().performClick()
    assertTrue(filterOpened)

    composeTestRule.onNodeWithTag("action_btn_add_personnel").assertIsDisplayed().performClick()
    assertTrue(addPersonnelClicked)

    composeTestRule.onNodeWithTag("all_persons_summary_banner").assertIsDisplayed()
    composeTestRule.onNodeWithText("All persons", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("all_persons_dotted_route", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("all_persons_summary_text", useUnmergedTree = true)
      .assertIsDisplayed()
      .assert(hasText("6 on duty", substring = true))
    composeTestRule.onNodeWithTag("all_persons_summary_banner").performClick()
    assertTrue(allPersonsOpened)
  }

  @Test
  fun `PersonnelCard trailing emerald quick action triggers onQuickAddLeave when on duty and onConfirmDuty when on leave`() {
    val onDutyPerson = Personnel(
      id = 501L,
      fullName = "خالد عطايلية",
      fullNameLatin = "Charfeddine Kahlessenane",
      rank = "نقيب - Capitaine",
      registrationNumber = "1.2026.00501",
      wilaya = "43 - ميلة (Mila)",
      startDateMillis = getOffsetDateMillis(-25),
      returnDateMillis = getOffsetDateMillis(-15),
      isReturnedToDuty = true
    )
    val onLeavePerson = Personnel(
      id = 502L,
      fullName = "أمين بن صالح",
      fullNameLatin = "Amine Bensalah",
      rank = "رقيب - Sergent",
      registrationNumber = "1.2026.00502",
      wilaya = "16 - الجزائر (Alger)",
      startDateMillis = getOffsetDateMillis(-2),
      returnDateMillis = getOffsetDateMillis(5),
      isReturnedToDuty = false
    )

    var quickLeaveTriggered = false
    var confirmDutyTriggered = false

    composeTestRule.setContent {
      MyApplicationTheme {
        androidx.compose.foundation.layout.Column(
          modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
          PersonnelCard(
            personnel = onDutyPerson,
            dutyThresholdDays = 40,
            onConfirmDuty = {},
            onEdit = {},
            onDelete = {},
            onQuickAddLeave = { quickLeaveTriggered = true },
            animationsEnabled = false,
            currentLang = AppLanguage.ENGLISH
          )
          PersonnelCard(
            personnel = onLeavePerson,
            dutyThresholdDays = 40,
            onConfirmDuty = { confirmDutyTriggered = true },
            onEdit = {},
            onDelete = {},
            onQuickAddLeave = {},
            animationsEnabled = false,
            currentLang = AppLanguage.ENGLISH
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Charfeddine Kahlessenane").assertIsDisplayed()
    composeTestRule.onNodeWithText("On Duty", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("personnel_wilaya_501", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag("personnel_item_501").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("personnel_quick_action_501").performScrollTo().assertIsDisplayed().performClick()
    assertTrue(quickLeaveTriggered)

    // Collapse first card and expand second card
    composeTestRule.onNodeWithTag("personnel_item_501").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("personnel_item_502").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("personnel_quick_action_502").performScrollTo().assertIsDisplayed().performClick()
    assertTrue(confirmDutyTriggered)
  }

  @Test
  fun `PersonnelCard displays effectiveDaysSinceDuty counter tile instead of wilaya code and shows lastShortLeaveInfo`() {
    val personOnDuty = Personnel(
      id = 601L,
      fullName = "شرف الدين قحلسنان",
      fullNameLatin = "Charfeddine Kahlessenane",
      rank = "عريف أول - Caporal-Chef",
      registrationNumber = "2014.430.01219",
      wilaya = "43 - ميلة (Mila)",
      startDateMillis = getOffsetDateMillis(-29),
      returnDateMillis = getOffsetDateMillis(-19),
      isReturnedToDuty = true
    )
    val shortLeaveRecord = com.example.data.LeaveRecord(
      id = 91L,
      personnelId = 601L,
      personnelName = "شرف الدين قحلسنان",
      rank = "عريف أول - Caporal-Chef",
      registrationNumber = "2014.430.01219",
      leaveType = "إجازة قصيرة",
      startDateMillis = getOffsetDateMillis(-10),
      returnDateMillis = getOffsetDateMillis(-7),
      durationDays = 4L,
      statusText = "منتهية",
      destinationWilaya = "43 - ميلة"
    )
    val shortLeaveInfo = com.example.data.LastShortLeaveInfo(
      record = shortLeaveRecord,
      daysCounter = 7L,
      isCurrentlyOnShortLeave = false,
      isReturningToday = false
    )
    val personOnLeave = Personnel(
      id = 602L,
      fullName = "كريم بن ناصر",
      fullNameLatin = "Karim Bennacer",
      rank = "رقيب - Sergent",
      registrationNumber = "2018.160.00441",
      wilaya = "16 - الجزائر - Alger",
      startDateMillis = getOffsetDateMillis(-5),
      returnDateMillis = getOffsetDateMillis(5),
      isReturnedToDuty = false
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        androidx.compose.foundation.layout.Column(
          modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
          PersonnelCard(
            personnel = personOnDuty,
            dutyThresholdDays = 45,
            onConfirmDuty = {},
            onEdit = {},
            onDelete = {},
            animationsEnabled = false,
            currentLang = AppLanguage.ENGLISH,
            effectiveDaysSinceDuty = 19L,
            lastShortLeaveInfo = shortLeaveInfo
          )
          PersonnelCard(
            personnel = personOnLeave,
            dutyThresholdDays = 45,
            onConfirmDuty = {},
            onEdit = {},
            onDelete = {},
            animationsEnabled = false,
            currentLang = AppLanguage.ENGLISH,
            effectiveDaysSinceDuty = -5L
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("personnel_days_counter_601", useUnmergedTree = true)
      .assert(androidx.compose.ui.test.hasAnyDescendant(hasText("19")))
    composeTestRule.onNodeWithTag("personnel_short_leave_badge_601", useUnmergedTree = true)
      .assertIsDisplayed()
      .assert(androidx.compose.ui.test.hasAnyDescendant(hasText("Short: 7")))
    composeTestRule.onNodeWithTag("personnel_days_counter_602", useUnmergedTree = true)
      .assert(androidx.compose.ui.test.hasAnyDescendant(hasText("\u200E-5\u200E")))

    // Expand first card to verify last short leave detail row renders
    composeTestRule.onNodeWithTag("personnel_item_601").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("personnel_short_leave_detail_601", useUnmergedTree = true)
      .performScrollTo()
      .assertIsDisplayed()
  }
}
