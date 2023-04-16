package com.example.prayertimequran.ui.activitys

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.prayertimequran.R
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.LangApp
import com.example.prayertimequran.common.takePermission.LocationPermission
import com.example.prayertimequran.common.SharedPreferencesApp
import com.example.prayertimequran.data.azanNotification.AlarmService
import com.example.prayertimequran.databinding.ActivityMainBinding
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
        coroutineScope= CoroutineScope(Dispatchers.IO + job)
    }
    private fun checkLang() {
       LangApp().checkLang(this)

}

private fun whenNotificationPermissionEnabled() {
    coroutineScope!!.launch {
    sharedInstance.writeInShared(Constants.NOTIFICATION_STATE, true)
    if (sharedInstance.getBooleanFromShared(Constants.LOCATION_TOKE, false)) {
        startService(
            Intent(
                this@MainActivity,
                AlarmService::class.java
            )
        )
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

    override fun onStop() {
        super.onStop()
        coroutineScope = null
    }
}