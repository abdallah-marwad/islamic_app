package com.abdallah.prayertimequran.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.abdallah.prayertimequran.data.models.quran.SavedPage


@Dao
interface SavedPageDoa {
    @Query("SELECT * from saved_page")
    fun getSavedPagesLiveData(): LiveData<List<SavedPage?>> // err might here change to list

    @Query("SELECT * from saved_page")
    fun getSavedPages(): List<SavedPage?>


    @Insert
     fun insertNewPage(savedPage: SavedPage) : Long

    @Delete
     fun deletePage(savedPage: SavedPage) : Integer


}