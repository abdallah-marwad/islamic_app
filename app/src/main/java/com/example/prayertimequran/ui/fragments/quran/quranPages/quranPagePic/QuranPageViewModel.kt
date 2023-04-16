package com.example.prayertimequran.ui.fragments.quran.quranPages.quranPagePic

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.data.db.QuranDB
import com.example.prayertimequran.data.models.quran.QuranPagesManager
import com.example.prayertimequran.data.models.quran.SavedPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class QuranPageViewModel(application: Application) : AndroidViewModel(application) {
        private var quranPagesManager: QuranPagesManager = QuranPagesManager()
    private val savedPageDao = QuranDB.getInstance(getApplication())?.savedPageDao()
    val mutableState = MutableStateFlow("Nothing")
    val savedPagesList = MutableLiveData<List<SavedPage?>>()
    fun getQuranImageByPageNumber(pageNumber: Int = 0): Drawable? {
        return quranPagesManager.getQuranImageByPageNumber(
            getApplication(),
            pageNumber
        ) // make it in back ground thread
    }

    fun addPage(savedPage: SavedPage) {
        viewModelScope.launch(Dispatchers.IO){
            if (savedPageDao!!.insertNewPage(savedPage) >= 0) {
                mutableState.emit(Constants.ADDING)
            }

        }
    }

    fun deletePage(savedPage: SavedPage) {
        viewModelScope.launch(Dispatchers.IO) {
            if (savedPageDao!!.deletePage(savedPage) >= 0) {
                mutableState.emit(Constants.DELETE)
            }
        }
    }

    fun getSavedPages(){
        viewModelScope.launch(Dispatchers.IO) {
            savedPagesList.postValue(savedPageDao!!.getSavedPages())
        }
}
}





