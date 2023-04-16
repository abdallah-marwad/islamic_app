package com.example.prayertimequran.common

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch

class SharedPreferencesApp(val application: Application) {
    private var preferences: SharedPreferences? = null

    companion object{
        var preferencesApp: SharedPreferencesApp? = null
        fun getInstance(application: Application) : SharedPreferencesApp{
            if(preferencesApp == null){
                preferencesApp = SharedPreferencesApp(application)
            }
            return preferencesApp!!
        }
    }
    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(application)!!
    }

    fun <T> writeInShared(varName: String, varValue: T) =
        with(preferences!!.edit()) {
            if (varValue is Boolean) {
                putBoolean(varName, varValue)
            } else if (varValue is String) {
                putString(varName, varValue)
            }
            else if (varValue is Long) {
                putLong(varName, varValue)
            }
            commit()
        }



fun getStringFromShared(varName: String, defaultValue: String): String? =
    preferences!!.getString(varName, defaultValue)

    fun getLongFromShared(varName: String, defaultValue: Long): Long =
    preferences!!.getLong(varName, defaultValue)

fun getBooleanFromShared(varName: String, defaultValue: Boolean): Boolean =
    preferences!!.getBoolean(varName, defaultValue)


}