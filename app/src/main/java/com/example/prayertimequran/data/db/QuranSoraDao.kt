package com.example.prayertimequran.data.db

import androidx.room.Dao
import androidx.room.Query
import com.example.prayertimequran.data.models.quran.Soraa
@Dao
interface QuranSoraDao {
    @Query("SELECT sora as soraNumber, MIN(page) as startPage ,MAX(page) as endPage ,sora_name_ar as arabicName,sora_name_en as englishName FROM quran WHERE sora = :soraNumber")
     fun getSoraByNumber(soraNumber: Int): Soraa
}