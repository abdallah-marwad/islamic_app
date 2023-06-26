package com.abdallah.prayertimequran.ui.fragments.prayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
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
import androidx.navigation.fragment.findNavController
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.BuildDialog
import com.abdallah.prayertimequran.common.BuildToast
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.common.SharedPreferencesApp
import com.abdallah.prayertimequran.data.azanNotification.alarms.AlarmScheduler
import com.abdallah.prayertimequran.data.azanNotification.alarms.AlarmScheduling
import com.abdallah.prayertimequran.data.azanNotification.calcTiming.PrayerTimesCal
import com.abdallah.prayertimequran.data.azanNotification.receivers.AzanNotificationReceiver
import com.abdallah.prayertimequran.data.azanNotification.receivers.BootReceiver
import com.abdallah.prayertimequran.databinding.FragmentPrayerBinding
import com.airbnb.lottie.LottieAnimationView
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.chrono.HijrahChronology
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
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
    private lateinit var prayerTimesCal: PrayerTimesCal
    var timer: CountDownTimer? = null
    var remainingTime: MutableLiveData<kotlin.collections.Map<String, Long>>? = MutableLiveData()
    private lateinit var sharedPreferencesApp: SharedPreferencesApp
    var isFjrSwitch: Boolean = false
    var isSunRiseSwitch: Boolean = false
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
//        prayerTimesCal= PrayerTimesCal(requireActivity().application)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.appbar_light_green)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPresenter()
        setUpDate()
        setFormatters()
        swipeRefreshCallback()
        checkLocationPermission()
        checkLocationAndNotificationPermissions()
//        shorokeOnClick()
        startBootCompleted()
        checkToShowDialogSummerTime()

    }

    private fun checkToShowDialogSummerTime() {
        coroutineScope!!.launch {
            if (sharedPreferencesApp.getBooleanFromShared(Constants.TIMMING_DRAW_ATTENTION, true)) {
                withContext(Dispatchers.Main) {
                    showDialogSummerTime()
                }
            }
        }

    }

    private fun showDialogSummerTime() {
        val title = "التوقيت الصيفي"
        val dialogMessage =
            "إذا كان هناك تأخير في مواقيت الصلاه فاذهب الى صفحه الاعدادات وحدد التوقيت الصيفي."
        val positiveMessage = "الذهاب "
        val negativeMessage = "تجاهل"
        BuildDialog(
            requireActivity(), dialogMessage, title, positiveMessage, negativeMessage,
            {

                findNavController().navigate(
                    PrayerFragmentDirections.actionPrayerFragmentToAlarmFragment(
                        true
                    )
                )

            }, {

            })
    }

