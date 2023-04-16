package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayertimequran.data.db.QuranDB
import com.example.prayertimequran.data.db.QuranJozzDao
import com.example.prayertimequran.data.models.quran.Juzz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JuzzViewModel(app : Application) : ParentViewModel<Juzz>(app) {
    var juzzDao : QuranJozzDao? = null

    override fun getAllData() {
        viewModelScope.launch(Dispatchers.IO) {
                val juzz: ArrayList<Juzz> = ArrayList()
                for (i in 1..30) {
                    juzz.add(juzzDao!!.getJozzByNumber(i))
                }
                liveDate.postValue(juzz)

        }
    }

    override fun callDao() {
        juzzDao = QuranDB.getInstance(getApplication())?.quranJozzDao()
    }

}