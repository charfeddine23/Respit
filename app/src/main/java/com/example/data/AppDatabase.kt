package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [Personnel::class, LeaveRecord::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personnelDao(): PersonnelDao
    abstract fun leaveRecordDao(): LeaveRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE personnel ADD COLUMN addedAtMillis INTEGER NOT NULL DEFAULT 0")
                val now = System.currentTimeMillis()
                db.execSQL("UPDATE personnel SET addedAtMillis = $now WHERE addedAtMillis = 0")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personnel_leaves_db"
                )
                    .addMigrations(MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.personnelDao(), database.leaveRecordDao())
                    }
                }
            }

            suspend fun populateDatabase(personnelDao: PersonnelDao, leaveDao: LeaveRecordDao) {
                fun getOffsetDateMillis(daysOffset: Int): Long {
                    val c = Calendar.getInstance()
                    c.add(Calendar.DAY_OF_YEAR, daysOffset)
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    c.set(Calendar.MINUTE, 0)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    return c.timeInMillis
                }

                val seedPersonnel = listOf(
                    Personnel(
                        fullName = "عبد الرحمن بلقاسمي",
                        rank = "ملازم أول - Lieutenant",
                        registrationNumber = "DZ-160492",
                        startDateMillis = getOffsetDateMillis(-7),
                        returnDateMillis = getOffsetDateMillis(3), // باقي 3 أيام على الدخول (بالبيت)
                        isReturnedToDuty = false,
                        leaveType = "عطلة سنوية",
                        wilaya = "16 - الجزائر - Alger",
                        notes = "الكتيبة الأولى - السرية الثانية"
                    ),
                    Personnel(
                        fullName = "محمد الأمين يحياوي",
                        rank = "مساعد رئيسي - Adjudant-Major",
                        registrationNumber = "DZ-318920",
                        startDateMillis = getOffsetDateMillis(-4),
                        returnDateMillis = getOffsetDateMillis(6), // باقي 6 أيام على الدخول (بالبيت)
                        isReturnedToDuty = false,
                        leaveType = "إجازة",
                        wilaya = "31 - وهران - Oran",
                        notes = "قسم التسيير الإداري"
                    ),
                    Personnel(
                        fullName = "خالد بن عيسى الدراجي",
                        rank = "رقيب أول - Sergent-Chef",
                        registrationNumber = "DZ-254100",
                        startDateMillis = getOffsetDateMillis(-10),
                        returnDateMillis = getOffsetDateMillis(0), // الدخول اليوم!
                        isReturnedToDuty = false,
                        leaveType = "إجازة قصيرة",
                        wilaya = "25 - قسنطينة - Constantine",
                        notes = "سلاح الإشارة والمواصلات"
                    ),
                    Personnel(
                        fullName = "ياسين بوجمعة صوالحي",
                        rank = "مساعد أول - Adjudant-Chef",
                        registrationNumber = "DZ-057812",
                        startDateMillis = getOffsetDateMillis(-12),
                        returnDateMillis = getOffsetDateMillis(-2), // متأخر بيومين عن الدخول!
                        isReturnedToDuty = false,
                        leaveType = "نقاهة",
                        wilaya = "05 - باتنة - Batna",
                        notes = "فصيل الحراسة والتدخل"
                    ),
                    Personnel(
                        fullName = "حمزة بن زروال الماجري",
                        rank = "عريف أول - Caporal-Chef",
                        registrationNumber = "DZ-194321",
                        startDateMillis = getOffsetDateMillis(-45),
                        returnDateMillis = getOffsetDateMillis(-35), // حاضر بالخدمة منذ 35 يوماً
                        isReturnedToDuty = true,
                        leaveType = "عطلة سنوية",
                        wilaya = "19 - سطيف - Sétif",
                        notes = "الإسناد اللوجيستي"
                    ),
                    Personnel(
                        fullName = "فاروق لعريبي الورتيلاني",
                        rank = "نقيب - Capitaine",
                        registrationNumber = "DZ-098871",
                        startDateMillis = getOffsetDateMillis(3), // إجازة مبرمجة تبدأ بعد 3 أيام
                        returnDateMillis = getOffsetDateMillis(13),
                        isReturnedToDuty = false,
                        leaveType = "إجازة",
                        wilaya = "09 - البليدة - Blida",
                        notes = "قيادة الأركان"
                    )
                )
                personnelDao.insertAll(seedPersonnel)

                val seedLeaves = listOf(
                    LeaveRecord(
                        personnelId = 1,
                        personnelName = "عبد الرحمن بلقاسمي",
                        rank = "ملازم أول - Lieutenant",
                        registrationNumber = "DZ-160492",
                        leaveType = "عطلة سنوية",
                        startDateMillis = getOffsetDateMillis(-7),
                        returnDateMillis = getOffsetDateMillis(3),
                        durationDays = 10,
                        statusText = "جارية (بالبيت)",
                        destinationWilaya = "16 - الجزائر - Alger",
                        notes = "عطلة النصف الأول من السنة"
                    ),
                    LeaveRecord(
                        personnelId = 2,
                        personnelName = "محمد الأمين يحياوي",
                        rank = "مساعد رئيسي - Adjudant-Major",
                        registrationNumber = "DZ-318920",
                        leaveType = "إجازة",
                        startDateMillis = getOffsetDateMillis(-4),
                        returnDateMillis = getOffsetDateMillis(6),
                        durationDays = 10,
                        statusText = "جارية (بالبيت)",
                        destinationWilaya = "31 - وهران - Oran",
                        notes = "إجازة عادية"
                    ),
                    LeaveRecord(
                        personnelId = 3,
                        personnelName = "خالد بن عيسى الدراجي",
                        rank = "رقيب أول - Sergent-Chef",
                        registrationNumber = "DZ-254100",
                        leaveType = "إجازة قصيرة",
                        startDateMillis = getOffsetDateMillis(-10),
                        returnDateMillis = getOffsetDateMillis(0),
                        durationDays = 10,
                        statusText = "الدخول اليوم",
                        destinationWilaya = "25 - قسنطينة - Constantine",
                        notes = "إجازة مستحقة"
                    ),
                    LeaveRecord(
                        personnelId = 4,
                        personnelName = "ياسين بوجمعة صوالحي",
                        rank = "مساعد أول - Adjudant-Chef",
                        registrationNumber = "DZ-057812",
                        leaveType = "نقاهة",
                        startDateMillis = getOffsetDateMillis(-12),
                        returnDateMillis = getOffsetDateMillis(-2),
                        durationDays = 10,
                        statusText = "متأخر عن الدخول",
                        destinationWilaya = "05 - باتنة - Batna",
                        notes = "فترة نقاهة طبية"
                    ),
                    LeaveRecord(
                        personnelId = 5,
                        personnelName = "حمزة بن زروال الماجري",
                        rank = "عريف أول - Caporal-Chef",
                        registrationNumber = "DZ-194321",
                        leaveType = "عطلة سنوية",
                        startDateMillis = getOffsetDateMillis(-45),
                        returnDateMillis = getOffsetDateMillis(-35),
                        durationDays = 10,
                        statusText = "مكتملة (مستأنف للعمل)",
                        destinationWilaya = "19 - سطيف - Sétif",
                        notes = "استأنف العمل بانتظام"
                    ),
                    LeaveRecord(
                        personnelId = 6,
                        personnelName = "فاروق لعريبي الورتيلاني",
                        rank = "نقيب - Capitaine",
                        registrationNumber = "DZ-098871",
                        leaveType = "إجازة",
                        startDateMillis = getOffsetDateMillis(3),
                        returnDateMillis = getOffsetDateMillis(13),
                        durationDays = 10,
                        statusText = "مبرمجة قريباً",
                        destinationWilaya = "09 - البليدة - Blida",
                        notes = "موافقة القيادة"
                    )
                )
                leaveDao.insertAll(seedLeaves)
            }
        }
    }
}
