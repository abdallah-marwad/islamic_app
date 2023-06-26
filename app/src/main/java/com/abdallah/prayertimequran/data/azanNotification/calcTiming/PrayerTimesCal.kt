package com.abdallah.prayertimequran.data.azanNotification.calcTiming

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.common.SharedPreferencesApp
import java.util.*

class PrayerTimesCal(application: Application) {
    private var sharedPreferencesApp: SharedPreferencesApp
    val mapOfPrayers = mutableMapOf<String, Long?>()

    init {
        sharedPreferencesApp = SharedPreferencesApp.getInstance(application)
    }


    private fun getCalcMethod() : CalculationMethod {
        return when (sharedPreferencesApp.getStringFromShared(
            Constants.CALC_METHOD,
            Constants.EGYPT
        )) {
            Constants.EGYPT -> CalculationMethod.EGYPTIAN
            Constants.QATAR -> CalculationMethod.QATAR
            Constants.KARACHI -> CalculationMethod.KARACHI
            Constants.KUWAIT -> CalculationMethod.KUWAIT
            Constants.OTHER -> CalculationMethod.OTHER
            Constants.NORTH_AMERICA -> CalculationMethod.NORTH_AMERICA
            Constants.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA
            Constants.DUBAI -> CalculationMethod.DUBAI
            Constants.SINGAPORE -> CalculationMethod.SINGAPORE
            Constants.MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE
            Constants.MOON_SIGHTING_COMMITTEE -> CalculationMethod.MOON_SIGHTING_COMMITTEE
            else -> CalculationMethod.EGYPTIAN
        }


    }
    fun initialPrayerTimes(latitude: Double, longitude: Double) {
        Log.d("test" , "refresh Prayers With country : ${getCalcMethod()} preayercalc.class")
        val coordinates = Coordinates(latitude, longitude)
        val params = getCalcMethod().parameters
        params.madhab = Madhab.SHAFI
        val date = DateComponents.from(Date())
        val prayerTimes = PrayerTimes(coordinates, date, params)
        setTimesToShared(prayerTimes)
        setPrayerTimesToMap(prayerTimes)

    }


    private fun setTimesToShared(
        prayerTimes: PrayerTimes
    ) {
        var summerTimingValue = 0L
        if(sharedPreferencesApp.getBooleanFromShared(Constants.IS_SUMMER_TIME , false)){
            summerTimingValue = 3600000L
        }
        Log.d("test" , "Summer timer is : ${sharedPreferencesApp.getBooleanFromShared(Constants.IS_SUMMER_TIME , false)} ")
        sharedPreferencesApp.writeInShared(
           Constants.FJR,
            prayerTimes.fajr.time+summerTimingValue
        )
        sharedPreferencesApp.writeInShared(
            Constants.SUNRISE,
            prayerTimes.sunrise.time+summerTimingValue
        )
        sharedPreferencesApp.writeInShared(
            Constants.DOHR,
            prayerTimes.dhuhr.time+summerTimingValue
        )
        sharedPreferencesApp.writeInShared(
            Constants.ASR,
            prayerTimes.asr.time+summerTimingValue
        )
        sharedPreferencesApp.writeInShared(
            Constants.MAGHREB,
            prayerTimes.maghrib.time+summerTimingValue
        )
        sharedPreferencesApp.writeInShared(
            Constants.ISHA,
            prayerTimes.isha.time+summerTimingValue
        )
    }


   private fun setPrayerTimesToMap(prayerTimes: PrayerTimes?) {

            var summerTimingValue = 0L
            if(sharedPreferencesApp.getBooleanFromShared(Constants.IS_SUMMER_TIME , false))
                summerTimingValue = 3600000L


        mapOfPrayers[Constants.FJR] = (prayerTimes?.fajr?.time ?: 0 )+ summerTimingValue
       mapOfPrayers[Constants.SUNRISE] = (prayerTimes?.sunrise?.time?:0)+summerTimingValue
        mapOfPrayers[Constants.DOHR] = (prayerTimes?.dhuhr?.time?:0)+summerTimingValue
        mapOfPrayers[Constants.ASR] = (prayerTimes?.asr?.time?:0)+summerTimingValue
        mapOfPrayers[Constants.MAGHREB] = (prayerTimes?.maghrib?.time?:0)+summerTimingValue
        mapOfPrayers[Constants.ISHA] = (prayerTimes?.isha?.time?:0)+summerTimingValue

    }


}