//    private fun shorokeOnClick() {
//        binding.prayerLottieShoroke.setOnClickListener {
//            BuildToast.showToast(
//                requireContext(),
//                " لايوجد منبه إفتراضي للشروق",
//                FancyToast.WARNING
//            )
//        }
//    }

    private fun switchersOnClick(locationState: Boolean, notificationState: Boolean) {
//        Log.d("test", "fajr : " + Date(1681300680573).toString())
//        Log.d("test", "isha : " + Date(1681359000577).toString())
        lottieAnimationViewSwitch(
            binding.prayerLottieFajr,
            Constants.ENABLE_FJR,
            Constants.FJR,
            "الفجر",
            "حي على الصلاه (موعد اذكار الصباح)",
            isFjrSwitch,
            locationState,
            notificationState

        )
        lottieAnimationViewSwitch(
            binding.prayerLottieShoroke,
            Constants.ENABLE_SUNRISE,
            Constants.SUNRISE,
            "الشروق",
            "",
            isSunRiseSwitch,
            locationState,
            notificationState
        )
        lottieAnimationViewSwitch(
            binding.prayerLottieDuhr,
            Constants.ENABLE_DOHR,
            Constants.DOHR,
            "الظهر",
            "حي على الصلاه",
            isDohrSwitch,
            locationState,
            notificationState
        )
        lottieAnimationViewSwitch(
            binding.prayerLottieAsr,
            Constants.ENABLE_ASR,
            Constants.ASR,
            "العصر",
            "حي على الصلاه",
            isAsrSwitch,
            locationState,
            notificationState
        )
        lottieAnimationViewSwitch(
            binding.prayerLottieMaghredb,
            Constants.ENABLE_MAGHREB,
            Constants.MAGHREB,
            " المغرب",
            "حي على الصلاه (موعد اذكار المساء)",
            isMaghrebSwitch,
            locationState,
            notificationState
        )
        lottieAnimationViewSwitch(
            binding.prayerLottieIsha,
            Constants.ENABLE_ISHA,
            Constants.ISHA,
            "العشاء",
            "حي على الصلاه",
            isIshaSwitch,
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
                getSwitcherValuesFromShared()
                withContext(Dispatchers.Main) {
                    setSwitcherValuesToViews()
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

    private fun setSwitcherValuesToViews() {
        switchTheValuesIfEnabled(isFjrSwitch, binding.prayerLottieFajr)
        switchTheValuesIfEnabled(isSunRiseSwitch, binding.prayerLottieShoroke)
        switchTheValuesIfEnabled(isDohrSwitch, binding.prayerLottieDuhr)
        switchTheValuesIfEnabled(isAsrSwitch, binding.prayerLottieAsr)
        switchTheValuesIfEnabled(isMaghrebSwitch, binding.prayerLottieMaghredb)
        switchTheValuesIfEnabled(isIshaSwitch, binding.prayerLottieIsha)
    }

    private fun getSwitcherValuesFromShared() {
        isFjrSwitch = getBooleanFromShared(Constants.ENABLE_FJR)
        isSunRiseSwitch = getBooleanFromShared(Constants.ENABLE_SUNRISE)
        isDohrSwitch = getBooleanFromShared(Constants.ENABLE_DOHR)
        isAsrSwitch = getBooleanFromShared(Constants.ENABLE_ASR)
        isMaghrebSwitch = getBooleanFromShared(Constants.ENABLE_MAGHREB)
        isIshaSwitch = getBooleanFromShared(Constants.ENABLE_ISHA)
    }


    private fun switchTheValuesIfEnabled(isSwitched: Boolean, view: LottieAnimationView) {
        if (isSwitched) {
            lottiePositiveAnimation(view)
        }
    }

    private fun getBooleanFromShared(shardVarName: String): Boolean =
        sharedPreferencesApp.getBooleanFromShared(shardVarName, false)

    private fun lottieNegativeAnimation(view: LottieAnimationView) {
        view.setMinAndMaxProgress(0.5f, 1.0f)
        view.playAnimation()
    }

    private fun lottiePositiveAnimation(view: LottieAnimationView) {
        view.setMinAndMaxProgress(0.0f, 0.5f)
        view.playAnimation()
    }

    private fun showingToast(txt: String, state: Int) {
        BuildToast.showToast(
            requireContext(),
            txt,
            state
        )
    }

    private fun lottieAnimationViewSwitch(
        view: LottieAnimationView,
        sharedEnablingName: String,
        sharedPrayerName: String,
        prayerArabicName: String,
        notificationContent: String,
        isSwitched: Boolean,
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
                    lottieNegativeAnimation(view)
                    alarmScheduler.cancel(
                        alarmCancelPendingIntent(
                            prayerArabicName,
                            sharedPrayerName,
                            notificationContent
                        ), requireActivity().application
                    )
                    showingToast("تم إلغاء منبه $prayerArabicName ", FancyToast.SUCCESS)
                    false
                } else {
                    coroutineScope!!.launch {
                        sharedPreferencesApp.writeInShared(sharedEnablingName, true)
                    }
                    lottiePositiveAnimation(view)
                    val alarmValue = sharedPreferencesApp.getLongFromShared(sharedPrayerName, 0)
                    if (Date().time <= alarmValue) {
                        val pi = alarmPendingIntent(
                            prayerArabicName,
                            sharedPrayerName,
                            notificationContent
                        )
                        alarmScheduler.schedule(alarmValue, pi, requireActivity().application)
                    }
                    showingToast("تم ضبط منبه $prayerArabicName ", FancyToast.SUCCESS)


                    true
                }
            } else {
                showingToast(
                    " تأكد من اعطاء التطبيق الصلاحيات من خلال صفحه الاعدادات",
                    FancyToast.SUCCESS
                )


            }
        }
    }

    private fun prayerRequestCode(name: String): String {
        var prayerHashCode = ""
        if (name == Constants.FJR) {
            prayerHashCode = Constants.FJR_HASH_CODE
        } else if (name == Constants.SUNRISE) {
            prayerHashCode = Constants.SUNRISE_HASH_CODE
        }else if (name == Constants.DOHR) {
            prayerHashCode = Constants.DOHR_HASH_CODE
        } else if (name == Constants.ASR) {
            prayerHashCode = Constants.ASR_HASH_CODE
        } else if (name == Constants.MAGHREB) {
            prayerHashCode = Constants.MAGHREB_HASH_CODE
        } else if (name == Constants.ISHA) {
            prayerHashCode = Constants.ISHA_HASH_CODE
        }
        return prayerHashCode

    }

    private fun alarmPendingIntent(
        prayerNameArabic: String,
        prayerName: String,
        notificationContent: String,
    ): PendingIntent {
        val intent =
            Intent(requireActivity().application, AzanNotificationReceiver::class.java).apply {
                putExtra(Constants.PRAYER_NAME, prayerNameArabic)
                putExtra(Constants.NOTIFICATION_CONTENT, notificationContent)

            }
        val requestCode = intent.hashCode()
        Log.d("test", "request code of $prayerName ${intent.hashCode()}  PrayerFrag.class")
        coroutineScope!!.launch {
            sharedPreferencesApp.writeInShared(prayerRequestCode(prayerName), requestCode)
        }
        return PendingIntent.getBroadcast(
            requireActivity().application, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun alarmCancelPendingIntent(
        prayerNameArabic: String,
        prayerName: String,
        notificationContent: String,
    ): PendingIntent {
        val intent =
            Intent(requireActivity().application, AzanNotificationReceiver::class.java).apply {
                putExtra(Constants.PRAYER_NAME, prayerNameArabic)
                putExtra(Constants.NOTIFICATION_CONTENT, notificationContent)
            }
        val requestCode = sharedPreferencesApp.getIntFromShared(prayerRequestCode(prayerName), 0)
        Log.d("test", "request code of $prayerName  from shared ${requestCode} prayerFrag.class")
        return PendingIntent.getBroadcast(
            requireActivity().application, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun startBootCompleted() {
        coroutineScope!!.launch {
            val locationToke =
                sharedPreferencesApp.getBooleanFromShared(Constants.LOCATION_TOKE, false)
            if (locationToke) {
                val bootReceiver = BootReceiver()
                val intentFilter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
                requireActivity().registerReceiver(bootReceiver, intentFilter)
            }
        }
    }

    private fun swipeRefreshCallback() {
        binding.prayerSwip.setOnRefreshListener {
            setUpDate()
            viewModel?.getAllPrayerInLongFromShared()
            checkLocationPermission()
            checkLocationAndNotificationPermissions()
            Log.d("test", "swipe start timer")

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


//        val locale = ULocale("@calendar=islamic")
//        val islamicCalendar = Calendar.getInstance(locale)
//        islamicCalendar.timeInMillis =   1690329383000
//        val df2 = SimpleDateFormat(SimpleDateFormat.MONTH_DAY, locale)
//        Log.d("test", df2.format(islamicCalendar.time)  )
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
//            var timerNotification=TimerNotification(
//                "",
//                "",
//                requireContext(),
//                0
//            )
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
                        Log.d("test", "onFinish start timer")
                        viewModel?.getAllPrayerInLongFromShared()
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
//                timerNotification.refreshNotification(result)
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
//                if (!Geocoder.isPresent()) {
//                    withContext(Dispatchers.Main) {
//                        BuildToast.showToast(
//                            requireContext(), "not available Geocoder.class",
//                            FancyToast.ERROR
//                        )
//                    }
                val geocoder = Geocoder(requireContext(), Locale("ar", "EG"))
                val addresses = geocoder.getFromLocation(MyLat, MyLong, 1)
                if (addresses!!.isNotEmpty()) {
                    val cityName = addresses[0].locality
                    Log.d("test", "country name is : ${addresses[0].countryName}")
                    viewModel?.setPrayerTimesInShared("location", cityName)
                    withContext(Dispatchers.Main) {
                        setAddressToView()
//                            BuildToast.showToast(
//                                requireContext(), "address $cityName detected  Geocoder.class",
//                                FancyToast.SUCCESS
//                            )
                    }
                    sharedPreferencesApp.writeInShared(Constants.CHANGE_CITY, false)
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    BuildToast.showToast(
                        requireContext(),
                        " قم بتفعيل الانترنت للوصول الى اسم مدينتك",
                        FancyToast.ERROR
                    )
//                    BuildToast.showToast(
//                        requireContext(), "${e.message}",
//                        FancyToast.ERROR
//                    )
//                    BuildToast.showToast(
//                        requireContext(), e.stackTraceToString(),
//                        FancyToast.ERROR
//                    )
//
//
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

        val changeCity = sharedPreferencesApp.getBooleanFromShared(Constants.CHANGE_CITY, false)

        if (sharedPreferences!!.contains("location") && !changeCity) {
            setAddressToView()

        } else {
            val lat = viewModel?.getPrayerTimeFromShared(Constants.LAT, "-1.0")!!.toDouble()
            val long = viewModel?.getPrayerTimeFromShared(Constants.LONG, "-1.0")!!.toDouble()
            if (lat != -1.0 && long != -1.0) {
                setAddress(lat, long)

            }
        }


    }


    private fun setTimeToCardByLong() {

        allPrayerLiveDataByLong?.observe(requireActivity()) { map ->
            binding.mainFajrTxt.text = "الفجر   : ${showDateFormat!!.format(map[Constants.FJR]!!)}"
            binding.mainShorokeTxt.text =
                "الشروق   : ${showDateFormat!!.format(map[Constants.SUNRISE]!!)}"
            binding.mainDohrTxt.text = "الظهر   : ${showDateFormat!!.format(map[Constants.DOHR]!!)}"
            binding.mainAsrTxt.text = "العصر   : ${showDateFormat!!.format(map[Constants.ASR]!!)}"
            binding.mainMaghrebTxt.text =
                "المغرب   : ${showDateFormat!!.format(map[Constants.MAGHREB]!!)}"
            binding.mainIshaTxt.text =
                "العشاء   : ${showDateFormat!!.format(map[Constants.ISHA]!!)}"
        }
//        prayerTimesCal.paryerTimesLiveData.observe(requireActivity()){
//                map ->
//            binding.mainFajrTxt.text = "الفجر   : ${showDateFormat!!.format(map[Constants.FJR]!!)}"
//            binding.mainShorokeTxt.text =
//                "الشروق   : ${showDateFormat!!.format(map[Constants.SUNRISE]!!)}"
//            binding.mainDohrTxt.text = "الظهر   : ${showDateFormat!!.format(map[Constants.DOHR]!!)}"
//            binding.mainAsrTxt.text = "العصر   : ${showDateFormat!!.format(map[Constants.ASR]!!)}"
//            binding.mainMaghrebTxt.text =
//                "المغرب   : ${showDateFormat!!.format(map[Constants.MAGHREB]!!)}"
//            binding.mainIshaTxt.text =
//                "العشاء   : ${showDateFormat!!.format(map[Constants.ISHA]!!)}"
//        }
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
//        Log.d("test" , "fragment Prayer onResume")

//        Log.d("test", "onResume start timer")
        viewModel?.getAllPrayerInLongFromShared()
        viewModel?.checkTheRangeOfPrayerTimes(requireActivity())
        timerForSecondPrayer()

    }
}