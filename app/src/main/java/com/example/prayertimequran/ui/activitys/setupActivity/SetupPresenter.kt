package com.example.prayertimequran.ui.activitys.setupActivity

import android.app.Application
import com.example.prayertimequran.common.SharedPreferencesApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SetupPresenter(application: Application) {
     val sharedInstance = SharedPreferencesApp(application)
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun <T>setDataToShared(varName: String , varValue: T){
        coroutineScope.launch{
            sharedInstance.writeInShared<T>(varName , varValue)
        }
    }
    fun getStringFromShared(varName: String, defaultString: String): String? =
        sharedInstance.getStringFromShared(varName , defaultString)

    fun getBooleanFromShared(varName: String, defaultString: Boolean): Boolean? =
        sharedInstance.getBooleanFromShared(varName , defaultString)

}