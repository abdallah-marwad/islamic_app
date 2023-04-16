package com.example.prayertimequran.data.azanNotification

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmScheduling() : AlarmScheduler {

    override fun schedule(
        alarmTime: Long, pi: PendingIntent , application: Application
    ) {
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pi
        )
    }

    override fun cancel(pi: PendingIntent , application: Application) {
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pi)

    }
}