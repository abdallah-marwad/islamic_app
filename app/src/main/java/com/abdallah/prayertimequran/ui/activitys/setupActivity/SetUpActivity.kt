package com.abdallah.prayertimequran.ui.activitys.setupActivity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.*
import com.abdallah.prayertimequran.common.takePermission.LocationPermission
import com.abdallah.prayertimequran.common.takePermission.NotificationPermission
import com.abdallah.prayertimequran.databinding.ActivitySetupBinding
import com.abdallah.prayertimequran.ui.activitys.MainActivity
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SetUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding
    private lateinit var sharedInstance: SharedPreferencesApp
    private lateinit var presenter: SetupPresenter
    private lateinit var locationPermission: LocationPermission
    private lateinit var countries: Array<String>
    private var selectCountry: Boolean = false
    private var calcMethod =""
    private val job: Job = Job()
    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLang()
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.appbar_light_green)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        coroutineScope = CoroutineScope(Dispatchers.IO + job)

        presenter = SetupPresenter(application)
        sharedInstance = SharedPreferencesApp.getInstance(application)
        // if Api <33 it always true
        writeInShared(Constants.NOTIFICATION_STATE, true)
        takeNotificationPermission()


        binding.setUp.setOnClickListener {
            if (selectCountry) {
                takeLocationPermission()
            } else {
                BuildToast.showToast(this, " قم بتحديد الدوله اولا", FancyToast.ERROR)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setUpSpinner()
        spinnerOnClick()
    }



    private fun setUpSpinner() {
        countries = resources.getStringArray(R.array.countries)
        val arrayAdapter = ArrayAdapter(this, R.layout.custom_dropdown_menu, countries)
        binding.spinner.adapter = arrayAdapter
    }


    private fun changeSwitchesEnabledState(
        state: Boolean,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch { sharedInstance.writeInShared(Constants.ENABLE_FJR, state) }
        coroutineScope.launch { sharedInstance.writeInShared(Constants.ENABLE_SUNRISE, state) }
        coroutineScope.launch { sharedInstance.writeInShared(Constants.ENABLE_DOHR, state) }
        coroutineScope.launch { sharedInstance.writeInShared(Constants.ENABLE_ASR, state) }
        coroutineScope.launch {
            sharedInstance.writeInShared(Constants.ENABLE_MAGHREB, state)
        }
        coroutineScope.launch {
            sharedInstance.writeInShared(Constants.ENABLE_ISHA, state)
        }
    }

        private fun spinnerOnClick() {
        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                when (p0!!.getItemAtPosition(p2).toString()) {
                    countries[1] -> {

                        selectCountry = true
                        calcMethod= Constants.EGYPT
                    }
                    countries[2] -> {

                        selectCountry = true
                        calcMethod=Constants.QATAR
                    }
                    countries[3] -> {

                        selectCountry = true
                        calcMethod=Constants.DUBAI
                    }
                    countries[4] -> {

                        selectCountry = true
                        calcMethod=Constants.UMM_AL_QURA
                    }
                    countries[5] -> {

                        selectCountry = true
                        calcMethod=Constants.SINGAPORE
                    }
                    countries[6] -> {

                        selectCountry = true
                        calcMethod=Constants.NORTH_AMERICA
                    }
                    countries[7] -> {

                        selectCountry = true
                        calcMethod=Constants.KUWAIT
                    }
                    countries[8] -> {
                        selectCountry = true
                        calcMethod=Constants.MUSLIM_WORLD_LEAGUE

                    }
                    countries[9] -> {
                        selectCountry = true
                        calcMethod=Constants.MOON_SIGHTING_COMMITTEE
                    }
                    countries[10] -> {
                        calcMethod=Constants.KARACHI
                        selectCountry = true
                    }
                    countries[11] -> {
                        calcMethod=Constants.OTHER
                        selectCountry = true
                    }
                    else -> {}
                }
                writeInShared(Constants.CALC_METHOD, calcMethod)
                Log.d("test", "country choosen is : $calcMethod  setup.class")

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun checkLang() {
        LangApp().checkLang(this)
    }

    private fun takeNotificationPermission() {
        val notificationPermission = NotificationPermission()
        notificationPermission.takeNotificationPermission(this)
    }

    private fun takeLocationPermission() {
        locationPermission = LocationPermission()
        locationPermission.takeLocationPermission(this)
    }


    private fun <T> writeInShared(varName: String, varValue: T) {
        presenter.setDataToShared(varName, varValue)
    }

    private fun whenNotificationPermissionEnabled() {
        writeInShared(Constants.NOTIFICATION_STATE, true)


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
                    coroutineScope!!.launch {
                        whenNotificationPermissionEnabled()
                    }

                } else {
                    //Notificatoin Permission denied
                    coroutineScope!!.launch {
                        writeInShared(Constants.NOTIFICATION_STATE, false)
                        changeSwitchesEnabledState(false , this)
                    }
                }
            }

            Constants.LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    binding.setUp.visibility = View.INVISIBLE
                    binding.lottieLoading.visibility = View.VISIBLE
                    coroutineScope!!.launch {

                        //Location Permission taken
                        locationPermission.detectLocation(this@SetUpActivity)
                        if (sharedInstance.getBooleanFromShared(
                                Constants.NOTIFICATION_STATE,
                                false
                            )
                        ) {
                            Log.d("test", "switchers Enabled setup.Class")

                            changeSwitchesEnabledState(true,  this)
                        }



                    }

                } else {
                    //Location Permission denied
                    coroutineScope!!.launch {
                        writeInShared(Constants.LOCATION_TOKE, false)
                        writeInShared(Constants.NOT_FIRST_TIME, true)
                        changeSwitchesEnabledState(false, this)
                        startActivity(Intent(this@SetUpActivity, MainActivity::class.java))
                    }
                }
            }
        }
    }
}
