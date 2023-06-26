package com.abdallah.prayertimequran.common.takePermission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.abdallah.prayertimequran.common.BuildDialog
import com.abdallah.prayertimequran.common.BuildToast
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.common.SharedPreferencesApp
import com.abdallah.prayertimequran.data.azanNotification.AlarmWorker
import com.abdallah.prayertimequran.ui.activitys.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.*

class LocationPermission {
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    fun takeLocationPermission(activity: Activity) {

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                Log.d("test", "show dialog then permisiion")
                val dialogMessage =
                    "يتم اخذ صلاحيه الوصول للموقع للتمكن من حساب مواقيت الصلاه وبدونها لن يتمكن التطبيق من حسابها ."
                val title = "صلاحيه الوصول للموقع"
                val positiveMessage = "حسنا"
                val negativeMessage = "رفض"
                BuildDialog(
                    activity, dialogMessage, title, positiveMessage, negativeMessage, {
                        showPermission(activity)
                    })
            } else {
                Log.d("test", "show permisiion without dialog")

                showPermission(activity)
            }

        } else {
            detectLocation(activity)
        }

    }

    private fun showPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.LOCATION_REQUEST_CODE
        )
    }


    @SuppressLint("MissingPermission")
    fun detectLocation(context: Activity) {
        val sharedInstance = SharedPreferencesApp.getInstance(context.application)
        Log.d("test", "detectLocation")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token


                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    BuildToast.showToast(context, " تأكد من تفعيل خدمه الموقع ثم أذهب لشاشه الإعدادات لتحديد الموقع", FancyToast.ERROR)
                    if(!sharedInstance.getBooleanFromShared(Constants.NOT_FIRST_TIME , false)){
                        context.startActivity(Intent(context, MainActivity::class.java))
                        sharedInstance.writeInShared(Constants.NOT_FIRST_TIME, true)
                        Log.d("test", "NOT_FIRST_TIME value changed")
                    }
                    Log.d("test", "Location is null LocationPermission.Class")

                } else {
                    coroutineScope.launch {
                        sharedInstance.writeInShared(Constants.LAT, "${location.latitude}")
                        sharedInstance.writeInShared(Constants.LONG, " ${location.longitude}")

//                        sharedInstance.writeInShared(Constants.LAT, "30.6701")
//                        sharedInstance.writeInShared(Constants.LONG, "31.5884")

                        sharedInstance.writeInShared(Constants.LOCATION_TOKE, true)
                        sharedInstance.writeInShared(Constants.CHANGE_CITY, true)

                        withContext(Dispatchers.Main) {
                            BuildToast.showToast(
                                context,
                                " تم تحديد المكان بنجاح قم بسحب شاشه الصلاة لأسفل للتحديث",
                                FancyToast.SUCCESS
                            )


                        }
                        Log.d("test", "SUCCESS get Location LocationPermission.Class")
                        val workRequest = OneTimeWorkRequest.Builder(AlarmWorker::class.java)
                            .build()
                        WorkManager.getInstance(context.applicationContext).enqueue(workRequest)

                        if(!sharedInstance.getBooleanFromShared(Constants.NOT_FIRST_TIME , false)){
                            context.startActivity(Intent(context, MainActivity::class.java))
                            sharedInstance.writeInShared(Constants.NOT_FIRST_TIME, true)
                            Log.d("test", "NOT_FIRST_TIME value changed")
                        }


//                        context.startService(Intent(context, AlarmService::class.java))
                    }
                }

            }


    }


}