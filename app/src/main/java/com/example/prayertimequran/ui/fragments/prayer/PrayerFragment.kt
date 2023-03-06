package com.example.prayertimequran.ui.fragments.prayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.example.prayertimequran.R
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.UserLocation
import com.example.prayertimequran.databinding.FragmentPrayerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


class PrayerFragment : Fragment(R.layout.fragment_prayer) {
    private lateinit var binding: FragmentPrayerBinding
    private lateinit var userLocation: UserLocation
    private var presenter: PrayerPresenter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var allPrayerLiveData: MutableLiveData<Map<String, String?>>? = MutableLiveData()
    private var coroutineScope: CoroutineScope? = null
    private var dateFormat: SimpleDateFormat? = null
    private var showDateFormat: SimpleDateFormat? = null
    var timer : CountDownTimer? = null
    var remainingTime: MutableLiveData<kotlin.collections.Map<String ,Long>>? = MutableLiveData()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("test", "onCreateView")
        binding = FragmentPrayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("test", "onView Created")
        setUpPresenter()
        setAddressToView()
        setTimeToCard()
        showIslamicDate()
        takePermissionAndSetPrayerTime()
    }

    private fun showIslamicDate() {
        dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
        showDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        binding.prayerFragTextDate.text = Date().toString()


    }

    private fun timerForSecondPrayer() {

        if (presenter?.checkPermissionsIsToke()!!) {
            remainingTime?.observe(requireActivity()){
            timer = object : CountDownTimer(it.values.first(), 1000) {
                override fun onTick(p0: Long) {
                    updateTimer(p0 , it.keys.first())
                }

                override fun onFinish() {
                    Log.d("test" ,"timer finished $it")
//                    Toast.makeText(requireContext(),"Timer Finished", Toast.LENGTH_SHORT).show()
                    presenter?.checkTimeToStartTimer(requireActivity())
                }
            }.start()
        }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun updateTimer(remainingTime : Long, prayerName : String ){
        coroutineScope?.launch {
            val dateDifference=remainingTime / 1000
            var hour = (dateDifference / 3600)
            var minute = (dateDifference/60)%60
            var second = dateDifference %60
            var result = java.lang.StringBuilder()
                result.append(hour)
                result.append(":")  
                result.append(minute)
                result.append(":")
                result.append(second)
            withContext(Dispatchers.Main) {
                binding.prayerFragTextTimer.text = prayerName+" بعد : "+result
            }
        }
    }
    private fun setUpPresenter() {
        presenter = PrayerPresenter(requireContext())
        sharedPreferences = presenter?.sharedPreferences
        coroutineScope = presenter?.coroutineScope
        allPrayerLiveData = presenter?.allPrayerLiveData!!
        remainingTime = presenter?.remainingTime!!
    }

    private fun takePermissionAndSetPrayerTime() {
        if (presenter?.checkPermissionsIsToke()!!) {
            Log.d("test", "Permission is taken")
        } else {
            takePermission()
        }
    }
//    }

    private fun takePermission() {
        Log.d("test", "In take Premisiin fun   ")

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getLocation()

                }
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getLocation()
                }
                else -> {
                    presenter?.setIsLocationToke(false)
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


    private fun getLocation() {
        Log.d("test", "in getLocation")
        userLocation = UserLocation()
        try {
            userLocation.getLocationByFused(requireContext())
            userLocation.location.observe(viewLifecycleOwner) {
                if (it != null) {
                    Log.d("test", "had location it is lang is : ${it.latitude}")

                    initialPrayerTimes(it)
                    setAddress(it.latitude, it.longitude)
                    // here
                    presenter?.setIsLocationToke(true)
                } else {
                    presenter?.setIsLocationToke(false)
                    Toast.makeText(
                        requireContext(),
                        "Cannot take the location try again",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("test", "null location")

                }
            }

        } catch (e: java.lang.Exception) {
            Log.d("test", "err : ${e.message}")
        }
    }

    private fun initialPrayerTimes(p0: Location) {
        val coordinates = Coordinates(p0.latitude, p0.longitude)
        val params = CalculationMethod.EGYPTIAN.parameters
        params.madhab = Madhab.SHAFI
        val date = DateComponents.from(Date())
        val prayerTimes = PrayerTimes(coordinates, date, params)
        setTimesToSharedFun(prayerTimes, dateFormat!!)
        setTimeToCard()
    }

    private fun setAddress(MyLat: Double, MyLong: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(MyLat, MyLong, 1)
        val cityName = addresses!![0].getAddressLine(0)
//        val countryName = addresses!![0].getAddressLine(2)
        presenter?.setPrayerTimesInShared("location", cityName)
        setAddressToView()
    }

    private fun setAddressToView() {
        binding.prayerFragTextLocation.text = presenter?.getPrayerTimeFromShared("location")
    }

    private fun setTimeToCard() {
        presenter?.getAllPrayerFromShared()
        allPrayerLiveData?.observe(requireActivity()) { map ->
            binding.mainFajrValue.text = map[Constants.FJR]
            binding.mainShorokeValue.text = map[Constants.SUNRISE]
            binding.mainDohrValue.text = map[Constants.DOHR]
            binding.mainAsrValue.text = map[Constants.ASR]
            binding.mainMaghrebValue.text = map[Constants.MAGHREB]
            binding.mainIshaValue.text = map[Constants.ISHA]
        }
    }


    private fun setTimesToSharedFun(prayerTimes: PrayerTimes, dateFormat: SimpleDateFormat) {
        presenter?.setPrayerTimesInShared(
            Constants.FJR,
            dateFormat.format(prayerTimes.fajr.time)
        )
        presenter?.setPrayerTimesInShared(
            Constants.SUNRISE,
            dateFormat.format(prayerTimes.sunrise.time)
        )
        presenter?.setPrayerTimesInShared(
            Constants.DOHR,
            dateFormat.format(prayerTimes.dhuhr.time)
        )
        presenter?.setPrayerTimesInShared(
            Constants.ASR,
            dateFormat.format(prayerTimes.asr.time)
        )
        presenter?.setPrayerTimesInShared(
            Constants.MAGHREB,
            dateFormat.format(prayerTimes.maghrib.time)
        )
        presenter?.setPrayerTimesInShared(
            Constants.ISHA,
            dateFormat.format(prayerTimes.isha.time)
        )

    }

    override fun onDetach() {
        super.onDetach()
        sharedPreferences = null
        coroutineScope = null
        allPrayerLiveData = null
        remainingTime = null
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer = null

    }

    override fun onResume() {
        super.onResume()
        presenter?.checkTimeToStartTimer(requireActivity())
        timerForSecondPrayer()

    }
}