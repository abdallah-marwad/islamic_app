package com.example.prayertimequran.ui.fragments.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.prayertimequran.R
import com.example.prayertimequran.common.*
import com.example.prayertimequran.common.takePermission.LocationPermission
import com.example.prayertimequran.common.takePermission.NotificationPermission
import com.example.prayertimequran.data.azanNotification.AlarmService
import com.example.prayertimequran.databinding.FragmentSettingBinding
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.*
import java.util.*


class SettingFragment : Fragment() {
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var binding: FragmentSettingBinding
    private lateinit var locationPermission: LocationPermission
    private lateinit var sharedInstance: SharedPreferencesApp
    private var coroutineScope: CoroutineScope? = null
    private val job = Job()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.appbar_light_green)
        coroutineScope = CoroutineScope(Dispatchers.IO+job)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedInstance = SharedPreferencesApp.getInstance(requireActivity().application)
        changeAppbarName()
        checkVersion()
        showNotificationPermission()
        showLocationPermission()
        aboutUsOnClick()
        quranFinishOnClick()
        refreshOnClick()
        backBtnOnclick()
    }

    private fun aboutUsOnClick() {
        binding.aboutUs.setOnClickListener {
            val title = "معلومات التواصل"
            val dialogMessage =
                "للتواصل مع المطور عبر الجيميل : abdallahshehata311as@gmail.com  او عن طريق الرقم : +201551123149"
            val positiveMessage = "الرقم "
            val negativeMessage = "الإيميل"
            BuildDialog(
                requireActivity(), dialogMessage, title, positiveMessage, negativeMessage,
                positiveOnClick(), negativeOnClick()
            )
        }


    }

    private fun quranFinishOnClick() {
        binding.quranFinish.setOnClickListener {
            val title = "دعاء الختم "
            val dialogMessage =
                requireContext().applicationContext.getString(R.string.finish_quran)
            val positiveMessage = "تم"
            val negativeMessage = ""
            BuildDialog(
                requireActivity(), dialogMessage, title, positiveMessage, negativeMessage,
                {}
            )
        }


    }

    private fun refreshOnClick() {
        binding.refresh.setOnClickListener {
            val title = requireContext().applicationContext.getString(R.string.manual_refresh)
            val dialogMessage =
                requireContext().applicationContext.getString(R.string.refresh_dialog_msg)
            val positiveMessage =
                requireContext().applicationContext.getString(R.string.refresh_txt)
            val negativeMessage =
                requireContext().applicationContext.getString(R.string.dont_refresh)
            BuildDialog(
                requireActivity(), dialogMessage, title, positiveMessage, negativeMessage,
                {
                    checkPrayerValuesToRefresh()
                }, {}
            )
        }


    }
    private fun backBtnOnclick(){
        binding.appbar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun dateOfDayByLong(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[java.util.Calendar.HOUR_OF_DAY] = 0
        calendar[java.util.Calendar.MINUTE] = 0
        calendar[java.util.Calendar.SECOND] = 0
        calendar[java.util.Calendar.MILLISECOND] = 0
        Log.d("test", "the value of this day ${Date(calendar.timeInMillis)}")

        return Date(calendar.timeInMillis).time
    }

    private fun dateOfDayForIshaFromSharedByLong(): Long {
        val calendarIsha = java.util.Calendar.getInstance()
        val ishaDayValue = sharedInstance.getLongFromShared(Constants.ISHA, 0)
        if (ishaDayValue != 0L) {
            calendarIsha.timeInMillis = ishaDayValue
            calendarIsha[java.util.Calendar.HOUR_OF_DAY] = 0
            calendarIsha[java.util.Calendar.MINUTE] = 0
            calendarIsha[java.util.Calendar.SECOND] = 0
            calendarIsha[java.util.Calendar.MILLISECOND] = 0
            Log.d("test", "the value of isha day ${Date(calendarIsha.timeInMillis)}")
            return Date(calendarIsha.timeInMillis).time
        }
        return 999999999999999L
    }

    private fun checkPrayerValuesToRefresh() {
        coroutineScope!!.launch {
            if (sharedInstance.getBooleanFromShared(Constants.LOCATION_TOKE, false)) {
                if (dateOfDayByLong() > dateOfDayForIshaFromSharedByLong()) {
                    Log.d("test", "prayers in shared is smaller  this day")
                    requireContext().startService(
                        Intent(
                            requireActivity(), AlarmService::class.java
                        )
                    )
                    withContext(Dispatchers.Main){
                    BuildToast.showToast(
                        requireContext(),
                        " تم تحديث مواقيت الصلاه",
                        FancyToast.SUCCESS
                    )
                    }

                } else if (dateOfDayByLong() == dateOfDayForIshaFromSharedByLong()) {
                    withContext(Dispatchers.Main){
                    BuildToast.showToast(
                        requireContext(),
                        " لا يوجد مشكله في مواقيت الصلاه للتحديث",
                        FancyToast.WARNING
                    )
                }
                }
            }

        }
    }

    private fun positiveOnClick(): (() -> Unit) {
        return {
            val intent = Intent()
            intent.action = Intent.ACTION_DIAL
            val number = "+201551123149"
            intent.data = Uri.parse("tel:$number")
            requireActivity().startActivity(intent)
        }
    }

    private fun negativeOnClick(): (() -> Unit) {
        return {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("abdallahshehata311as@gmail.com"))
            requireActivity().startActivity(intent)

        }
    }

    private fun checkVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            binding.takeNotificationPermission.visibility = View.GONE
        }
    }

    private fun changeAppbarName() {
        binding.appbar.tfseerFragAppbar.text = "الإعدادات"
//        binding.appbar.back.visibility = View.GONE
    }

    private fun showNotificationPermission() {
        binding.takeNotificationPermission.setOnClickListener {
            takeNotificationPermission()
        }
    }

    private fun showLocationPermission() {
        binding.changeLocation.setOnClickListener {
            takeLocationPermission()
        }
    }

    private fun takeNotificationPermission() {
        val notificationPermission = NotificationPermission()
        notificationPermission.takeNotificationPermission(requireActivity())

    }

    private fun takeLocationPermission() {
        locationPermission = LocationPermission()
        coroutineScope = locationPermission.coroutineScope
        locationPermission.takeLocationPermission(requireActivity())
    }

    override fun onDetach() {
        super.onDetach()
        coroutineScope = null
    }


}