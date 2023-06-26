package com.abdallah.prayertimequran.ui.fragments.quran.searchFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.abdallah.prayertimequran.data.db.QuranDB

class SearchViewModel(app : Application) : AndroidViewModel(app) {
    private val searchDao  = QuranDB.getInstance(getApplication())?.quranSearchDao()

     fun getSearchData(keyword : String)=
        searchDao!!.getAyaBySubText(keyword)


}