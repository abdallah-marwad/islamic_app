package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.abdallah.prayertimequran.data.db.QuranDB
import com.abdallah.prayertimequran.data.db.QuranSoraDao
import com.abdallah.prayertimequran.data.models.quran.Soraa
import kotlinx.coroutines.Dispatchers
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