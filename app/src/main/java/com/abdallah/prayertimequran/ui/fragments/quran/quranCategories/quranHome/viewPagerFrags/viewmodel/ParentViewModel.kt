package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

abstract class  ParentViewModel<T> (app: Application) : AndroidViewModel(app) {
        var liveDate = MutableLiveData<ArrayList<T>>()
        init {
            callDao()
            getAllData()
        }

        abstract fun getAllData()
        abstract fun callDao()
}