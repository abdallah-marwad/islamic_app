package com.abdallah.prayertimequran.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abdallah.prayertimequran.data.models.quran.Aya
import com.abdallah.prayertimequran.data.models.quran.SavedPage


@Database(entities = [Aya::class , SavedPage::class ], version = 1)
abstract class QuranDB : RoomDatabase() {

        abstract fun quranSoraDao(): QuranSoraDao?
        abstract fun quranJozzDao(): QuranJozzDao?
        abstract fun quranSearchDao(): QuranSearchDao?
        abstract fun savedPageDao(): SavedPageDoa
        abstract fun tfseerDao(): TfseerDao

        companion object {
            private var instance: QuranDB? = null
//            private val MIGRATION1_2: Migration = object : Migration(1, 2) {
//                override fun migrate(database: SupportSQLiteDatabase) {
//                    // Create the new table
//                    database.execSQL("CREATE TABLE alarm (id INTEGER PRIMARY KEY AUTOINCREMENT" +
//                            ", alarm_name TEXT NOT NULL , " +
//                            "alarm_date TEXT, repetition INTEGER , note TEXT)")
//                }
//            }
            fun getInstance(context: Context): QuranDB? {
                if (instance == null) {
                    synchronized(QuranDB::class.java) {
                        if (instance == null) {
                            try {
                                val DATABASE_NAME = "quran.db"
                                instance = Room.databaseBuilder(
                                    context.applicationContext,
                                    QuranDB::class.java, DATABASE_NAME
                                )
                                    .createFromAsset("quran/databases/quran.db")

                                    .build()
                            } catch (e: Exception) {
                                return null
                            }
                        }
                    }
                }
                return instance
            }
        }


    }
