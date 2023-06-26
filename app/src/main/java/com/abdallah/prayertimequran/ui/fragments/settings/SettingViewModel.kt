package com.abdallah.prayertimequran.ui.fragments.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.abdallah.prayertimequran.common.SharedPreferencesApp

class SettingViewModel(application : Application) :AndroidViewModel(application)  {

    val sharedInstance =SharedPreferencesApp.getInstance(getApplication())


}