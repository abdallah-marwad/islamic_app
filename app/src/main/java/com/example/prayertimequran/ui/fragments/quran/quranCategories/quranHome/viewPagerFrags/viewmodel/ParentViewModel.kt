package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.prayertimequran.data.db.QuranDB
import com.example.prayertimequran.data.db.QuranSoraDao
import com.example.prayertimequran.data.models.quran.Juzz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class  ParentViewModel<T> (app: Application) : AndroidViewModel(app) {
        var liveDate = MutableLiveData<ArrayList<T>>()
        init {
            callDao()
            getAllData()
        }

        abstract fun getAllData()
        abstract fun callDao()
}