package com.example

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityUpgradeRobolectricTest {

  @get:Rule
  val composeTestRule = createEmptyComposeRule()

  @Test
  fun `MainActivity launches and renders upgraded v7 database without crashing`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val dbFile = context.getDatabasePath("personnel_leaves_db")
    dbFile.parentFile?.mkdirs()
    if (dbFile.exists()) dbFile.delete()

    val sqliteDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
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
        notes TEXT NOT NULL,
        addedAtMillis INTEGER NOT NULL DEFAULT 0,
        isPinned INTEGER NOT NULL DEFAULT 0,
        pinnedAtMillis INTEGER NOT NULL DEFAULT 0,
        isDeleted INTEGER NOT NULL DEFAULT 0,
        deletedAtMillis INTEGER NOT NULL DEFAULT 0
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
      INSERT INTO personnel (fullName, rank, registrationNumber, startDateMillis, returnDateMillis, isReturnedToDuty, leaveType, wilaya, notes, addedAtMillis, isPinned, pinnedAtMillis, isDeleted, deletedAtMillis)
      VALUES ('رشيد بن علي', 'نقيب - Capitaine', 'DZ-777001', 1700000000000, 1700864000000, 1, 'إجازة', '16 - الجزائر - Alger', '', 1700000000000, 0, 0, 0, 0)
      """.trimIndent()
    )
    sqliteDb.execSQL(
      """
      INSERT INTO leave_records (personnelId, personnelName, rank, registrationNumber, leaveType, startDateMillis, returnDateMillis, durationDays, statusText, destinationWilaya, notes, createdAtMillis)
      VALUES (1, 'رشيد بن علي', 'نقيب - Capitaine', 'DZ-777001', 'إجازة', 1700000000000, 1700864000000, 10, 'مكتملة', '16 - الجزائر - Alger', '', 1700000000000)
      """.trimIndent()
    )
    sqliteDb.version = 7
    sqliteDb.close()

    ActivityScenario.launch(MainActivity::class.java).use { scenario ->
      composeTestRule.waitForIdle()
      assertEquals(Lifecycle.State.RESUMED, scenario.state)

      composeTestRule.waitUntil(timeoutMillis = 5_000L) {
        composeTestRule.onAllNodesWithTag("dashboard_lazy_column").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithTag("dashboard_lazy_column")
        .performScrollToNode(hasTestTag("personnel_item_1"))
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithText("رشيد بن علي").assertIsDisplayed()
    }
  }
}
