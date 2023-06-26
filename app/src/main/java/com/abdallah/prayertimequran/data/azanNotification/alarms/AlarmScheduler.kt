package com.abdallah.prayertimequran.data.azanNotification.alarms

import android.app.Application
import android.app.PendingIntent

interface AlarmScheduler {
    fun schedule(alarmTime: Long, pi: PendingIntent, application: Application)
    fun scheduleInExact(alarmTime: Long, pi: PendingIntent, application: Application)
    fun cancel(pi: PendingIntent , application: Application)
}