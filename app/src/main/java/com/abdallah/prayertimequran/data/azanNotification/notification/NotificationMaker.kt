package com.abdallah.prayertimequran.data.azanNotification.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.ui.activitys.MainActivity

class NotificationMaker(
    content: String,
    title: String,
    context: Context,
    makingSound: Boolean = false,
    notificationIcon: Int,
    notificationSoundPath: Int = R.raw.azan_1,
    channelImportance: Int,
    channelName: String = Constants.CHANNEL_NAME,
    channelId: String = Constants.CHANNEL_ID,
) {
     var notificationSound: Uri

    init {
        notificationSound =
            Uri.parse(
                "android.resource://" + context.applicationContext.packageName + "/" +
                        notificationSoundPath
            )
        sendNotification(
            title,
            content,
            context,
            makingSound,
            notificationIcon,
            channelImportance,
            channelName,
            channelId
        )
    }

    private fun sendNotification(
        title: String,
        content: String,
        context: Context,
        makingSound: Boolean,
        notificationIcon: Int,
        channelImportance: Int,
        channelName: String,
        channelId: String,
    ) {
        val notificationChannel = NotificationChannel(
            channelName,
            channelId,
            channelImportance
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notificationChannel)
        val notificationBuilder =
            createNotificationBuilder(title, content, context, makingSound, notificationIcon)
        val nmc = NotificationManagerCompat.from(context)

        if (makingSound)
            notificationChannel.setSound(notificationSound, audioForAzan())
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        nmc.notify(nmc.hashCode(), notificationBuilder.build())
    }

    private fun audioForAzan() = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_ALARM)
        .build()

    private fun createNotificationBuilder(
        title: String,
        content: String,
        context: Context,
        makingSound: Boolean,
        notificationIcon: Int
    ): NotificationCompat.Builder {

        val notificationBuilder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
        notificationBuilder
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentIntent(notificationPendingIntent(context))
            .setContentText(content)
        if (makingSound)
            notificationBuilder.setSound(notificationSound)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
        return notificationBuilder
    }

    private fun notificationPendingIntent(context: Context):PendingIntent{
        return PendingIntent.getActivities(
            context, 0, arrayOf(Intent(context, MainActivity::class.java)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

}