package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [Personnel::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personnelDao(): PersonnelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personnel_leaves_db"
                )
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
                        populateDatabase(database.personnelDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: PersonnelDao) {
                fun getOffsetDateMillis(daysOffset: Int): Long {
                    val c = Calendar.getInstance()
                    c.add(Calendar.DAY_OF_YEAR, daysOffset)
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    c.set(Calendar.MINUTE, 0)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    return c.timeInMillis
                }

                val seed = listOf(
                    Personnel(
                        fullName = "عبد الرحمن بلقاسمي",
                        rank = "ملازم أول",
                        registrationNumber = "DZ-160492",
                        startDateMillis = getOffsetDateMillis(-7),
                        returnDateMillis = getOffsetDateMillis(3), // باقي 3 أيام على الدخول (بالبيت)
                        isReturnedToDuty = false,
                        leaveType = "عطلة سنوية",
                        wilaya = "16 - الجزائر",
                        notes = "الكتيبة الأولى - السرية الثانية"
                    ),
                    Personnel(
                        fullName = "محمد الأمين يحياوي",
                        rank = "مساعد رئيسي",
                        registrationNumber = "DZ-318920",
                        startDateMillis = getOffsetDateMillis(-4),
                        returnDateMillis = getOffsetDateMillis(6), // باقي 6 أيام على الدخول (بالبيت)
                        isReturnedToDuty = false,
                        leaveType = "إجازة",
                        wilaya = "31 - وهران",
                        notes = "قسم التسيير الإداري"
                    ),
                    Personnel(
                        fullName = "خالد بن عيسى الدراجي",
                        rank = "رقيب أول",
                        registrationNumber = "DZ-254100",
                        startDateMillis = getOffsetDateMillis(-10),
                        returnDateMillis = getOffsetDateMillis(0), // الدخول اليوم!
                        isReturnedToDuty = false,
                        leaveType = "إجازة قصيرة",
                        wilaya = "25 - قسنطينة",
                        notes = "سلاح الإشارة والمواصلات"
                    ),
                    Personnel(
                        fullName = "ياسين بوجمعة صوالحي",
                        rank = "مساعد أول",
                        registrationNumber = "DZ-057812",
                        startDateMillis = getOffsetDateMillis(-12),
                        returnDateMillis = getOffsetDateMillis(-2), // متأخر بيومين عن الدخول!
                        isReturnedToDuty = false,
                        leaveType = "نقاهة",
                        wilaya = "05 - باتنة",
                        notes = "فصيل الحراسة والتدخل"
                    ),
                    Personnel(
                        fullName = "حمزة بن زروال الماجري",
                        rank = "عريف أول",
                        registrationNumber = "DZ-194321",
                        startDateMillis = getOffsetDateMillis(-45),
                        returnDateMillis = getOffsetDateMillis(-35), // حاضر بالخدمة منذ 35 يوماً
                        isReturnedToDuty = true,
                        leaveType = "عطلة سنوية",
                        wilaya = "19 - سطيف",
                        notes = "الإسناد اللوجيستي"
                    ),
                    Personnel(
                        fullName = "فاروق لعريبي الورتيلاني",
                        rank = "نقيب",
                        registrationNumber = "DZ-098871",
                        startDateMillis = getOffsetDateMillis(3), // إجازة مبرمجة تبدأ بعد 3 أيام
                        returnDateMillis = getOffsetDateMillis(13),
                        isReturnedToDuty = false,
                        leaveType = "إجازة",
                        wilaya = "09 - البليدة",
                        notes = "قيادة الأركان"
                    )
                )
                dao.insertAll(seed)
            }
        }
    }
}
