package com.example.prayertimequran.data.models.alarm

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.SharedPreferencesApp
import kotlinx.coroutines.launch
import java.util.*

class PrayerTimesCal(application: Application) {
    private val formatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    private var sharedPreferencesApp: SharedPreferencesApp
    val mapOfPrayers = mutableMapOf<String, Long?>()

    init {
        sharedPreferencesApp = SharedPreferencesApp.getInstance(application)

    }

    fun convertFromStringDateToLong(date: String?): Long =
        if (date == null) {
            0
        } else {
            formatter.parse(date).time
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
        sharedPreferencesApp.writeInShared(
            Constants.FJR,
            prayerTimes.fajr.time
        )
        sharedPreferencesApp.writeInShared(
            Constants.SUNRISE,
            prayerTimes.sunrise.time
        )
        sharedPreferencesApp.writeInShared(
            Constants.DOHR,
            prayerTimes.dhuhr.time
        )
        sharedPreferencesApp.writeInShared(
            Constants.ASR,
            prayerTimes.asr.time
        )
        sharedPreferencesApp.writeInShared(
            Constants.MAGHREB,
            prayerTimes.maghrib.time
        )
        // read old and save it in old Isha
//        writeOldIshaInShared()
        sharedPreferencesApp.writeInShared(
            Constants.ISHA,
            prayerTimes.isha.time
        )
    }
//    private fun readOldIshaFromShared() : Long=
//        sharedPreferencesApp.getLongFromShared(Constants.ISHA ,0L )
//
//    private fun writeOldIshaInShared(){
//        sharedPreferencesApp.writeInShared(Constants.OLD_ISHA , readOldIshaFromShared())
//    }

    private fun setPrayerTimesToMap(prayerTimes: PrayerTimes?) {
        mapOfPrayers[Constants.FJR] = prayerTimes?.fajr?.time ?:0
        mapOfPrayers[Constants.SUNRISE] = prayerTimes?.sunrise?.time?:0
        mapOfPrayers[Constants.DOHR] = prayerTimes?.dhuhr?.time?:0
        mapOfPrayers[Constants.ASR] = prayerTimes?.asr?.time?:0
        mapOfPrayers[Constants.MAGHREB] = prayerTimes?.maghrib?.time?:0
        mapOfPrayers[Constants.ISHA] = prayerTimes?.isha?.time?:0
    }

//     fun getAllPrayerFromShared() {
//            val fajr = sharedPreferencesApp.getStringFromShared(Constants.FJR , "00:00 AM")
//            val sunrise =  sharedPreferencesApp.getStringFromShared(Constants.SUNRISE, "00:00 AM")
//            val duhr =  sharedPreferencesApp.getStringFromShared(Constants.DOHR, "00:00 AM")
//            val asr =  sharedPreferencesApp.getStringFromShared(Constants.ASR , "00:00 AM")
//            val maghreb =  sharedPreferencesApp.getStringFromShared(Constants.MAGHREB, "00:00 AM")
//            val isha =  sharedPreferencesApp.getStringFromShared(Constants.ISHA, "00:00 AM")
//            mapOfPrayers[Constants.FJR] = fajr
//            mapOfPrayers[Constants.SUNRISE] = sunrise
//            mapOfPrayers[Constants.DOHR] = duhr
//            mapOfPrayers[Constants.ASR] = asr
//            mapOfPrayers[Constants.MAGHREB] = maghreb
//            mapOfPrayers[Constants.ISHA] = isha
//            allPrayerLiveData.postValue(mapOfPrayers)
//
//
//        }
}