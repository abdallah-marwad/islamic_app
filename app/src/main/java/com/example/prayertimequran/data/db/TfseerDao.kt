package com.example.prayertimequran.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.prayertimequran.data.models.quran.Aya
@Dao
interface TfseerDao {

    @Query("SELECT * FROM quran WHERE page = :pageNumber")
    fun getPageAyatByNumber(pageNumber: Int): LiveData<List<Aya>>
}
