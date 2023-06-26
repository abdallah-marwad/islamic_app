package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.abdallah.prayertimequran.data.db.QuranDB
import com.abdallah.prayertimequran.data.db.SavedPageDoa
import com.abdallah.prayertimequran.data.models.quran.SavedPage

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