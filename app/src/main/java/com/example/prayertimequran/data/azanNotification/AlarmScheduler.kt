package com.example.prayertimequran.data.azanNotification

import android.app.Application
import android.app.PendingIntent

interface AlarmScheduler {
    fun schedule(alarmTime: Long, pi: PendingIntent, application: Application)
    fun cancel(pi: PendingIntent , application: Application)
}