package com.example.prayertimequran.ui.fragments.azkarHome.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.prayertimequran.data.azkarProvider.AzkarProvider
import com.example.prayertimequran.data.models.azkar.AzkarType
import kotlinx.coroutines.*

class AzkarHomePresenter(val context: Context) {
    val azkarLiveData: MutableLiveData<HashSet<AzkarType>> = MutableLiveData()
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun getAzkarWithType() {
        coroutineScope.launch {
            val azkarProvider = AzkarProvider()
            azkarLiveData.postValue(azkarProvider.getAzkarTypes(context))
        }
    }
}