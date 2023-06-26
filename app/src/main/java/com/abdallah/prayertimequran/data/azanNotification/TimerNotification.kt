package com.abdallah.prayertimequran.data.azanNotification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.ui.activitys.MainActivity

class TimerNotification(title: String, content: String, context: Context,value: Long)  {
    val notificationBuilder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)

    var numMessages = 0
     var nmc : NotificationManagerCompat
    init {
        sendNotification(title , content , context , value)
        nmc = NotificationManagerCompat.from(context)

    }
    @SuppressLint("MissingPermission")
    fun refreshNotification(timerValue : StringBuilder){
        notificationBuilder.setContentText(timerValue)  // <-- your timer value
            .setNumber(++numMessages);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        nmc.notify(
            0,  // <-- Place your notification id here
            notificationBuilder.build());
    }
    private fun sendNotification(title: String, content: String, context: Context ,  value: Long) {
        val notificationChannel = NotificationChannel(
            Constants.CHANNEL_ID,
            Constants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notificationChannel)
        val notificationBuilder = createNotificationBuilder(title, content, context , value )
        val nmc = NotificationManagerCompat.from(context)


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        nmc.notify(0, notificationBuilder.build())
    }

    private fun createNotificationBuilder(
        title: String,
        content: String,
        context: Context ,
        value : Long
    ): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivities(
            context, 0, arrayOf(intent),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        notificationBuilder
            .setSmallIcon(com.abdallah.prayertimequran.R.drawable.notification_mosque)
            .setContentTitle(title)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setContentIntent(pi)
            .setContentText(content)

        return notificationBuilder
    }

}