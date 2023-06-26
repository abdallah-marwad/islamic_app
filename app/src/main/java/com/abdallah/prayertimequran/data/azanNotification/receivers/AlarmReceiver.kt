package com.abdallah.prayertimequran.data.azanNotification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.abdallah.prayertimequran.data.azanNotification.AlarmWorker


class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("test" , "AlarmReceiver")
        val workRequest = OneTimeWorkRequest.Builder(AlarmWorker::class.java)
            .build()
        WorkManager.getInstance(context.applicationContext).enqueue(workRequest)
    }

}