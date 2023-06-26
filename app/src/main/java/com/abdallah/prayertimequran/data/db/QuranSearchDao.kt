package com.abdallah.prayertimequran.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.abdallah.prayertimequran.data.models.quran.Aya
@Dao
interface QuranSearchDao {
    @Query("SELECT * FROM quran WHERE aya_text_emlaey LIKE '%' || :keyword || '%'")
     fun getAyaBySubText(keyword: String?): LiveData<List<Aya>?>?


}