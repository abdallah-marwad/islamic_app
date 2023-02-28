package com.example.prayertimequran.ui.fragments.azkarHome

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.prayertimequran.data.azkarProvider.AzkarProvider
import com.example.prayertimequran.data.models.azkar.AzkarType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AzkarHomePresenter(val context: Context) {
    val azkarLiveData: MutableLiveData<HashSet<AzkarType>> = MutableLiveData()

    fun getAzkarWithType() {
            CoroutineScope(Dispatchers.IO).launch {
                val azkarProvider = AzkarProvider()
                azkarLiveData.postValue(azkarProvider.getAzkarTypes(context))
            }
    }
}