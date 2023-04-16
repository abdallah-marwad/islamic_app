package com.example.prayertimequran.ui.fragments.quran.quranTfseer

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.prayertimequran.data.models.quran.Tfseer
import com.example.prayertimequran.data.dataProviders.tfseerProvider.TfseerProvider
import com.example.prayertimequran.data.db.QuranDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TfseerViewModel(app: Application) : AndroidViewModel(app) {
    private val tfseerPage = TfseerProvider()
    val tfseerLiveData = MutableLiveData<Tfseer>()
    lateinit var startWork: ((Boolean) -> Unit)
    var arryListTfseer = ArrayList<Tfseer>()
    val TfseerDao = QuranDB.getInstance(getApplication())?.tfseerDao()

    init {
        getAllTfseer()
    }

    private fun getAllTfseer() {
        viewModelScope.launch(Dispatchers.IO) {
            arryListTfseer = tfseerPage.getAllTfasser(getApplication())!!
            startWork(true)
        }
    }

    fun getTfseerByPage(soraNumber: String, ayaNumber: String) {
        startWork = {
            if (it) {
                tfseerLiveData.postValue( arryListTfseer.stream()
                    ?.filter { tfseer: Tfseer -> soraNumber == tfseer.number }
                    ?.filter { tfseer: Tfseer -> ayaNumber == tfseer.aya }
                    ?.findFirst()!!.get() )
            }
        }
    }

    fun getTfseerAyaByPage(pageNumber: Int) =
        TfseerDao!!.getPageAyatByNumber(pageNumber)


}