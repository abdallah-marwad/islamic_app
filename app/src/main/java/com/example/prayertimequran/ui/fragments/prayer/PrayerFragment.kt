package com.example.prayertimequran.ui.fragments.prayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.prayertimequran.R
import com.example.prayertimequran.common.BuildToast
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.SharedPreferencesApp
import com.example.prayertimequran.data.azanNotification.AlarmScheduler
import com.example.prayertimequran.data.azanNotification.AlarmScheduling
import com.example.prayertimequran.data.azanNotification.AlarmService
import com.example.prayertimequran.data.azanNotification.AzanNotificationReceiver
import com.example.prayertimequran.databinding.FragmentPrayerBinding
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.chrono.HijrahChronology
import java.time.format.DateTimeFormatter
import java.util.*

class PrayerFragment : Fragment(R.layout.fragment_prayer) {
    private lateinit var binding: FragmentPrayerBinding
    private var viewModel: PrayerViewModel? = null
    private var sharedPreferences: SharedPreferences? = null
    private var allPrayerLiveData: MutableLiveData<Map<String, String?>>? = MutableLiveData()
    private var allPrayerLiveDataByLong: MutableLiveData<Map<String, Long>>? = MutableLiveData()
    private var coroutineScope: CoroutineScope? = null
    private var dateFormat: SimpleDateFormat? = null
    private var showDateFormat: SimpleDateFormat? = null
    var timer: CountDownTimer? = null
    var remainingTime: MutableLiveData<kotlin.collections.Map<String, Long>>? = MutableLiveData()
    private lateinit var sharedPreferencesApp: SharedPreferencesApp
    var isFjrSwitch: Boolean = false
    var isDohrSwitch: Boolean = false
    var isAsrSwitch: Boolean = false
    var isMaghrebSwitch: Boolean = false
    var isIshaSwitch: Boolean = false
    private val alarmScheduler: AlarmScheduler = AlarmScheduling()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPrayerBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PrayerViewModel::class.java]
        sharedPreferencesApp = SharedPreferencesApp.getInstance(requireActivity().application)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.appbar_light_green)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPresenter()
