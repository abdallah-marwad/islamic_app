package com.example.prayertimequran.ui.fragments.prayer

import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.data.models.azkar.AzkarType
import kotlinx.coroutines.*
import net.time4j.calendar.PersianCalendar.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PrayerPresenter(val context: Context) {
    private val formatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    private val secondsFormatter = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    val allPrayerLiveData: MutableLiveData<Map<String, String?>> = MutableLiveData()
    val remainingTime: MutableLiveData<Map<String, Long>> = MutableLiveData()
    private val mapOfPrayers = mutableMapOf<String, String?>()


    fun setIsLocationToke(toke: Boolean) {
        coroutineScope.launch {
            with(sharedPreferences.edit()) {
                putBoolean(Constants.LOCATION_TOKE, toke)
                Log.d("test", "Done Change Shared Value To ${toke}")
                commit() // may error here
            }
        }
    }

    fun setPrayerTimesInShared(prayerName: String, prayerTime: String) =
        coroutineScope.launch {
            with(sharedPreferences.edit()) {
                putString(prayerName, prayerTime)
                Log.d("test", "the value of  $prayerName is : $prayerTime  ")
                commit() // may error here
            }

        }

    fun checkPermissionsIsToke(): Boolean =
        sharedPreferences.getBoolean(Constants.LOCATION_TOKE, false)
//        coroutineScope.launch {


    //        }
    fun checkTimeToStartTimer(activity: FragmentActivity) {
        allPrayerLiveData.observe(activity) { map ->
            val fjr = convertFromStringDateToLong(map[Constants.FJR])
            val sunrise = convertFromStringDateToLong(map[Constants.SUNRISE])
            val douhr = convertFromStringDateToLong(map[Constants.DOHR])
            val asr = convertFromStringDateToLong(map[Constants.ASR])
            val maghreb = convertFromStringDateToLong(map[Constants.MAGHREB])
            val isha = convertFromStringDateToLong(map[Constants.ISHA])
            Log.d("test" , "fjr : $fjr / sun : $sunrise/ douhr :$douhr  /" +
                    " asr: $asr /maghreb : $maghreb/ isha: $isha / ")
            checkTheRangeOfPrayerTimes(arrayListOf(fjr, sunrise, douhr, asr, maghreb, isha))

        }

    }

    private fun convertFromStringDateToLong(date: String?): Long =
        if (date == null) {
            0
        } else {
            formatter.parse(date).time
        }

    private fun checkTheRangeOfPrayerTimes(prayerTimes: ArrayList<Long>) {
        val date1 = secondsFormatter.format(Date())
        val date = secondsFormatter.parse(date1).time
//        Toast.makeText(context,"$date", Toast.LENGTH_SHORT).show()

        var dateDifference = 0L
        var PrayerName = ""
        if (prayerTimes[0] < date && date < prayerTimes[1]) {
            Log.d("test", "in 1 Range")
            dateDifference = prayerTimes[1] - date
            PrayerName = "الشروق"
        } else if (prayerTimes[1] < date && date < prayerTimes[2]) {
            Log.d("test", "in 2 Range")
            dateDifference = prayerTimes[2] - date
            PrayerName = "الظهر"
        } else if (prayerTimes[2] < date && date < prayerTimes[3]) {
            Log.d("test", "in 3 Range")
            dateDifference = prayerTimes[3] - date
            PrayerName = "العصر"
        } else if (prayerTimes[3] < date && date < prayerTimes[4]) {
            Log.d("test", "in 4 Range")
            dateDifference = prayerTimes[4]-date
            PrayerName = "المغرب"
        } else if (prayerTimes[4] < date && date < prayerTimes[5]) {
            Log.d("test", "in 5 Range")
            dateDifference = prayerTimes[5] - date
            PrayerName = "العشاء"
        } else if (prayerTimes[5] < date && date < prayerTimes[0]) {
            Log.d("test", "in 6 Range")
            dateDifference = prayerTimes[0] - date
            PrayerName = "الفجر"
        }

        if(dateDifference < 0){
            dateDifference *= -1

        }
//        Toast.makeText(context,"start new timer", Toast.LENGTH_SHORT).show()


        remainingTime.value = mapOf(PrayerName to dateDifference)
    }

    fun getAllPrayerFromShared() =
        coroutineScope.launch {
            val fajr = getPrayerTimeFromShared(Constants.FJR)
            val sunrise = getPrayerTimeFromShared(Constants.SUNRISE)
            val duhr = getPrayerTimeFromShared(Constants.DOHR)
            val asr = getPrayerTimeFromShared(Constants.ASR)
            val maghreb = getPrayerTimeFromShared(Constants.MAGHREB)
            val isha = getPrayerTimeFromShared(Constants.ISHA)
            mapOfPrayers[Constants.FJR] = fajr
            mapOfPrayers[Constants.SUNRISE] = sunrise
            mapOfPrayers[Constants.DOHR] = duhr
            mapOfPrayers[Constants.ASR] = asr
            mapOfPrayers[Constants.MAGHREB] = maghreb
            mapOfPrayers[Constants.ISHA] = isha
            allPrayerLiveData.postValue(mapOfPrayers)


        }


//    private fun convertFromLongDateToString() : String{
//
//    }


    fun getPrayerTimeFromShared(prayerName: String): String? =
        sharedPreferences.getString(prayerName, "00:00 AM")

}


