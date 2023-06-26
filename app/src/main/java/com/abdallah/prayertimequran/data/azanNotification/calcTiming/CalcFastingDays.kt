package com.abdallah.prayertimequran.data.azanNotification.calcTiming

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class CalcFastingDays {

    fun checkMondayAndThursday(dayValue: Long): String? {
        val mondayName = "Monday"
        val thursdayName = "Thursday"

        if (mondayName == checkDayOfWeek(dayValue)) {
            Log.d("test", " غدا صيام يوم الاثنين")

            return "غدا صيام يوم الإثنين"
        } else if (thursdayName == checkDayOfWeek(dayValue)) {
            Log.d("test", " غدا صيام يوم الخميس")
            return "غدا صيام يوم الخميس"
        }
        Log.d("test", "لا يوجد صيام اثنين وخميس غدا ")
        return null
    }

    private fun checkDayOfWeek(dayValue: Long): String {
        val dayOfWeek =
            LocalDate.ofEpochDay(dayValue / (24 * 60 * 60 * 1000)).dayOfWeek // number of days since 1 jan 1970
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    }

    private fun checkDayAndMonthHijriForFasting(isMiddleMonth: Boolean, dayValue: Long): String? {
        val locale = ULocale("@calendar=islamic")
        val islamicCalendar = Calendar.getInstance(locale)
        islamicCalendar.timeInMillis = dayValue

        return if (isMiddleMonth) {
            val df2 = SimpleDateFormat(SimpleDateFormat.DAY, locale)
            df2.format(islamicCalendar.time)
        } else {
            val df2 = SimpleDateFormat(SimpleDateFormat.MONTH_DAY, locale)
            df2.format(islamicCalendar.time)
        }
    }

    fun getTheDayAndMonthName(isMiddleMonth: Boolean, dayValue: Long): String? {
        val stringDate = checkDayAndMonthHijriForFasting(isMiddleMonth, dayValue)
        if (isMiddleMonth) {
            if (stringDate == "13") {
                Log.d("test", " غدا صيام يوم 13")
                return " غدا صيام يوم 13"
            } else if (stringDate == "14") {
                Log.d("test", " غدا صيام يوم 14")
                return "غدا صيام يوم 14"
            } else if (stringDate == "15") {
                Log.d("test", " غدا صيام يوم 15")
                return "غدا صيام يوم 15"
            } else {
                Log.d("test", " نصف الشهر لا يوجد صيام")
                return null
            }
        } else {

            if (stringDate == "Dhuʻl-Hijjah1") {   // اتاكد من المسافات -- اتاكد واللغه عربي وانجليزي
                Log.d("test", "يبدأ غدا صيام التسع الأوائل من ذو الحجه")
                return "يبدأ غدا صيام التسع الأوائل من ذو الحجه"
            } else if (stringDate == "Dhuʻl-Hijjah9") {
                Log.d("test", "غدا صيام يوم 9 ذو الحجه (يوم عرفه)")

                return "غدا صيام يوم 9 ذو الحجه (يوم عرفه)"
            } else if (stringDate == "Muharram9") {
                Log.d("test", "غدا صيام يوم 9 محرم (تاسوعاء)")

                return "غدا صيام يوم 9 محرم (تاسوعاء)"
            } else if (stringDate == "Muharram10") {
                Log.d("test", "غدا صيام يوم 10 محرم (عاشوراء)")

                return "غدا صيام يوم 10 محرم (عاشوراء)"
            } else if (stringDate == "Shawwal2") {
                Log.d("test", "يبدا من غدا صيام ال6 ايام من شوال ")

                return "يبدا من غدا صيام ال6 ايام من شوال "
            }
        }
        Log.d("test", "لا يوجد صيام في الايام على مدار العام")
        return null
    }


}