package com.example.prayertimequran.ui.fragments.azkar.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prayertimequran.data.dataProviders.azkarProvider.AzkarProvider
import com.example.prayertimequran.data.models.azkar.AzkarType
import kotlinx.coroutines.*

class AzkarHomeVieModel() : ViewModel() {
    val azkarLiveData: MutableLiveData<HashSet<AzkarType>> = MutableLiveData()
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun getAzkarWithType(context: Context) {
        coroutineScope.launch {
            val azkarProvider = AzkarProvider()
            azkarLiveData.postValue(azkarProvider.getAzkarTypes(context))
        }
    }
}