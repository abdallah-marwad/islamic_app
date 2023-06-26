package com.abdallah.prayertimequran.ui.activitys

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.common.LangApp
import com.abdallah.prayertimequran.common.takePermission.LocationPermission
import com.abdallah.prayertimequran.common.SharedPreferencesApp
import com.abdallah.prayertimequran.data.azanNotification.AlarmWorker
import com.abdallah.prayertimequran.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedInstance: SharedPreferencesApp
    val job: Job = Job()
    var coroutineScope : CoroutineScope? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("test" , "onCreate Main Activity")
        checkLang()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.appbar_light_green)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedInstance = SharedPreferencesApp.getInstance(application)
        setUpNavController()

    }

    override fun onResume() {
        super.onResume()

    }
    private fun checkLang() {
       LangApp().checkLang(this)

}

private fun whenNotificationPermissionEnabled() {
    coroutineScope!!.launch {
    sharedInstance.writeInShared(Constants.NOTIFICATION_STATE, true)
    if (sharedInstance.getBooleanFromShared(Constants.LOCATION_TOKE, false)) {
        val workRequest = OneTimeWorkRequest.Builder(AlarmWorker::class.java)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
    }
}

private fun setUpNavController() {
    val navHostFragment =
        supportFragmentManager.findFragmentById(R.id.prayer_frag_container) as NavHostFragment
    binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        Constants.NOTIFICATION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                //Notificatoin Permission taken
                whenNotificationPermissionEnabled()

            } else {
                //Notificatoin Permission denied
                sharedInstance.writeInShared(Constants.NOTIFICATION_STATE, false)
            }
        }
        Constants.LOCATION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                val locationPermission = LocationPermission()

                //Location Permission taken
                Log.d("test", "location permission applied")
                locationPermission.detectLocation(this)

            }
        }
    }
}


}