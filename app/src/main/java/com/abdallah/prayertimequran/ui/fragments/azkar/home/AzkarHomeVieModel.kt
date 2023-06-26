package com.abdallah.prayertimequran.ui.fragments.azkar.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abdallah.prayertimequran.data.dataProviders.azkarProvider.AzkarProvider
import com.abdallah.prayertimequran.data.models.azkar.AzkarType
import kotlinx.coroutines.*

class AzkarHomeVieModel() : ViewModel() {
    val azkarLiveData: MutableLiveData<HashSet<AzkarType>> = MutableLiveData()
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    var changeText = true

    fun getAzkarWithType(context: Context) {
        coroutineScope.launch {
            val azkarProvider = AzkarProvider()
            azkarLiveData.postValue(azkarProvider.getAzkarTypes(context))
        }
    }
}