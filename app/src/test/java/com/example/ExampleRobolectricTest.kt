package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.algerianLeaveTypes
import com.example.data.algerianRanks
import com.example.data.algerianWilayas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.LeaveRecord
import com.example.data.PersonnelRepository
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.util.AppLanguage
import com.example.util.AppUpdateManager
import com.example.util.AppUpdateState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.robolectric.Robolectric
import java.util.Calendar
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Respite Pulse", appName)
  }

  @Test
  fun `verify exactly 16 military ranks bilingual as requested`() {
    assertEquals(16, algerianRanks.size)
    val expectedRanks = listOf(
      "عميد - Général / Brigadier",
      "عقيد - Colonel",
      "مقدم - Lieutenant-Colonel",
      "رائد - Commandant",
      "نقيب - Capitaine",
      "ملازم أول - Lieutenant",
      "ملازم - Sous-Lieutenant",
      "مرشح - Aspirant / Élève-Officier",
      "مساعد رئيسي - Adjudant-Major",
      "مساعد أول - Adjudant-Chef",
      "مساعد - Adjudant",
      "رقيب أول - Sergent-Chef",
      "رقيب - Sergent",
      "عريف أول - Caporal-Chef",
      "عريف - Caporal",
      "جندي - Djoundi / Soldat"
    )
    assertEquals(expectedRanks, algerianRanks)
  }

  @Test
  fun `verify exactly 69 Algerian wilayas with bilingual names`() {
    assertEquals(69, algerianWilayas.size)
    assertTrue(algerianWilayas.first().contains("أدرار"))
    assertTrue(algerianWilayas.first().contains("Adrar"))
    assertTrue(algerianWilayas.last().contains("الأبيض سيدي الشيخ"))
    assertTrue(algerianWilayas.last().contains("El Abiodh Sidi Cheikh"))
  }

  @Test
  fun `verify exactly 4 leave and vacation types`() {
    assertEquals(4, algerianLeaveTypes.size)
    val expectedTypes = listOf("إجازة", "إجازة قصيرة", "عطلة سنوية", "نقاهة")
    assertEquals(expectedTypes, algerianLeaveTypes)
  }

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
  fun `calculate at home remaining days and all leave statuses`() {
    // 1. Person currently at home with 3 days remaining until return
    val pAtHome = Personnel(
      id = 1,
      fullName = "عبد الرحمن بلقاسمي",
      rank = "ملازم أول",
      registrationNumber = "DZ-160492",
      startDateMillis = getOffsetDateMillis(-7),
      returnDateMillis = getOffsetDateMillis(3),
      isReturnedToDuty = false
    )
    assertEquals(LeaveStatus.AT_HOME, pAtHome.status)
    assertEquals(3L, pAtHome.daysRemainingToReturn)
    assertTrue(pAtHome.isCurrentlyAtHome)
    assertTrue(pAtHome.countdownTextAr.contains("3 أيام"))

    // 2. Person returning today
    val pToday = Personnel(
      id = 2,
      fullName = "خالد بن عيسى",
      rank = "رقيب أول",
      registrationNumber = "DZ-254100",
      startDateMillis = getOffsetDateMillis(-10),
      returnDateMillis = getOffsetDateMillis(0),
      isReturnedToDuty = false
    )
    assertEquals(LeaveStatus.RETURNING_TODAY, pToday.status)
    assertEquals(0L, pToday.daysRemainingToReturn)

    // 3. Person overdue by 2 days
    val pOverdue = Personnel(
      id = 3,
      fullName = "ياسين صوالحي",
      rank = "مساعد أول",
      registrationNumber = "DZ-057812",
      startDateMillis = getOffsetDateMillis(-12),
      returnDateMillis = getOffsetDateMillis(-2),
      isReturnedToDuty = false
    )
    assertEquals(LeaveStatus.OVERDUE, pOverdue.status)
    assertEquals(2L, pOverdue.daysOverdue)
    assertTrue(pOverdue.countdownTextAr.contains("متأخر بيومين"))

    // 4. Person on duty (confirmed returned)
    val pOnDuty = Personnel(
      id = 4,
      fullName = "حمزة الماجري",
      rank = "عريف أول",
      registrationNumber = "DZ-194321",
      startDateMillis = getOffsetDateMillis(-45),
      returnDateMillis = getOffsetDateMillis(-35),
      isReturnedToDuty = true
    )
    assertEquals(LeaveStatus.ON_DUTY, pOnDuty.status)
    assertEquals(35L, pOnDuty.daysSinceDutyReturn)

    // 5. Person with upcoming leave in 4 days
    val pUpcoming = Personnel(
      id = 5,
      fullName = "فاروق لعريبي",
      rank = "نقيب",
      registrationNumber = "DZ-098871",
      startDateMillis = getOffsetDateMillis(4),
      returnDateMillis = getOffsetDateMillis(14),
      isReturnedToDuty = false
    )
    assertEquals(LeaveStatus.UPCOMING, pUpcoming.status)
  }

  @Test
  fun `verify custom duty threshold eligibility`() {
    val pDuty = Personnel(
      id = 10,
      fullName = "حمزة الماجري",
      rank = "عريف أول",
      registrationNumber = "DZ-194321",
      startDateMillis = getOffsetDateMillis(-70),
      returnDateMillis = getOffsetDateMillis(-50),
      isReturnedToDuty = true
    )
    // daysSinceDutyReturn is 50 days
    assertEquals(50L, pDuty.daysSinceDutyReturn)
    // With threshold 45 -> eligible
    assertTrue(pDuty.isEligibleForLeave(45))
    // With threshold 60 -> not yet eligible
    assertTrue(!pDuty.isEligibleForLeave(60))
    // With threshold 50 -> eligible
    assertTrue(pDuty.isEligibleForLeave(50))
  }

  @Test
  fun `verify Room database persistence for Personnel and LeaveRecord`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    val personnelDao = inMemoryDb.personnelDao()
    val leaveDao = inMemoryDb.leaveRecordDao()
    val repo = PersonnelRepository(personnelDao, leaveDao)

    // Test Personnel insertion and Flow query
    val p = Personnel(
      fullName = "أحمد القاسمي",
      rank = "ملازم",
      registrationNumber = "DZ-999000",
      startDateMillis = System.currentTimeMillis(),
      returnDateMillis = System.currentTimeMillis() + 10 * 86400000L,
      leaveType = "عطلة سنوية"
    )
    val insertedId = repo.insert(p)
    assertTrue(insertedId > 0)

    val list = repo.allPersonnel.first()
    assertEquals(1, list.size)
    assertEquals("أحمد القاسمي", list[0].fullName)

    // Test LeaveRecord insertion and Flow query
    val leave = LeaveRecord(
      personnelId = insertedId,
      personnelName = "أحمد القاسمي",
      rank = "ملازم",
      registrationNumber = "DZ-999000",
      leaveType = "عطلة سنوية",
      startDateMillis = System.currentTimeMillis(),
      returnDateMillis = System.currentTimeMillis() + 10 * 86400000L,
      durationDays = 10,
      statusText = "جارية (بالبيت)",
      destinationWilaya = "16 - الجزائر"
    )
    val leaveId = repo.insertLeave(leave)
    assertTrue(leaveId > 0)

    val leavesList = repo.allLeaves.first()
    assertEquals(1, leavesList.size)
    assertEquals("أحمد القاسمي", leavesList[0].personnelName)
    assertEquals(10L, leavesList[0].durationDays)

    inMemoryDb.close()
  }

  @Test
  fun `verify localized rank wilaya without code and countdown text`() {
    val rankRaw = "ملازم أول - Lieutenant"
    assertEquals("ملازم أول", formatRank(rankRaw, AppLanguage.ARABIC))
    assertEquals("Lieutenant", formatRank(rankRaw, AppLanguage.FRENCH))
    assertEquals("First Lieutenant", formatRank(rankRaw, AppLanguage.ENGLISH))

    val wilayaRaw = "16 - الجزائر - Alger"
    assertEquals("الجزائر", formatWilaya(wilayaRaw, AppLanguage.ARABIC, includeCode = false))
    assertEquals("Alger", formatWilaya(wilayaRaw, AppLanguage.FRENCH, includeCode = false))
    assertEquals("Alger", formatWilaya(wilayaRaw, AppLanguage.ENGLISH, includeCode = false))
    assertEquals("16 - الجزائر", formatWilaya(wilayaRaw, AppLanguage.ARABIC, includeCode = true))

    assertEquals("عطلة سنوية", formatLeaveType("عطلة سنوية", AppLanguage.ARABIC))
    assertEquals("Congé annuel", formatLeaveType("عطلة سنوية", AppLanguage.FRENCH))
    assertEquals("Annual Leave", formatLeaveType("عطلة سنوية", AppLanguage.ENGLISH))

    val pAtHome = Personnel(
      fullName = "Test",
      rank = rankRaw,
      registrationNumber = "DZ-100",
      startDateMillis = getOffsetDateMillis(-2),
      returnDateMillis = getOffsetDateMillis(4),
      isReturnedToDuty = false
    )
    assertTrue(pAtHome.getCountdownText(AppLanguage.ARABIC).contains("4"))
    assertTrue(pAtHome.getCountdownText(AppLanguage.FRENCH).contains("4 jours restants"))
    assertTrue(pAtHome.getCountdownText(AppLanguage.ENGLISH).contains("4 days remaining"))
  }

  @Test
  fun `verify MainActivity launches on SDK 34 without requestCode 16-bit crash`() {
    val controller = Robolectric.buildActivity(MainActivity::class.java)
    controller.create().start().resume()
    val activity = controller.get()
    assertTrue(!activity.isFinishing)
    controller.pause().stop().destroy()
  }

  @Test
  fun `verify AppUpdateManager version comparison and GitHub release parsing`() {
    assertTrue(AppUpdateManager.compareVersions("1.43.0", "1.42.4") > 0)
    assertTrue(AppUpdateManager.compareVersions("1.43.0", "1.42.99") > 0)
    assertTrue(AppUpdateManager.compareVersions("v2.0.0", "1.99.9") > 0)
    assertEquals(0, AppUpdateManager.compareVersions("v1.42.4", "1.42.4"))
    assertTrue(AppUpdateManager.compareVersions("1.42.3", "1.42.4") < 0)

    val releaseJson = JSONObject().apply {
      put("tag_name", "v1.43.0")
      put("name", "Respite Pulse v1.43.0")
      put("body", "Bug fixes and performance improvements")
      put(
        "assets",
        JSONArray().put(
          JSONObject().apply {
            put("name", "RespitePulse.v1.43.0.apk")
            put("browser_download_url", "https://github.com/charfeddine23/Respit/releases/download/v1.43.0/RespitePulse.v1.43.0.apk")
            put("size", 12_582_912L)
          }
        )
      )
    }

    val newerState = AppUpdateManager.parseReleaseObject(releaseJson, "1.42.4")
    assertTrue(newerState is AppUpdateState.UpdateAvailable)
    val available = newerState as AppUpdateState.UpdateAvailable
    assertEquals("1.43.0", available.versionName)
    assertEquals("RespitePulse.v1.43.0.apk", available.fileName)
    assertTrue(available.downloadUrl.startsWith("https://github.com/charfeddine23/Respit"))

    val sameVersionState = AppUpdateManager.parseReleaseObject(releaseJson, "1.43.0")
    assertTrue(sameVersionState is AppUpdateState.UpToDate)
  }

  @Test
  fun `verify LeaveNotificationHelper dispatches overdue and grouped notifications`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    org.robolectric.Shadows.shadowOf(app).grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
    val list = listOf(
      Personnel(
        id = 101,
        fullName = "أحمد منصور",
        rank = "نقيب - Capitaine",
        registrationNumber = "DZ-771100",
        startDateMillis = getOffsetDateMillis(-14),
        returnDateMillis = getOffsetDateMillis(-2),
        isReturnedToDuty = false,
        wilaya = "16 - الجزائر - Alger"
      ),
      Personnel(
        id = 102,
        fullName = "سليم بن علي",
        rank = "ملازم - Sous-Lieutenant",
        registrationNumber = "DZ-772200",
        startDateMillis = getOffsetDateMillis(-10),
        returnDateMillis = getOffsetDateMillis(0),
        isReturnedToDuty = false,
        wilaya = "31 - وهران - Oran"
      )
    )
    val sent = com.example.util.LeaveNotificationHelper.checkApproachingLeaves(app, list, "ar")
    assertEquals(2, sent)
  }

  @Test
  fun `verify leave records remain immutable even after personnel clearAndReset`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    val repo = PersonnelRepository(inMemoryDb.personnelDao(), inMemoryDb.leaveRecordDao())
    val start = getOffsetDateMillis(-5)
    val ret = getOffsetDateMillis(5)

    val p = Personnel(
      fullName = "كريم بن صالح",
      rank = "رائد - Commandant",
      registrationNumber = "DZ-551122",
      startDateMillis = start,
      returnDateMillis = ret,
      isReturnedToDuty = false,
      leaveType = "عطلة سنوية",
      wilaya = "16 - الجزائر - Alger"
    )
    val pid = repo.insert(p)
    repo.insertLeave(
      LeaveRecord(
        personnelId = pid,
        personnelName = p.fullName,
        rank = p.rank,
        registrationNumber = p.registrationNumber,
        leaveType = p.leaveType,
        startDateMillis = start,
        returnDateMillis = ret,
        durationDays = 10,
        statusText = "في إجازة",
        destinationWilaya = p.wilaya
      )
    )

    assertEquals(1, repo.countExactLeave(pid, start, ret))
    assertEquals(1, repo.countExactLeaveByReg("DZ-551122", start, ret))

    // Clearing personnel table or calling clearAndReset must NEVER wipe leave records
    repo.clearPersonnel()
    repo.clearAndReset(emptyList())
    val survivingLeaves = repo.allLeaves.first()
    assertEquals(1, survivingLeaves.size)
    assertEquals("كريم بن صالح", survivingLeaves[0].personnelName)

    inMemoryDb.close()
  }

  @Test
  fun `verify No Screenshot FLAG_SECURE disabled by default and toggleable`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val settings = com.example.data.SettingsManager(context)
    assertTrue(!settings.isNoScreenshotEnabled)

    val controller = Robolectric.buildActivity(MainActivity::class.java)
    controller.create().start().resume()
    val activity = controller.get()
    val hasSecureFlagInitially = (activity.window.attributes.flags and android.view.WindowManager.LayoutParams.FLAG_SECURE) != 0
    assertTrue(!hasSecureFlagInitially)
    controller.pause().stop().destroy()

    settings.isNoScreenshotEnabled = true
    assertTrue(settings.isNoScreenshotEnabled)

    val securedController = Robolectric.buildActivity(MainActivity::class.java)
    securedController.create().start().resume()
    val securedActivity = securedController.get()
    val hasSecureFlagWhenEnabled = (securedActivity.window.attributes.flags and android.view.WindowManager.LayoutParams.FLAG_SECURE) != 0
    assertTrue(hasSecureFlagWhenEnabled)
    securedController.pause().stop().destroy()

    settings.isNoScreenshotEnabled = false
    assertTrue(!settings.isNoScreenshotEnabled)
  }

  @Test
  fun `verify ExcelXlsxExporter generates valid A4 landscape xlsx workbook`() {
    val start = getOffsetDateMillis(-4)
    val ret = getOffsetDateMillis(6)
    val personnel = listOf(
      Personnel(
        id = 1L,
        fullName = "سفيان بلقاسم & <أمين>",
        rank = "نقيب - Capitaine",
        registrationNumber = "DZ-880112",
        startDateMillis = start,
        returnDateMillis = ret,
        isReturnedToDuty = false,
        leaveType = "عطلة سنوية",
        wilaya = "16 - الجزائر - Alger",
        notes = "ملاحظة اختبارية 🎖️\u0001\u000B"
      )
    )
    val leaves = listOf(
      LeaveRecord(
        id = 1L,
        personnelId = 1L,
        personnelName = "سفيان بلقاسم",
        rank = "نقيب - Capitaine",
        registrationNumber = "DZ-880112",
        leaveType = "عطلة سنوية",
        startDateMillis = start,
        returnDateMillis = ret,
        durationDays = 10L,
        statusText = "جارية (بالبيت)",
        destinationWilaya = "16 - الجزائر - Alger",
        notes = "نص & \"اقتباس\" 'مفرد'"
      )
    )

    val expectedWorkbookOrder = listOf("bookViews", "sheets")
    val expectedStylesOrder = listOf(
      "fonts", "fills", "borders", "cellStyleXfs", "cellXfs", "cellStyles", "dxfs", "tableStyles"
    )
    val expectedWorksheetOrder = listOf(
      "sheetPr", "dimension", "sheetViews", "sheetFormatPr", "cols",
      "sheetData", "autoFilter", "mergeCells", "pageMargins", "pageSetup", "headerFooter"
    )

    val dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance().apply {
      isNamespaceAware = true
    }

    fun childElementNames(xml: String): List<String> {
      val doc = dbf.newDocumentBuilder().parse(org.xml.sax.InputSource(java.io.StringReader(xml)))
      val children = doc.documentElement.childNodes
      val names = mutableListOf<String>()
      for (i in 0 until children.length) {
        val node = children.item(i)
        if (node is org.w3c.dom.Element) {
          names.add(node.localName ?: node.nodeName)
        }
      }
      return names
    }

    AppLanguage.entries.forEach { lang ->
      listOf(
        personnel to leaves,
        emptyList<Personnel>() to emptyList()
      ).forEach { (pList, lList) ->
        val bytes = com.example.util.ExcelXlsxExporter.generateWorkbookBytes(
          personnelList = pList,
          leaveRecords = lList,
          lang = lang
        )
        assertTrue(bytes.isNotEmpty())

        val entries = mutableMapOf<String, String>()
        java.util.zip.ZipInputStream(java.io.ByteArrayInputStream(bytes)).use { zis ->
          var entry = zis.nextEntry
          while (entry != null) {
            entries[entry.name] = zis.readBytes().toString(Charsets.UTF_8)
            entry = zis.nextEntry
          }
        }

        val requiredEntries = listOf(
          "[Content_Types].xml",
          "_rels/.rels",
          "xl/workbook.xml",
          "xl/_rels/workbook.xml.rels",
          "xl/styles.xml",
          "xl/worksheets/sheet1.xml",
          "xl/worksheets/sheet2.xml"
        )
        requiredEntries.forEach { key ->
          assertTrue("Missing $key", entries.containsKey(key))
          // Ensure every XML entry is well-formed XML 1.0
          dbf.newDocumentBuilder().parse(org.xml.sax.InputSource(java.io.StringReader(entries[key]!!)))
        }

        assertEquals(expectedWorkbookOrder, childElementNames(entries["xl/workbook.xml"]!!))
        assertEquals(expectedStylesOrder, childElementNames(entries["xl/styles.xml"]!!))

        val sheet1 = entries["xl/worksheets/sheet1.xml"]!!
        val sheet2 = entries["xl/worksheets/sheet2.xml"]!!
        assertEquals(expectedWorksheetOrder, childElementNames(sheet1))
        assertEquals(expectedWorksheetOrder, childElementNames(sheet2))

        val expectedRtl = if (lang.isRtl) "rightToLeft=\"1\"" else "rightToLeft=\"0\""
        assertTrue(sheet1.contains("paperSize=\"9\""))
        assertTrue(sheet1.contains("orientation=\"landscape\""))
        assertTrue(sheet1.contains("fitToWidth=\"1\""))
        assertTrue(sheet1.contains(expectedRtl))
        assertTrue(sheet1.contains("<selection pane=\"bottomLeft\" activeCell=\"A4\" sqref=\"A4\"/>"))

        assertTrue(sheet2.contains("paperSize=\"9\""))
        assertTrue(sheet2.contains("orientation=\"landscape\""))
        assertTrue(sheet2.contains("fitToWidth=\"1\""))
        assertTrue(sheet2.contains(expectedRtl))

        if (pList.isNotEmpty()) {
          assertTrue(sheet1.contains("DZ-880112"))
          assertTrue(sheet1.contains("&amp; &lt;أمين&gt;"))
          assertTrue(sheet1.contains("🎖️"))
          assertTrue(!sheet1.contains("\u0001"))
          assertTrue(!sheet1.contains("\u000B"))
          assertTrue(sheet2.contains("DZ-880112"))
        }
      }
    }
  }

  @Test
  fun `verify duty counting starts from last leave return date or addedAtMillis when no leaves exist`() {
    val added50DaysAgo = getOffsetDateMillis(-50)
    val lastLeaveStart = getOffsetDateMillis(-25)
    val lastLeaveReturn15DaysAgo = getOffsetDateMillis(-15)
    val today = getOffsetDateMillis(0)

    // Case 1: Person has NO leave records in database -> count starts from addedAtMillis (50 days)
    val personWithoutLeaves = Personnel(
      id = 10L,
      fullName = "عمر بن يوسف",
      rank = "رقيب - Sergent",
      registrationNumber = "DZ-101010",
      startDateMillis = getOffsetDateMillis(-5),
      returnDateMillis = getOffsetDateMillis(-2),
      isReturnedToDuty = true,
      addedAtMillis = added50DaysAgo
    )
    val noLeaveInfo = personWithoutLeaves.computeDutyReferenceInfo(emptyList(), today)
    assertEquals(com.example.data.ReferenceDateSource.PERSONNEL_ADDED_DATE, noLeaveInfo.source)
    assertEquals(added50DaysAgo, noLeaveInfo.referenceDateMillis)
    assertEquals(50L, noLeaveInfo.daysSinceReference)
    assertTrue(personWithoutLeaves.isEligibleForLeave(45, emptyList(), today))

    // Case 2: Person HAS leave records -> count starts from returnDateMillis of latest leave (15 days)
    val leaves = listOf(
      LeaveRecord(
        id = 1L,
        personnelId = 99L, // even if personnelId changed, registrationNumber matches
        personnelName = "عمر بن يوسف",
        rank = "رقيب - Sergent",
        registrationNumber = "DZ-101010",
        leaveType = "إجازة",
        startDateMillis = getOffsetDateMillis(-40),
        returnDateMillis = getOffsetDateMillis(-30),
        durationDays = 10L
      ),
      LeaveRecord(
        id = 2L,
        personnelId = 10L,
        personnelName = "عمر بن يوسف",
        rank = "رقيب - Sergent",
        registrationNumber = "DZ-101010",
        leaveType = "عطلة سنوية",
        startDateMillis = lastLeaveStart,
        returnDateMillis = lastLeaveReturn15DaysAgo,
        durationDays = 10L
      )
    )
    val withLeaveInfo = personWithoutLeaves.computeDutyReferenceInfo(leaves, today)
    assertEquals(com.example.data.ReferenceDateSource.LAST_LEAVE_RETURN, withLeaveInfo.source)
    assertEquals(lastLeaveReturn15DaysAgo, withLeaveInfo.referenceDateMillis)
    assertEquals(15L, withLeaveInfo.daysSinceReference)
    assertTrue(!personWithoutLeaves.isEligibleForLeave(45, leaves, today))
  }

  @Test
  fun `verify LeaveAnalyticsCalculator monthly rank and per-personnel annual statistics`() {
    fun dateInYear(year: Int, monthZeroBased: Int, day: Int): Long {
      return Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, monthZeroBased)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }.timeInMillis
    }

    val targetYear = 2026
    val today = dateInYear(2026, Calendar.JUNE, 20)

    val p1 = Personnel(
      id = 1L,
      fullName = "أحمد منصور",
      rank = "نقيب - Capitaine",
      registrationNumber = "DZ-111",
      startDateMillis = dateInYear(2026, Calendar.MARCH, 1),
      returnDateMillis = dateInYear(2026, Calendar.MARCH, 11),
      isReturnedToDuty = true,
      addedAtMillis = dateInYear(2025, Calendar.JANUARY, 10)
    )
    val p2 = Personnel(
      id = 2L,
      fullName = "سمير بن علي",
      rank = "رقيب أول - Sergent-Chef",
      registrationNumber = "DZ-222",
      startDateMillis = dateInYear(2026, Calendar.JANUARY, 1),
      returnDateMillis = dateInYear(2026, Calendar.JANUARY, 1),
      isReturnedToDuty = true,
      addedAtMillis = dateInYear(2026, Calendar.APRIL, 1) // 80 days before June 20
    )

    val leaves = listOf(
      LeaveRecord(
        id = 1L,
        personnelId = 1L,
        personnelName = p1.fullName,
        rank = p1.rank,
        registrationNumber = p1.registrationNumber,
        leaveType = "إجازة",
        startDateMillis = dateInYear(2026, Calendar.JANUARY, 5),
        returnDateMillis = dateInYear(2026, Calendar.JANUARY, 12),
        durationDays = 7L
      ),
      LeaveRecord(
        id = 2L,
        personnelId = 1L,
        personnelName = p1.fullName,
        rank = p1.rank,
        registrationNumber = p1.registrationNumber,
        leaveType = "عطلة سنوية",
        startDateMillis = dateInYear(2026, Calendar.MARCH, 1),
        returnDateMillis = dateInYear(2026, Calendar.MARCH, 11),
        durationDays = 10L
      ),
      LeaveRecord(
        id = 3L,
        personnelId = 1L,
        personnelName = p1.fullName,
        rank = p1.rank,
        registrationNumber = p1.registrationNumber,
        leaveType = "نقاهة",
        startDateMillis = dateInYear(2025, Calendar.DECEMBER, 1),
        returnDateMillis = dateInYear(2025, Calendar.DECEMBER, 15),
        durationDays = 14L
      )
    )

    val report = com.example.data.LeaveAnalyticsCalculator.computeReport(
      personnelList = listOf(p1, p2),
      allLeaves = leaves,
      selectedYear = targetYear,
      dutyThresholdDays = 45,
      todayMillis = today
    )

    assertEquals(2, report.totalLeavesInYear)
    assertEquals(17L, report.totalDaysInYear)
    assertEquals(1, report.monthlyStats[Calendar.JANUARY].leaveCount)
    assertEquals(7L, report.monthlyStats[Calendar.JANUARY].totalDays)
    assertEquals(1, report.monthlyStats[Calendar.MARCH].leaveCount)
    assertEquals(10L, report.monthlyStats[Calendar.MARCH].totalDays)
    assertEquals(1, report.rankStats.size)
    assertEquals("نقيب - Capitaine", report.rankStats[0].rankRaw)
    assertEquals(100f, report.rankStats[0].percentage, 0.01f)

    val p1Annual = report.personnelAnnualStats.first { it.registrationNumber == "DZ-111" }
    assertEquals(2, p1Annual.leavesCountInYear)
    assertEquals(17L, p1Annual.totalDaysInYear)
    assertEquals(com.example.data.ReferenceDateSource.LAST_LEAVE_RETURN, p1Annual.referenceSource)
    assertEquals(dateInYear(2026, Calendar.MARCH, 11), p1Annual.referenceDateMillis)

    val p2Annual = report.personnelAnnualStats.first { it.registrationNumber == "DZ-222" }
    assertEquals(0, p2Annual.leavesCountInYear)
    assertEquals(0L, p2Annual.totalDaysInYear)
    assertEquals(com.example.data.ReferenceDateSource.PERSONNEL_ADDED_DATE, p2Annual.referenceSource)
    assertEquals(80L, p2Annual.daysSinceReference)
    assertTrue(p2Annual.isEligible)
  }

  @Test
  fun `verify Room MIGRATION_4_5 preserves existing personnel and populates addedAtMillis`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val dbFile = context.getDatabasePath("test_mig_4_5.db")
    if (dbFile.exists()) dbFile.delete()

    val sqliteDb = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbFile, null)
    sqliteDb.execSQL(
      """
      CREATE TABLE IF NOT EXISTS personnel (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        fullName TEXT NOT NULL,
        rank TEXT NOT NULL,
        registrationNumber TEXT NOT NULL,
        startDateMillis INTEGER NOT NULL,
        returnDateMillis INTEGER NOT NULL,
        isReturnedToDuty INTEGER NOT NULL,
        leaveType TEXT NOT NULL,
        wilaya TEXT NOT NULL,
        notes TEXT NOT NULL
      )
      """.trimIndent()
    )
    sqliteDb.execSQL(
      """
      CREATE TABLE IF NOT EXISTS leave_records (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        personnelId INTEGER NOT NULL,
        personnelName TEXT NOT NULL,
        rank TEXT NOT NULL,
        registrationNumber TEXT NOT NULL,
        leaveType TEXT NOT NULL,
        startDateMillis INTEGER NOT NULL,
        returnDateMillis INTEGER NOT NULL,
        durationDays INTEGER NOT NULL,
        statusText TEXT NOT NULL,
        destinationWilaya TEXT NOT NULL,
        notes TEXT NOT NULL,
        createdAtMillis INTEGER NOT NULL
      )
      """.trimIndent()
    )
    sqliteDb.execSQL(
      """
      INSERT INTO personnel (fullName, rank, registrationNumber, startDateMillis, returnDateMillis, isReturnedToDuty, leaveType, wilaya, notes)
      VALUES ('ياسين بلقاسم', 'ملازم - Sous-Lieutenant', 'DZ-4040', 1000, 2000, 1, 'إجازة', '16 - الجزائر - Alger', '')
      """.trimIndent()
    )
    sqliteDb.version = 4
    sqliteDb.close()

    val migratedDb = Room.databaseBuilder(context, AppDatabase::class.java, "test_mig_4_5.db")
      .addMigrations(AppDatabase.MIGRATION_4_5, AppDatabase.MIGRATION_5_6, AppDatabase.MIGRATION_6_7)
      .allowMainThreadQueries()
      .build()

    val list = runBlocking { migratedDb.personnelDao().getAllPersonnel().first() }
    assertEquals(1, list.size)
    assertEquals("ياسين بلقاسم", list[0].fullName)
    assertTrue(list[0].addedAtMillis > 0L)
    migratedDb.close()
    dbFile.delete()
  }

  @Test
  fun `verify ThemeEngine provides all 60 named themes and SettingsManager persists selection`() {
    val expectedNames = listOf(
      "Monokai", "Monokai Pro", "Solarized Dark", "Solarized Light", "One Dark Pro",
      "One Light", "Dracula", "Tomorrow Night Eighties", "Nord", "Night Owl",
      "Bluloco Dark", "Ayu Dark", "Ayu Mirage", "Ayu Light", "Winter is Coming",
      "Zenburn", "SynthWave '84", "Shades of Purple", "LaserWave", "Abyss",
      "Cobalt2", "Red", "Poimandres", "Hopscotch", "Gruvbox Dark",
      "Gruvbox Light", "Oceanic Next", "Base16 Eighties", "Tomorrow Night Blue", "Material Theme (Oceanic)",
      "Material Theme (Palenight)", "Material Theme (Darker)", "Catppuccin (Mocha)", "Kanagawa", "Rose Pine",
      "GitHub Dark", "GitHub Light", "Panda Theme", "Atom One Dark", "Tokyo Night",
      "Obsidian Liquid Glass", "Frosted Arctic Glass", "Cyber Neon Liquid", "Smoked Quartz Glass", "Midnight Velvet Glass",
      "Aurora Borealis Glass", "Crystal Prism Liquid", "Deep Ocean Glass", "Amber Mercury Glass", "Titanium Mist Glass",
      "Holographic Liquid Glass", "Carbon Blur Glass", "Emerald Translucent Glass", "Ultraviolet Frost Glass", "Sapphire Liquid Glass",
      "Pearlescent Dark Glass", "Ruby Refractive Glass", "Chromatic Aberration Glass", "Liquid Mercury Dark", "Bioluminescent Glass"
    )
    assertEquals(60, expectedNames.size)

    val themes = com.example.ui.theme.ThemeEngine.allThemes
    assertEquals(60, themes.size)
    assertEquals(expectedNames, themes.map { it.name })
    assertEquals(60, themes.map { it.id }.distinct().size)

    assertEquals(20, themes.count { it.isLiquidGlass })
    assertEquals(8, themes.count { !it.isDark })

    val context = ApplicationProvider.getApplicationContext<Context>()
    val settings = com.example.data.SettingsManager(context)
    settings.selectedThemeEngineId = "cyber_neon_liquid"
    val reloaded = com.example.data.SettingsManager(context)
    assertEquals("cyber_neon_liquid", reloaded.selectedThemeEngineId)
    val found = com.example.ui.theme.ThemeEngine.findById(reloaded.selectedThemeEngineId)
    assertEquals("Cyber Neon Liquid", found?.name)
    assertTrue(found?.isLiquidGlass == true)
    assertTrue(found?.isDark == true)

    settings.selectedThemeEngineId = ""
  }

  @Test
  fun `verify days counter counts negative during leave (-5 to 0) then positive normally`() {
    val startDate = getOffsetDateMillis(-5)
    val returnDate = getOffsetDateMillis(5)
    val onLeave = Personnel(
      id = 77L,
      fullName = "سعيد بن عمر",
      rank = "رقيب أول - Sergent-Chef",
      registrationNumber = "DZ-770011",
      startDateMillis = startDate,
      returnDateMillis = returnDate,
      isReturnedToDuty = false
    )
    val leaveRecord = LeaveRecord(
      id = 1L,
      personnelId = 77L,
      personnelName = onLeave.fullName,
      rank = onLeave.rank,
      registrationNumber = onLeave.registrationNumber,
      leaveType = "إجازة",
      startDateMillis = startDate,
      returnDateMillis = returnDate,
      durationDays = 10L
    )

    // 5 days before return -> -5
    assertEquals(-5L, onLeave.daysSinceDutyReturn)
    assertEquals(-5L, onLeave.computeDutyReferenceInfo(listOf(leaveRecord), getOffsetDateMillis(0)).daysSinceReference)

    // 1 day before return -> -1
    assertEquals(-1L, onLeave.computeDutyReferenceInfo(listOf(leaveRecord), getOffsetDateMillis(4)).daysSinceReference)

    // On return date -> 0
    assertEquals(0L, onLeave.computeDutyReferenceInfo(listOf(leaveRecord), getOffsetDateMillis(5)).daysSinceReference)

    // 1 day after return -> 1, 3 days after -> 3
    val returned = onLeave.copy(isReturnedToDuty = true)
    assertEquals(1L, returned.computeDutyReferenceInfo(listOf(leaveRecord), getOffsetDateMillis(6)).daysSinceReference)
    assertEquals(3L, returned.computeDutyReferenceInfo(listOf(leaveRecord), getOffsetDateMillis(8)).daysSinceReference)
  }

  @Test
  fun `verify SettingsManager persists Living Breathing Luxury mode`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val settings = com.example.data.SettingsManager(context)
    settings.isLivingLuxuryMode = false
    assertTrue(!settings.isLivingLuxuryMode)

    settings.isLivingLuxuryMode = true
    val reloaded = com.example.data.SettingsManager(context)
    assertTrue(reloaded.isLivingLuxuryMode)

    settings.isLivingLuxuryMode = false
  }

  @Test
  fun `verify Room Trash Bin soft delete restore and emptyTrash persistence`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val repo = PersonnelRepository(inMemoryDb.personnelDao(), inMemoryDb.leaveRecordDao())

    val id1 = repo.insert(
      Personnel(
        fullName = "وليد بن سالم",
        rank = "رقيب - Sergent",
        registrationNumber = "DZ-301",
        startDateMillis = getOffsetDateMillis(-5),
        returnDateMillis = getOffsetDateMillis(5)
      )
    )
    val id2 = repo.insert(
      Personnel(
        fullName = "أمين تواتي",
        rank = "نقيب - Capitaine",
        registrationNumber = "DZ-302",
        startDateMillis = getOffsetDateMillis(-8),
        returnDateMillis = getOffsetDateMillis(2)
      )
    )
    assertEquals(2, repo.allPersonnel.first().size)
    assertEquals(0, repo.trashedCount.first())

    val p1 = repo.allPersonnel.first().first { it.id == id1 }
    val p2 = repo.allPersonnel.first().first { it.id == id2 }

    // Move first to trash -> active list has 1, trash has 1
    repo.moveToTrash(p1.id)
    assertEquals(1, repo.allPersonnel.first().size)
    assertEquals(1, repo.trashedCount.first())
    assertEquals(p1.id, repo.trashedPersonnel.first().first().id)

    // Restore from trash -> active list back to 2, trash empty
    repo.restoreFromTrash(p1.id)
    assertEquals(2, repo.allPersonnel.first().size)
    assertEquals(0, repo.trashedCount.first())

    // Move both to trash, then permanently empty trash
    repo.moveToTrash(p1.id)
    repo.moveToTrash(p2.id)
    assertEquals(0, repo.allPersonnel.first().size)
    assertEquals(2, repo.trashedCount.first())

    repo.emptyTrash()
    assertEquals(0, repo.allPersonnel.first().size)
    assertEquals(0, repo.trashedCount.first())

    inMemoryDb.close()
  }

  @Test
  fun `verify 0_5s hold and directional swipe delete rules for Arabic Left and English Right`() {
    assertEquals(500L, com.example.ui.components.SwipeDeleteGestureHelper.HOLD_ARM_DURATION_MS)

    // Arabic (RTL): swipe LEFT (negative X) deletes; positive right drag is clamped to 0
    assertEquals(-1f, com.example.ui.components.SwipeDeleteGestureHelper.expectedDirectionSign(AppLanguage.ARABIC))
    assertEquals(0f, com.example.ui.components.SwipeDeleteGestureHelper.coerceDirectionalOffset(260f, AppLanguage.ARABIC))
    assertEquals(-320f, com.example.ui.components.SwipeDeleteGestureHelper.coerceDirectionalOffset(-320f, AppLanguage.ARABIC))
    assertTrue(com.example.ui.components.SwipeDeleteGestureHelper.shouldTriggerDelete(-320f, 900f, AppLanguage.ARABIC))
    assertTrue(!com.example.ui.components.SwipeDeleteGestureHelper.shouldTriggerDelete(320f, 900f, AppLanguage.ARABIC))
    assertTrue(!com.example.ui.components.SwipeDeleteGestureHelper.shouldTriggerDelete(-120f, 900f, AppLanguage.ARABIC))

    // English & French (LTR): swipe RIGHT (positive X) deletes; negative left drag is clamped to 0
    listOf(AppLanguage.ENGLISH, AppLanguage.FRENCH).forEach { lang ->
      assertEquals(1f, com.example.ui.components.SwipeDeleteGestureHelper.expectedDirectionSign(lang))
      assertEquals(0f, com.example.ui.components.SwipeDeleteGestureHelper.coerceDirectionalOffset(-260f, lang))
      assertEquals(320f, com.example.ui.components.SwipeDeleteGestureHelper.coerceDirectionalOffset(320f, lang))
      assertTrue(com.example.ui.components.SwipeDeleteGestureHelper.shouldTriggerDelete(320f, 900f, lang))
      assertTrue(!com.example.ui.components.SwipeDeleteGestureHelper.shouldTriggerDelete(-320f, 900f, lang))
      assertTrue(!com.example.ui.components.SwipeDeleteGestureHelper.shouldTriggerDelete(120f, 900f, lang))
    }
  }
}



