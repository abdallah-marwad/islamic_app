package com.abdallah.prayertimequran.ui.fragments.quran.quranTfseer

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdallah.prayertimequran.data.models.quran.Tfseer
import com.abdallah.prayertimequran.data.dataProviders.tfseerProvider.TfseerProvider
import com.abdallah.prayertimequran.data.db.QuranDB
import com.abdallah.prayertimequran.data.db.TfseerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.filter

class TfseerViewModel(app: Application) : AndroidViewModel(app) {
    private val tfseerPage = TfseerProvider()
    val tfseerLiveData = MutableLiveData<Tfseer>()
    val tfseerStateFlow = MutableStateFlow<Tfseer>(Tfseer())
    var tfseerCallback : ((Tfseer) -> Unit) ={}
    var startWork: ((Boolean) -> Unit) ={}
    var arryListTfseer = ArrayList<Tfseer>()
    var startWorkToGetTfseer = false
    val TfseerDao = QuranDB.getInstance(getApplication())?.tfseerDao()

    init {
        getAllTfseer()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getAllTfseer() {
        viewModelScope.launch(Dispatchers.IO) {
            arryListTfseer = tfseerPage.getAllTfasser(getApplication())!!
                startWork (true)



        }
    }

     fun getTfseerByPage(soraNumber: String, ayaNumber: String) {
             tfseerCallback(arryListTfseer
                 ?.filter { tfseer: Tfseer -> soraNumber == tfseer.number }
                 ?.filter { tfseer: Tfseer -> ayaNumber == tfseer.aya }
             !![0])
    }

    fun getTfseerAyaByPage(pageNumber: Int) =
        TfseerDao!!.getPageAyatByNumber(pageNumber)


}