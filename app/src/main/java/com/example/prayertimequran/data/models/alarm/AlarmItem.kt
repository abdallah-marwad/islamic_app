package com.example.prayertimequran.data.models.alarm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class AlarmItem(

    val alarm_name : String,
    val alarm_date : Long?,

)
