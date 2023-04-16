package com.example.prayertimequran.common.takePermission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.example.prayertimequran.common.BuildDialog
import com.example.prayertimequran.common.Constants

class NotificationPermission() {


     fun takeNotificationPermission(activity:  Activity ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if(shouldShowRequestPermissionRationale(activity , Manifest.permission.POST_NOTIFICATIONS)) {
                   Log.d("test" , "show dialog then permisiion")
                    val dialogMessage = "يتم اخذ صلاحيه ارسال اشعارات للتمكن من تنبيهك في معاد الاذان ."
                    val title =  "صلاحيه الاشعارات"
                    val positiveMessage = "حسنا"
                    val negativeMessage = "رفض"
                   BuildDialog(activity , dialogMessage , title ,positiveMessage,negativeMessage ,
                     {
                        showPermission(activity)
                    })
                }else{
                    Log.d("test" , "show noticcation permission ")
                    showPermission(activity)
                }

            }
        }


    }

    private fun showPermission(activity : Activity){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            Constants.NOTIFICATION_REQUEST_CODE
        )
    }
}