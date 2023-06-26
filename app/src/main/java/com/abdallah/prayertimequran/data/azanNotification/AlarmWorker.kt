package com.abdallah.prayertimequran.data.azanNotification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.common.MyApplication
import com.abdallah.prayertimequran.common.SharedPreferencesApp
import com.abdallah.prayertimequran.data.azanNotification.alarms.AlarmScheduler
import com.abdallah.prayertimequran.data.azanNotification.alarms.AlarmScheduling
import com.abdallah.prayertimequran.data.azanNotification.calcTiming.CalcFastingDays
import com.abdallah.prayertimequran.data.azanNotification.calcTiming.PrayerTimesCal
import com.abdallah.prayertimequran.data.azanNotification.receivers.AlarmReceiver
import com.abdallah.prayertimequran.data.azanNotification.receivers.AzanNotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class AlarmWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    var sharedPreferences: SharedPreferencesApp? = null
    var prayerTimes: PrayerTimesCal? = null
    private val job: Job = Job()
    var coroutineScope: CoroutineScope? = CoroutineScope(Dispatchers.IO + job)
    private var long: Double = -1.0
    private var lat: Double = -1.0
    private var mapOfPrayers = mutableMapOf<String, Long?>()
    private var alarm: AlarmScheduler = AlarmScheduling()

    override fun doWork(): Result {
        Log.d("test", "doWork Started")
        val applicationContext = applicationContext
        val application = applicationContext as MyApplication
        sharedPreferences = SharedPreferencesApp.getInstance(application)
        prayerTimes = PrayerTimesCal(application)
        intiLocation()
        if (long != -1.0 && lat != -1.0) {
            prayerTimes?.initialPrayerTimes(lat, long)
            getPrayerTimeFromMap()
            alarmToRefreshEachDay()
            if (getBooleanFromShared(Constants.NOTIFICATION_STATE)) {
                makeAlarmToEachPrayer()
            }
        } else {
            return Result.failure()
        }
        return Result.success()
    }


    private fun alarmToRefreshEachDay() {
        val todayDate = getDateOfSpecificDayWithoutHours(System.currentTimeMillis())
        val alarmNextDayValue =
            Date(todayDate + Constants.DAY_VALUE).time

        Log.d("test", "new refresh at  : ${Date(alarmNextDayValue)} AlarmWorker.class")
        cancelThenMakeAlarmForRefreshing(alarmNextDayValue)


//        if (makeFastingAlarm(todayDate, mapOfPrayers[Constants.ISHA]!! + Constants.HALF_HOUR))
//            makeFastingAlarm(alarmNextDayValue)
    }

    private fun makeFastingAlarm(today: Long, ishaDay: Long): Boolean {
        return today > ishaDay
    }

    private fun getDateOfSpecificDayWithoutHours(dateValue: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateValue
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    private fun getFastingDayWithMakingAlar(dayValue: Long): String? {
        val calcFastingDays = CalcFastingDays()
        val monAndThur = calcFastingDays.checkMondayAndThursday(dayValue)
        val monthMiddle = calcFastingDays.getTheDayAndMonthName(true, dayValue)
        val allFastingDays = calcFastingDays.getTheDayAndMonthName(false, dayValue)
        return monAndThur + monthMiddle + allFastingDays
    }

    private fun makeFastingAlarm(dayValue: Long) {
        val fastingDays = getFastingDayWithMakingAlar(dayValue)
        if (fastingDays?.isNotEmpty() == true) {
            cancelAlarm(startReceiverFasting(fastingDays, canceling = true))
            makeAlarmForFasting(
                mapOfPrayers[Constants.ISHA]!! + Constants.HALF_HOUR,
                startReceiverFasting(fastingDays, canceling =false)!!
            )
        }

    }

    private fun cancelThenMakeAlarmForRefreshing(alarmValue: Long) {
        cancelAlarm(refreshPendingIntent(true))
        makeAlarm(alarmValue, refreshPendingIntent(false)!!)
    }

    private fun makeAlarmToEachPrayer() {
        cancelAlarm(azanPendingIntent(Constants.FJR, Constants.NOTIFICATION_TITLE_FJR, "حي على الصلاه (موعد اذكار الصباح)", true))
        cancelAlarm(azanPendingIntent(Constants.SUNRISE, Constants.NOTIFICATION_TITLE_SUNRISE, "", true))
        cancelAlarm(azanPendingIntent(Constants.DOHR, Constants.NOTIFICATION_TITLE_DOHR, "حي على الصلاه", true))
        cancelAlarm(azanPendingIntent(Constants.ASR,  Constants.NOTIFICATION_TITLE_ASR, "حي على الصلاه", true))
        cancelAlarm(azanPendingIntent(Constants.MAGHREB,  Constants.NOTIFICATION_TITLE_MAGREB,"حي على الصلاه (موعد اذكار المساء)",  true))
        cancelAlarm(azanPendingIntent(Constants.ISHA,  Constants.NOTIFICATION_TITLE_ISHA, "حي على الصلاه", true))


        if (Date().time <= mapOfPrayers[Constants.FJR]!! && getBooleanFromShared(Constants.ENABLE_FJR)) {
            makeAlarm(
                mapOfPrayers[Constants.FJR]!!,
                azanPendingIntent(Constants.FJR,
                    Constants.NOTIFICATION_TITLE_FJR,
                    "حي على الصلاه (موعد اذكار الصباح)",
                    false)!!
            )
            Log.d("test", "Alarm FJR")
        }
        if (Date().time <= mapOfPrayers[Constants.SUNRISE]!! && getBooleanFromShared(Constants.ENABLE_DOHR)) {
            Log.d("test", "Alarm Sunrise")
            makeAlarm(
                mapOfPrayers[Constants.SUNRISE]!!,
                azanPendingIntent(Constants.SUNRISE,
                    Constants.NOTIFICATION_TITLE_SUNRISE, "",
                    false)!!
            )
        }
        if (Date().time <= mapOfPrayers[Constants.DOHR]!! && getBooleanFromShared(Constants.ENABLE_DOHR)) {
            Log.d("test", "Alarm Duhr")
            makeAlarm(
                mapOfPrayers[Constants.DOHR]!!,
                azanPendingIntent(Constants.DOHR, Constants.NOTIFICATION_TITLE_DOHR,"حي على الصلاه",  false)!!
            )
        }
        if (Date().time <= mapOfPrayers[Constants.ASR]!! && getBooleanFromShared(Constants.ENABLE_ASR)) {

            makeAlarm(
                mapOfPrayers[Constants.ASR]!!,
                azanPendingIntent(Constants.ASR, Constants.NOTIFICATION_TITLE_ASR, "حي على الصلاه", false)!!
            )
            Log.d("test", "Alarm ASR")
        }
        if (Date().time <= mapOfPrayers[Constants.MAGHREB]!! && getBooleanFromShared(Constants.ENABLE_MAGHREB)) {

            makeAlarm(
                mapOfPrayers[Constants.MAGHREB]!!,
                azanPendingIntent(Constants.MAGHREB, Constants.NOTIFICATION_TITLE_MAGREB,"حي على الصلاه (موعد اذكار المساء)",  false)!!
            )
            Log.d("test", "Alarm MAGHREB")
        }
        if (Date().time <= mapOfPrayers[Constants.ISHA]!! && getBooleanFromShared(Constants.ENABLE_ISHA)) {

            makeAlarm(
                mapOfPrayers[Constants.ISHA]!!,
                azanPendingIntent(Constants.ISHA, Constants.NOTIFICATION_TITLE_ISHA,"حي على الصلاه",  false)!!
            )
            Log.d("test", "Alarm ISHA")
        }
    }

    private fun getLocationDataFromShared(location: String) =
        sharedPreferences!!.getStringFromShared(location, "-1.0")

    private fun getBooleanFromShared(varName: String) =
        sharedPreferences!!.getBooleanFromShared(varName, true)

    private fun getPrayerTimeFromMap() {
        mapOfPrayers = prayerTimes!!.mapOfPrayers


    }

    private fun prayerRequestCode(name: String): String {
        var prayerHashCode = ""
        if (name == Constants.FJR) {
            prayerHashCode = Constants.FJR_HASH_CODE
        } else if (name == Constants.SUNRISE) {
            prayerHashCode = Constants.SUNRISE_HASH_CODE
        } else if (name == Constants.DOHR) {
            prayerHashCode = Constants.DOHR_HASH_CODE
        } else if (name == Constants.ASR) {
            prayerHashCode = Constants.ASR_HASH_CODE
        } else if (name == Constants.MAGHREB) {
            prayerHashCode = Constants.MAGHREB_HASH_CODE
        } else if (name == Constants.ISHA) {
            prayerHashCode = Constants.ISHA_HASH_CODE
        }
        return prayerHashCode

    }


    private fun getBroadCastPendingIntent(
        application: MyApplication,
        requestCode: Int,
        intent: Intent
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            application, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun notificationPrayerPendingIntent(
        canceling: Boolean,
        intent: Intent,
        application: MyApplication,
        prayerName: String
    ): PendingIntent? {
        return if (!canceling) {
            val requestCode = intent.hashCode()
            Log.d("test", "request code of $prayerName ${intent.hashCode()}  AlarmWorker.class")
            sharedPreferences!!.writeInShared(prayerRequestCode(prayerName), requestCode)
            getBroadCastPendingIntent(application, requestCode, intent)

        } else {
            val requestCodeFromShared =
                sharedPreferences!!.getIntFromShared(prayerRequestCode(prayerName), 0)
            Log.d(
                "test",
                "cancel $prayerName request code of $requestCodeFromShared  AlarmWorker.class"
            )

            if (requestCodeFromShared == 0) {
                return null
            }
            getBroadCastPendingIntent(application, requestCodeFromShared, intent)

        }
    }

    private fun azanPendingIntent(
        prayerName: String,
        prayerNameArabic: String,
        notificationContent: String,
        canceling: Boolean
    ): PendingIntent? {
        val application = applicationContext as MyApplication

        val intent = Intent(application, AzanNotificationReceiver::class.java).apply {
            putExtra(Constants.PRAYER_NAME, prayerNameArabic)
            putExtra(Constants.NOTIFICATION_CONTENT, notificationContent)
//            putExtra(Constants.NOTIFICATION_TYPE, Constants.NOTIFICATION_AZAN)
        }

        return notificationPrayerPendingIntent(canceling, intent, application, prayerName)
    }

    private fun startReceiverFasting(
        fastingDayName: String,
        notificationContent: String = "",
        canceling: Boolean
    ): PendingIntent? {
        val application = applicationContext as MyApplication

        val intent = Intent(application, AzanNotificationReceiver::class.java).apply {
            putExtra(Constants.PRAYER_NAME, fastingDayName)
            putExtra(Constants.NOTIFICATION_CONTENT, notificationContent)

        }

        return notificationFastingPendingIntent(application, intent, canceling)
    }

    private fun notificationFastingPendingIntent(
        application: MyApplication,
        intent: Intent,
        canceling: Boolean,

        ): PendingIntent? {
        return if (!canceling) {
            val requestCode = intent.hashCode()
            Log.d("test", "request code of fasting day ${intent.hashCode()}  AlarmWorker.class")
            sharedPreferences!!.writeInShared(Constants.FASTING_DAY_REQ_CODE, requestCode)
            getBroadCastPendingIntent(application, requestCode, intent)

        } else {
            val requestCodeFromShared =
                sharedPreferences!!.getIntFromShared(Constants.FASTING_DAY_REQ_CODE, 0)
            Log.d(
                "test",
                "cancel fasting day request code : $requestCodeFromShared  AlarmWorker.class"
            )

            if (requestCodeFromShared == 0) {
                return null
            }
            getBroadCastPendingIntent(application, requestCodeFromShared, intent)

        }
    }


    private fun refreshPendingIntent(canceling: Boolean): PendingIntent? {
        val application = applicationContext as MyApplication
        val intent = Intent(application, AlarmReceiver::class.java)

        return if (!canceling) {
            val requestCode = intent.hashCode()
            sharedPreferences!!.writeInShared(Constants.REFRESH_REQUEST_TXT, requestCode)

            getBroadCastPendingIntent(application,requestCode,intent)

        } else {
            val requestCodeFromShared =
                sharedPreferences!!.getIntFromShared(Constants.REFRESH_REQUEST_TXT, 0)
            if (requestCodeFromShared == 0) {
                return null
            }
            getBroadCastPendingIntent(application,requestCodeFromShared,intent)
        }

    }

    private fun cancelAlarm(pi: PendingIntent?) {
        val application = applicationContext as MyApplication

        if (pi != null) {
            alarm.cancel(pi, application)
        }
    }

    private fun intiLocation() {
        long = getLocationDataFromShared(Constants.LONG)!!.toDouble()
        lat = getLocationDataFromShared(Constants.LAT)!!.toDouble()
    }

    private fun makeAlarm(
        alarmTime: Long, pi: PendingIntent
    ) {
        val application = applicationContext as MyApplication
        alarm.schedule(alarmTime, pi, application)
    }

    private fun makeAlarmForFasting(
        alarmTime: Long, pi: PendingIntent
    ) {
        val application = applicationContext as MyApplication
        alarm.scheduleInExact(alarmTime, pi, application)
    }


}