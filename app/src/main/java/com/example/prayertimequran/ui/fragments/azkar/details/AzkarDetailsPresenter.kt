package com.example.prayertimequran.ui.fragments.azkar.details

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.prayertimequran.data.dataProviders.azkarProvider.AzkarProvider
import com.example.prayertimequran.data.models.azkar.AzkarDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AzkarDetailsPresenter(val context: Context, val zkrType :String) {
    val azkarLiveData: MutableLiveData<ArrayList<AzkarDetails>> = MutableLiveData()
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun getAzkarByType() {
        coroutineScope.launch {
            val azkarProvider = AzkarProvider()
            azkarLiveData.postValue(azkarProvider.getAzkarByType(context , zkrType))
        }
    }
}