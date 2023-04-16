package com.example.prayertimequran.data.azanNotification

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.Constants.CHANNEL_ID
import com.example.prayertimequran.common.Constants.CHANNEL_NAME
import com.example.prayertimequran.common.Constants.PRAYER_NAME
import com.example.prayertimequran.ui.activitys.MainActivity
import java.io.File


class AzanNotificationReceiver() : BroadcastReceiver() {
    lateinit var azanSound: Uri
    override fun onReceive(context: Context, intent: Intent?) {

        azanSound =
            Uri.parse("android.resource://" + context.applicationContext.packageName + "/" + com.example.prayertimequran.R.raw.azan_1);
        val prayerName = intent?.getStringExtra(PRAYER_NAME)
        sendNotification("وقت صلاه $prayerName ", "حي على الصلاه", context)

    }

    private fun sendNotification(title: String, content: String, context: Context) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        notificationChannel.setSound(azanSound, audioAttributes)
        manager.createNotificationChannel(notificationChannel)
        val notificationBuilder = createNotificationBuilder(title, content, context)
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
        context: Context
    ): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivities(
            context, 0, arrayOf(intent),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        notificationBuilder
            .setSmallIcon(com.example.prayertimequran.R.drawable.notification_mosque)
            .setContentTitle(title)
            .setSound(azanSound)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setContentIntent(pi)
            .setContentText(content).priority = NotificationCompat.PRIORITY_HIGH
        return notificationBuilder
    }

}