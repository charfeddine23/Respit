package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import com.example.data.DutyReferenceInfo
import com.example.data.Personnel
import com.example.data.ReferenceDateSource
import com.example.ui.DashboardStats
import com.example.ui.screens.EntryLandingScreen
import com.example.ui.screens.selectEntryLandingPersonnel
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w412dp-h915dp")
class EntryLandingScreenRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun offsetMillis(days: Int): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, days)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private fun samplePersonnel(): Pair<List<Personnel>, Map<Long, DutyReferenceInfo>> {
        val p1 = Personnel(
            id = 1L,
            fullName = "أمين بن صالح",
            fullNameLatin = "Amine Bensalah",
            rank = "عريف - Caporal",
            registrationNumber = "1.2026.00001",
            startDateMillis = offsetMillis(-20),
            returnDateMillis = offsetMillis(-10),
            isReturnedToDuty = true,
            isPinned = false
        )
        val p2 = Personnel(
            id = 2L,
            fullName = "يوسف مرابط",
            fullNameLatin = "Youcef Merabet",
            rank = "رقيب - Sergent",
            registrationNumber = "1.2026.00002",
            startDateMillis = offsetMillis(-65),
            returnDateMillis = offsetMillis(-55),
            isReturnedToDuty = true,
            isPinned = false
        )
        val p3 = Personnel(
            id = 3L,
            fullName = "سفيان بلعيد",
            fullNameLatin = "Sofiane Belaid",
            rank = "نقيب - Capitaine",
            registrationNumber = "1.2026.00003",
            startDateMillis = offsetMillis(-50),
            returnDateMillis = offsetMillis(-40),
            isReturnedToDuty = true,
            isPinned = false
        )
        val p4 = Personnel(
            id = 4L,
            fullName = "كريم زروقي",
            fullNameLatin = "Karim Zerrouki",
            rank = "ملازم - Sous-Lieutenant",
            registrationNumber = "1.2026.00004",
            startDateMillis = offsetMillis(-35),
            returnDateMillis = offsetMillis(-25),
            isReturnedToDuty = true,
            isPinned = false
        )
        val p5 = Personnel(
            id = 5L,
            fullName = "عمر حداد",
            fullNameLatin = "Omar Haddad",
            rank = "جندي - Djoundi / Soldat",
            registrationNumber = "1.2026.00005",
            startDateMillis = offsetMillis(-15),
            returnDateMillis = offsetMillis(-5),
            isReturnedToDuty = true,
            isPinned = true,
            pinnedAtMillis = 1_000L
        )

        val list = listOf(p1, p2, p3, p4, p5)
        val refs = mapOf(
            1L to DutyReferenceInfo(offsetMillis(-10), ReferenceDateSource.LAST_LEAVE_RETURN, 10L),
            2L to DutyReferenceInfo(offsetMillis(-55), ReferenceDateSource.LAST_LEAVE_RETURN, 55L),
            3L to DutyReferenceInfo(offsetMillis(-40), ReferenceDateSource.LAST_LEAVE_RETURN, 40L),
            4L to DutyReferenceInfo(offsetMillis(-25), ReferenceDateSource.LAST_LEAVE_RETURN, 25L),
            5L to DutyReferenceInfo(offsetMillis(-5), ReferenceDateSource.LAST_LEAVE_RETURN, 5L)
        )
        return list to refs
    }

    @Test
    fun `selectEntryLandingPersonnel puts pinned first then highest days descending and caps at 4`() {
        val (list, refs) = samplePersonnel()

        val result = selectEntryLandingPersonnel(
            personnelList = list,
            dutyReferenceMap = refs,
            searchQuery = "",
            lang = AppLanguage.ARABIC
        )

        assertEquals(4, result.size)
        // Pinned person (5L, 5 days) comes first, followed by highest days descending: 2L (55d), 3L (40d), 4L (25d)
        // Lowest-days unpinned 1L (10d) is excluded by the 4-person cap.
        assertEquals(listOf(5L, 2L, 3L, 4L), result.map { it.id })
    }

    @Test
    fun `EntryLandingScreen renders search bar max 4 ordered cards '+' quick add and See more bottom window`() {
        val (list, refs) = samplePersonnel()
        var query by mutableStateOf("")
        var quickAddedId: Long? = null
        var seeMoreClicked = false

        composeRule.setContent {
            MyApplicationTheme {
                EntryLandingScreen(
                    personnelList = list,
                    dutyReferenceMap = refs,
                    stats = DashboardStats(total = 5, atHome = 1, onDuty = 4, eligible = 2),
                    searchQuery = query,
                    onSearchQueryChange = { query = it },
                    onQuickAddLeave = { quickAddedId = it.id },
                    onOpenPersonnelHistory = {},
                    onTogglePin = {},
                    onAddPersonnel = {},
                    onSeeMore = { seeMoreClicked = true },
                    currentLang = AppLanguage.ARABIC,
                    isDarkMode = false,
                    animationsEnabled = false
                )
            }
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("entry_landing_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("entry_search_field").assertIsDisplayed()
        composeRule.onNodeWithTag("entry_see_more_card").assertIsDisplayed()
        composeRule.onNodeWithText("See more").assertIsDisplayed()

        // Max 4 cards shown: 5L (pinned), 2L (55d), 3L (40d), 4L (25d); 1L (10d) hidden
        composeRule.onNodeWithTag("entry_personnel_card_5").assertIsDisplayed()
        composeRule.onNodeWithTag("entry_personnel_card_2").assertIsDisplayed()
        composeRule.onNodeWithTag("entry_personnel_card_3").assertIsDisplayed()
        composeRule.onNodeWithTag("entry_personnel_card_4").assertIsDisplayed()
        composeRule.onNodeWithTag("entry_personnel_card_1").assertDoesNotExist()

        // Verify vertical order: pinned (5) above highest days (2) above next (3)
        val b5 = composeRule.onNodeWithTag("entry_personnel_card_5").getBoundsInRoot()
        val b2 = composeRule.onNodeWithTag("entry_personnel_card_2").getBoundsInRoot()
        val b3 = composeRule.onNodeWithTag("entry_personnel_card_3").getBoundsInRoot()
        assertTrue(b5.bottom <= b2.top + 1.dp)
        assertTrue(b2.bottom <= b3.top + 1.dp)

        // Clicking '+' button triggers quick-add leave for that person
        composeRule.onNodeWithTag("entry_plus_btn_2").performClick()
        assertEquals(2L, quickAddedId)

        // Filtering via search bar surfaces matching 5th person (1L)
        composeRule.onNodeWithTag("entry_search_field").performTextInput("أمين")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("entry_personnel_card_1").assertIsDisplayed()
        composeRule.onNodeWithTag("entry_personnel_card_2").assertDoesNotExist()

        // Clicking calm bottom "See more" window enters the full app
        composeRule.onNodeWithTag("entry_see_more_card").performClick()
        assertTrue(seeMoreClicked)
    }
}
