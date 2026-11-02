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
import java.util.Calendar
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Respite Pulse", appName)
  }

  @Test
  fun `verify exactly 15 military ranks as requested`() {
    assertEquals(15, algerianRanks.size)
    val expectedRanks = listOf(
      "جندي", "عريف", "عريف أول", "رقيب", "رقيب أول",
      "مساعد", "مساعد أول", "مساعد رئيسي", "ملازم", "ملازم أول",
      "نقيب", "رائد", "مقدم", "عقيد", "عميد"
    )
    assertEquals(expectedRanks, algerianRanks)
  }

  @Test
  fun `verify exactly 69 Algerian wilayas`() {
    assertEquals(69, algerianWilayas.size)
    assertTrue(algerianWilayas.first().contains("أدرار"))
    assertTrue(algerianWilayas.last().contains("دلس"))
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
}
