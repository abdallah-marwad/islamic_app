package com.example.prayertimequran.data.azanNotification

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.os.IBinder
import android.util.Log
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.SharedPreferencesApp
import com.example.prayertimequran.data.models.alarm.PrayerTimesCal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AlarmService : Service() {
    var sharedPreferences: SharedPreferencesApp? = null
    var prayerTimes: PrayerTimesCal? = null
    private val job: Job = Job()
    var coroutineScope: CoroutineScope? = CoroutineScope(Dispatchers.IO + job)
    private var long: Double = -1.0
    private var lat: Double = -1.0
    private var mapOfPrayers = mutableMapOf<String, Long?>()
    private var alarm: AlarmScheduler = AlarmScheduling()

    override fun onCreate() {
        super.onCreate()
        Log.d("test", "Service OnCreate ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope!!.launch {
            sharedPreferences = SharedPreferencesApp.getInstance(application)
            prayerTimes = PrayerTimesCal(application)
            intiLocation()
            if (long != -1.0 && lat != -1.0) {
                prayerTimes?.initialPrayerTimes(lat, long)
                getPrayerTimeFromMap()
                alarmToRefreshAfterEachIsha()
                if (getBooleanFromShared(Constants.NOTIFICATION_STATE)) {
                    makeAlarmToEachPrayer()
                }

                stopSelf()
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun alarmToRefreshAfterEachIsha() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val alarmValue = Date(calendar.timeInMillis + 86400000).time
        Log.d("test" , "new refresh at  : ${Date(alarmValue)}")
        makeAlarm(alarmValue, servicePendingIntent(5))
    }

    private fun makeAlarmToEachPrayer() {
        if (Date().time <= mapOfPrayers[Constants.FJR]!! && getBooleanFromShared(Constants.ENABLE_FJR)) {
            makeAlarm(
                mapOfPrayers[Constants.FJR]!!,
                reciverPendingIntent(Constants.FJR_REQUEST_CODE, "الفجر")
            )
            Log.d("test", "Alarm FJR")
        }
        if (Date().time <= mapOfPrayers[Constants.DOHR]!! && getBooleanFromShared(Constants.ENABLE_DOHR)) {
            Log.d("test", "Alarm Duhr")
            makeAlarm(
                mapOfPrayers[Constants.DOHR]!!,
                reciverPendingIntent(Constants.DOHR_REQUEST_CODE, "الظهر")
            )
        }
        if (Date().time <= mapOfPrayers[Constants.ASR]!! && getBooleanFromShared(Constants.ENABLE_ASR)) {
            makeAlarm(
                mapOfPrayers[Constants.ASR]!!,
                reciverPendingIntent(Constants.ASR_REQUEST_CODE, "العصر")
            )
            Log.d("test", "Alarm ASR")
        }
        if (Date().time <= mapOfPrayers[Constants.MAGHREB]!! && getBooleanFromShared(Constants.ENABLE_MAGHREB)) {
            makeAlarm(
                mapOfPrayers[Constants.MAGHREB]!!,
                reciverPendingIntent(Constants.MAGHREB_REQUEST_CODE, "المغرب")
            )
            Log.d("test", "Alarm MAGHREB")
        }
        if (Date().time <= mapOfPrayers[Constants.ISHA]!! && getBooleanFromShared(Constants.ENABLE_ISHA)) {
            makeAlarm(
                mapOfPrayers[Constants.ISHA]!!,
                reciverPendingIntent(Constants.ISHA_REQUEST_CODE, "العشاء")
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

    private fun reciverPendingIntent(requestCode: Int, prayerNameArabic: String): PendingIntent {
        val intent = Intent(application, AzanNotificationReceiver::class.java).apply {
            putExtra(Constants.PRAYER_NAME, prayerNameArabic)
        }
        return PendingIntent.getBroadcast(
            application, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun servicePendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(application, AlarmService::class.java)

        return PendingIntent.getService(
            application, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun intiLocation() {
        long = getLocationDataFromShared(Constants.LONG)!!.toDouble()
        lat = getLocationDataFromShared(Constants.LAT)!!.toDouble()
    }

    private fun makeAlarm(
        alarmTime: Long, pi: PendingIntent
    ) {
        alarm.schedule(alarmTime, pi, application)
    }


    override fun onDestroy() {
        super.onDestroy()
        coroutineScope = null
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}