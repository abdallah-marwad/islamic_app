package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.prayertimequran.data.db.QuranDB
import com.example.prayertimequran.data.db.QuranSoraDao
import com.example.prayertimequran.data.models.quran.Juzz
import com.example.prayertimequran.data.models.quran.Soraa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SoraaViewModel(app: Application) : ParentViewModel<Soraa>(app) {
    var soraaDao: QuranSoraDao? = null


    override fun getAllData() {
        Log.d("test","getAllSoras from mvvm")
        viewModelScope.launch(Dispatchers.IO) {
            val soras: ArrayList<Soraa> = ArrayList()
            for (i in 1..114) {
                soras.add(soraaDao!!.getSoraByNumber(i))
            }
            liveDate.postValue(soras)
        }
    }

    override fun callDao() {
        soraaDao = QuranDB.getInstance(getApplication())?.quranSoraDao()
    }

}