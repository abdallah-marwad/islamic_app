package com.example.prayertimequran.data.db

import androidx.room.Dao
import androidx.room.Query
import com.example.prayertimequran.data.models.quran.Juzz

@Dao
interface QuranJozzDao {

    @Query("SELECT jozz as jozzNumber, MIN(page) as startPage ,MAX(page) as endPage FROM quran WHERE jozz = :jozzNumber")
     fun getJozzByNumber(jozzNumber: Int): Juzz
}