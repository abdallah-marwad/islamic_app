package com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer

import android.app.Application
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class QuranPageContainerViewModel() : ViewModel() {

    lateinit var context : Application
    val job: Job = Job()
    lateinit var frag : FragmentActivity
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var sharedPreferences : SharedPreferences

    fun writeInShared(currentItem : Int){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with(sharedPreferences!!.edit()) {
            putInt("current Page", currentItem)
            commit()
        }
    }
    fun getSharedCurrentItem() : Int{
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPreferences.getInt("current Page" , 0)
    }
    fun getMyAdapter() : QuranPagesAdapter {

        return QuranPagesAdapter(frag)

    }


}