package com.abdallah.prayertimequran.data.azanNotification.receivers
import android.Manifest
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
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.Constants.CHANNEL_ID
import com.abdallah.prayertimequran.common.Constants.CHANNEL_NAME
import com.abdallah.prayertimequran.common.Constants.NOTIFICATION_CONTENT
import com.abdallah.prayertimequran.common.Constants.NOTIFICATION_TITLE_SUNRISE
import com.abdallah.prayertimequran.common.Constants.PRAYER_NAME
import com.abdallah.prayertimequran.data.azanNotification.notification.NotificationMaker
import com.abdallah.prayertimequran.ui.activitys.MainActivity
class AzanNotificationReceiver() : BroadcastReceiver() {
    lateinit var azanSound: Uri
    override fun onReceive(context: Context, intent: Intent?) {

        azanSound =
            Uri.parse("android.resource://" + context.applicationContext.packageName + "/" + com.abdallah.prayertimequran.R.raw.azan_1);

//        sendNotification("وقت صلاه $title ", "حي على الصلاه", context)

        val title = intent?.getStringExtra(PRAYER_NAME)
        val content = intent?.getStringExtra(NOTIFICATION_CONTENT)
        Log.d("test" , "on receive $title")

        var notificationSoundPath: Int
        if(title == NOTIFICATION_TITLE_SUNRISE){
            notificationSoundPath = R.raw.sunrise_sound
        }else {
            notificationSoundPath = R.raw.azan_1

        }
        NotificationMaker(
            content!!,
            title!!,
            context,
            true,
            R.drawable.notification_mosque,
            channelImportance = NotificationManager.IMPORTANCE_HIGH,
            notificationSoundPath = notificationSoundPath

        )
    }

    private fun sendNotification(title: String, content: String, context: Context) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
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
        nmc.notify(nmc.hashCode(), notificationBuilder.build())
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
            .setSmallIcon(com.abdallah.prayertimequran.R.drawable.notification_mosque)
            .setContentTitle(title)
            .setSound(azanSound)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setContentIntent(pi)
            .setContentText(content)
        return notificationBuilder
    }

}