//        requireActivity().startService(Intent(requireContext(), AlarmService::class.java))
        setUpDate()
        setFormatters()
        swipeRefreshCallback()
        checkLocationPermission()
        checkLocationAndNotificationPermissions()
        shorokeOnClick()

    }

    private fun shorokeOnClick() {
        binding.prayerLottieShoroke.setOnClickListener {
            BuildToast.showToast(
                requireContext(),
                " لايوجد منبه إفتراضي للشروق",
                FancyToast.WARNING
            )
        }
    }

    private fun switchersOnClick(locationState: Boolean, notificationState: Boolean) {
//        Log.d("test", "fajr : " + Date(1681300680573).toString())
//        Log.d("test", "isha : " + Date(1681359000577).toString())
        lottieAnimationViewSwitch(
            binding.prayerLottieFajr,
            Constants.ENABLE_FJR,
            Constants.FJR,
            "الفجر",
            isFjrSwitch,
            Constants.FJR_REQUEST_CODE,
            locationState,
            notificationState

        )
        lottieAnimationViewSwitch(
            binding.prayerLottieDuhr,
            Constants.ENABLE_DOHR,
            Constants.DOHR,
            "الظهر",
            isDohrSwitch,
            Constants.DOHR_REQUEST_CODE,
            locationState,
            notificationState
        )
        lottieAnimationViewSwitch(
            binding.prayerLottieAsr,
            Constants.ENABLE_ASR,
            Constants.ASR,
            "العصر",
            isAsrSwitch,
            Constants.ASR_REQUEST_CODE,
            locationState,
            notificationState
        )
        lottieAnimationViewSwitch(
            binding.prayerLottieMaghredb,
            Constants.ENABLE_MAGHREB,
            Constants.MAGHREB,
            "المغرب",
            isMaghrebSwitch,
            Constants.MAGHREB_REQUEST_CODE,
            locationState,
            notificationState
        )
        lottieAnimationViewSwitch(
            binding.prayerLottieIsha,
            Constants.ENABLE_ISHA,
            Constants.ISHA,
            "العشاء",
            isIshaSwitch,
            Constants.ISHA_REQUEST_CODE,
            locationState,
            notificationState
        )

    }

    private fun checkLocationAndNotificationPermissions() {
        coroutineScope!!.launch {
            val notificationPermissionTaken =
                sharedPreferencesApp.getBooleanFromShared(Constants.NOTIFICATION_STATE, false)
            val locationPermissionTaken =
                sharedPreferencesApp.getBooleanFromShared(Constants.LOCATION_TOKE, false)
            if (notificationPermissionTaken && locationPermissionTaken) {
                setSwitcherValues()
                withContext(Dispatchers.Main) {
                    switchAllIfEnabled()
                }


            }
            withContext(Dispatchers.Main) {
                switchersOnClick(
                    locationPermissionTaken,
                    notificationPermissionTaken

                )
            }

        }
    }

    private fun switchAllIfEnabled() {
        switchTheValuesIfEnabled(isFjrSwitch, binding.prayerLottieFajr)
        switchTheValuesIfEnabled(isDohrSwitch, binding.prayerLottieDuhr)
        switchTheValuesIfEnabled(isAsrSwitch, binding.prayerLottieAsr)
        switchTheValuesIfEnabled(isMaghrebSwitch, binding.prayerLottieMaghredb)
        switchTheValuesIfEnabled(isIshaSwitch, binding.prayerLottieIsha)
    }

    private fun setSwitcherValues() {
        isFjrSwitch = getBooleanFromShared(Constants.ENABLE_FJR)
        isDohrSwitch = getBooleanFromShared(Constants.ENABLE_DOHR)
        isAsrSwitch = getBooleanFromShared(Constants.ENABLE_ASR)
        isMaghrebSwitch = getBooleanFromShared(Constants.ENABLE_MAGHREB)
        isIshaSwitch = getBooleanFromShared(Constants.ENABLE_ISHA)
    }



    private fun switchTheValuesIfEnabled(isSwitched: Boolean, view: LottieAnimationView) {
        if (isSwitched) {
            view.setMinAndMaxProgress(0.0f, 0.5f)
            view.playAnimation()
        }
    }

    private fun getBooleanFromShared(shardVarName: String): Boolean =
        sharedPreferencesApp.getBooleanFromShared(shardVarName, false)

    private fun lottieAnimationViewSwitch(
        view: LottieAnimationView,
        sharedEnablingName: String,
        sharedPrayerName: String,
        prayerArabicName: String,
        isSwitched: Boolean,
        requestCode: Int,
        locationState: Boolean,
        notificationState: Boolean
    ) {

        var switcher = isSwitched
        view.speed = 3f
        view.setOnClickListener {
            if (locationState && notificationState) {

                switcher = if (switcher) {
                    coroutineScope!!.launch {
                        sharedPreferencesApp.writeInShared(sharedEnablingName, false)
                    }
                    view.setMinAndMaxProgress(0.5f, 1.0f)
                    view.playAnimation()
                    val pi = alarmPendingIntent(prayerArabicName, requestCode)
                    alarmScheduler.cancel(pi, requireActivity().application)
                    BuildToast.showToast(
                        requireContext(),
                        "تم إلغاء منبه صلاه $prayerArabicName ",
                        FancyToast.SUCCESS
                    )
                    false
                } else {
                    coroutineScope!!.launch {
                        sharedPreferencesApp.writeInShared(sharedEnablingName, true)
                    }
                    view.setMinAndMaxProgress(0.0f, 0.5f)
                    view.playAnimation()
                    val alarmValue = sharedPreferencesApp.getLongFromShared(sharedPrayerName, 0)
                    if (Date().time < alarmValue) {
                        val pi = alarmPendingIntent(prayerArabicName, requestCode)
                        alarmScheduler.schedule(alarmValue, pi, requireActivity().application)
                    }
                    BuildToast.showToast(
                        requireContext(),
                        "تم ضبط منبه لصلاه $prayerArabicName ",
                        FancyToast.SUCCESS
                    )


                    true
                }
            } else {
                BuildToast.showToast(
                    requireContext(),
                    " تأكد من اعطاء التطبيق الصلاحيات من خلال صفحه الاعدادات",
                    FancyToast.WARNING
                )
            }
        }
    }


    private fun alarmPendingIntent(prayerNameArabic: String, requestCode: Int): PendingIntent {
        val intent =
            Intent(requireActivity().application, AzanNotificationReceiver::class.java).apply {
                putExtra(Constants.PRAYER_NAME, prayerNameArabic)
            }

        return PendingIntent.getBroadcast(
            requireActivity().application, requestCode, intent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    private fun swipeRefreshCallback() {
        binding.prayerSwip.setOnRefreshListener {
            setUpDate()
            viewModel?.getAllPrayerInLongFromShared()
            checkLocationPermission()
            checkLocationAndNotificationPermissions()
            viewModel?.checkTheRangeOfPrayerTimes(requireActivity())
            timerForSecondPrayer()
            binding.prayerSwip.isRefreshing = false
        }
    }

    private fun setUpDate() {

        setNormalDate()
        setHijriDate()


    }

    private fun setFormatters() {
        dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
        showDateFormat = SimpleDateFormat("hh:mm a", Locale("ar", "EG"))
    }

    private fun setNormalDate() {
        val calendar = Calendar.getInstance(Locale("ar", "EG"))
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
        val formattedDate = dateFormat.format(calendar.time)
        binding.prayerFragTextDate.text = formattedDate
    }

    private fun setHijriDate() {
        val hijriDate = HijrahChronology.INSTANCE.date(LocalDate.now())

        val formatter = DateTimeFormatter.ofPattern("EEEE، dd MMMM yyyy", Locale("ar", "EG", "ar"))
            .withChronology(HijrahChronology.INSTANCE)

        val formattedHijriDate = hijriDate.format(formatter)
        binding.prayerFragTextDateHejri.text = formattedHijriDate

    }

    private fun timerForSecondPrayer() {
        remainingTime?.observe(requireActivity()) {
            if (it.values.first() != 1L) {
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                    binding.prayerFragTextTimer.text = "00:00:00"
                    binding.prayerFragTextPrayerName.text = "الصلاه القادمه"
                }

                timer = object : CountDownTimer(it.values.first(), 1000) {
                    override fun onTick(p0: Long) {
                        updateTimer(p0, it.keys.first())
                    }

                    override fun onFinish() {
                        viewModel?.checkTheRangeOfPrayerTimes(requireActivity())
                    }
                }.start()
            }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateTimer(remainingTime: Long, prayerName: String) {
        coroutineScope?.launch {
            val dateDifference = remainingTime / 1000
            var hour = (dateDifference / 3600)
            var minute = (dateDifference / 60) % 60
            var second = dateDifference % 60
            var result = java.lang.StringBuilder()
            result.append(String.format("%02d", hour))
            result.append(":")
            result.append(String.format("%02d", minute))
            result.append(":")
            result.append(String.format("%02d", second))
            withContext(Dispatchers.Main) {
                binding.prayerFragTextTimer.text = result
                binding.prayerFragTextPrayerName.text = "الصلاه القادمه " + prayerName
            }
        }
    }

    private fun setUpPresenter() {

        viewModel!!.checkTimeToStartTimerByLongValues(requireActivity())
        sharedPreferences = viewModel?.sharedPreferences
        coroutineScope = viewModel?.coroutineScope
        allPrayerLiveData = viewModel?.allPrayerLiveData!!
        allPrayerLiveDataByLong = viewModel?.allPrayerLiveDataByLong!!
        remainingTime = viewModel?.remainingTime!!
    }

    private fun checkLocationPermission() {
        if (viewModel?.checkPermissionsIsToke()!!) {
//            Log.d("test", "Location permission true prayer")

            checkLocationDataGetAddress()
            setTimeToCardByLong()
        }
    }


    private fun setAddress(MyLat: Double, MyLong: Double) {
        coroutineScope!!.launch {
            try {
                val geocoder = Geocoder(requireContext(), Locale("ar", "EG"))
                val addresses = geocoder.getFromLocation(MyLat, MyLong, 1)
                if (addresses!!.isNotEmpty()) {
                    val cityName = addresses!![0].locality
                    Log.d("test", "country name is : ${addresses[0].countryName}")
                    viewModel?.setPrayerTimesInShared("location", cityName)
                    withContext(Dispatchers.Main) {
                        setAddressToView()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    BuildToast.showToast(
                        requireContext(),
                        " قم بتفعيل الانترنت للوصول الى اسم مدينتك",
                        FancyToast.ERROR
                    )


                }

//            Log.d("test", "Error getting city :  $e.message")
            }
        }
    }

    private fun setAddressToView() {
        binding.prayerFragTextLocation.text =
            viewModel?.getPrayerTimeFromShared("location", Constants.LOCATION_NOT_FOUNDED)
    }

    private fun checkLocationDataGetAddress() {
        val lat = viewModel?.getPrayerTimeFromShared(Constants.LAT, "-1.0")!!.toDouble()
        val long = viewModel?.getPrayerTimeFromShared(Constants.LONG, "-1.0")!!.toDouble()

        if (sharedPreferences!!.contains("location")) {
            setAddressToView()
        } else {
            if (lat != -1.0 && long != -1.0) {
                setAddress(lat, long)
            }
        }


    }



    private fun setTimeToCardByLong() {

        allPrayerLiveDataByLong?.observe(requireActivity()) { map ->
//         Log.d("test" , "setTimeToCardByLong")
            binding.mainFajrTxt.text ="الفجر   : ${ showDateFormat!!.format(map[Constants.FJR]!!)}"
            binding.mainShorokeTxt.text = "الشروق   : ${ showDateFormat!!.format(map[Constants.SUNRISE]!!)}"
            binding.mainDohrTxt.text = "الظهر   : ${ showDateFormat!!.format(map[Constants.DOHR]!!)}"
            binding.mainAsrTxt.text = "العصر   : ${ showDateFormat!!.format(map[Constants.ASR]!!)}"
            binding.mainMaghrebTxt.text = "المغرب   : ${ showDateFormat!!.format(map[Constants.MAGHREB]!!)}"
            binding.mainIshaTxt.text = "العشاء   : ${ showDateFormat!!.format(map[Constants.ISHA]!!)}"
        }
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
//        Log.d("test" , "fragment Prayer onPause")

        timer?.cancel()
        timer = null

    }

    override fun onResume() {
        super.onResume()
//        Log.d("test" , "fragment Prayer onResume")


        viewModel?.checkTheRangeOfPrayerTimes(requireActivity())
        timerForSecondPrayer()

    }
}