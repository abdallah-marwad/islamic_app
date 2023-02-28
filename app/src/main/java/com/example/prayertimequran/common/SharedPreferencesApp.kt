package com.example.prayertimequran.common

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreferencesApp {
    lateinit var preferences: SharedPreferences
    fun getSharedInstance(): SharedPreferences {
        preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication())
        return preferences
    }


}