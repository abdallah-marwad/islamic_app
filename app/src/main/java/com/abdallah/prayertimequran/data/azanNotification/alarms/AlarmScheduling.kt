package com.abdallah.prayertimequran.data.azanNotification.alarms

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context

class AlarmScheduling() : AlarmScheduler {

    override fun schedule(
        alarmTime: Long, pi: PendingIntent , application: Application
    ) {
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pi
        )
    }
    override fun scheduleInExact(
        alarmTime: Long, pi: PendingIntent , application: Application
    ) {
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pi
        )
    }

    override fun cancel(pi: PendingIntent , application: Application) {
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pi)
        pi.cancel()

    }
}