package com.example.prayertimequran.ui.fragments.prayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.example.prayertimequran.R
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.MyApplication
import com.example.prayertimequran.common.SharedPreferencesApp
import com.example.prayertimequran.common.UserLocation
import com.example.prayertimequran.databinding.FragmentPrayerBinding
import com.google.android.gms.location.FusedLocationProviderClient
import java.text.DateFormat
import java.util.*

class PrayerFragment : Fragment(R.layout.fragment_prayer) {
    private lateinit var binding: FragmentPrayerBinding
    private lateinit var userLocation: UserLocation
    private lateinit var sharedPreferences : SharedPreferences
    private val LOCATION_TOKE = "location toke"
    private val  FJR = "fjr"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
//        sharedPreferences.edit().clear().commit()
        takePermissionAndSetPrayerTime()
    }

    private fun takePermissionAndSetPrayerTime() {
        setIsLocationToke(false)
        if (checkPermissionsIsToke()) {
            Log.d("test" , "Permission is taken")
            getLocation()
        } else {
            val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                        setIsLocationToke(true)
                        getLocation()

                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                        setIsLocationToke(true)
                        getLocation()
                    }
                    else -> {
                        setIsLocationToke(false)
                    }
                }

            }
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setIsLocationToke(toke: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(LOCATION_TOKE, toke)
            Log.d("test" , "Done Change Shared Value To ${toke}")
            apply() // may error here
        }
    }
    private fun setPrayerTimesInShared(prayerName: String , prayerTime : String) {
        with(sharedPreferences.edit()) {
            putString(prayerName, prayerTime)
            Log.d("test" , "the value of  $prayerName is : $prayerTime  ")

            apply() // may error here
        }
    }

    private fun checkPermissionsIsToke(): Boolean =
        sharedPreferences.getBoolean(LOCATION_TOKE, false)

    private fun getPrayerTimeFromShared(prayerName : String): String? =
        sharedPreferences.getString(prayerName, "0:0")


    private fun getLocation() {
        Log.d("test" , "in getLocation")
        userLocation = UserLocation()
        try {
            userLocation.getLocationByFused(requireContext())
            userLocation.location.observe(viewLifecycleOwner){
                if (it != null){
                    Log.d("test" , "had location it is lang is : ${it.latitude}")

                    initialPrayerTimes(it)
                }else{
                    Log.d("test" , "null location")

                }
            }

        } catch (e: java.lang.Exception) {
            Log.d("test" , "err : ${e.message}")
        }
    }

    private fun initialPrayerTimes(p0: Location) {
        val coordinates = Coordinates(p0.latitude, p0.longitude)
        val params = CalculationMethod.EGYPTIAN.parameters
        params.madhab = Madhab.SHAFI
        val date = DateComponents.from(Date())
        val prayerTimes = PrayerTimes(coordinates, date, params)
        val dateFormat =
            android.text.format.DateFormat.getTimeFormat(requireActivity().applicationContext);

        setTimesToSharedFun(prayerTimes , dateFormat)
        setTimeToCard()
    }

    private fun setTimeToCard() {
        binding.mainFajrValue.text = getPrayerTimeFromShared(Constants.FJR)
        binding.mainShorokeValue.text = getPrayerTimeFromShared(Constants.SUNRISE)

        binding.mainDohrValue.text = getPrayerTimeFromShared(Constants.DOHR)

        binding.mainAsrValue.text = getPrayerTimeFromShared(Constants.ASR)

        binding.mainMaghrebValue.text = getPrayerTimeFromShared(Constants.MAGHREB)

        binding.mainIshaValue.text = getPrayerTimeFromShared(Constants.ISHA)
    }


    private fun setTimesToSharedFun(prayerTimes: PrayerTimes, dateFormat: DateFormat){
        setPrayerTimesInShared(Constants.FJR, dateFormat.format(prayerTimes.fajr))
        setPrayerTimesInShared(Constants.SUNRISE, dateFormat.format(prayerTimes.sunrise))
        setPrayerTimesInShared(Constants.DOHR, dateFormat.format(prayerTimes.dhuhr))
        setPrayerTimesInShared(Constants.ASR, dateFormat.format(prayerTimes.asr))
        setPrayerTimesInShared(Constants.MAGHREB, dateFormat.format(prayerTimes.maghrib))
        setPrayerTimesInShared(Constants.ISHA, dateFormat.format(prayerTimes.isha))
    }
}