package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.prayertimequran.data.db.QuranDB
import com.example.prayertimequran.data.db.SavedPageDoa
import com.example.prayertimequran.data.models.quran.SavedPage
import com.example.prayertimequran.data.models.quran.Soraa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedPageViewModel(app: Application) : ParentViewModel<SavedPage>(app){
    private var savedPage :SavedPageDoa? = null
    var savedPagesList : LiveData<List<SavedPage?>>? = null


    override fun getAllData() {
        }



    override fun callDao() {
        savedPage  = QuranDB.getInstance(getApplication())?.savedPageDao()
        savedPagesList =  savedPage!!.getSavedPagesLiveData()
    }
}