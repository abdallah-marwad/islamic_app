package com.abdallah.prayertimequran.common

import android.content.Context
import com.shashank.sony.fancytoastlib.FancyToast

class BuildToast {
    companion object{
        fun showToast(context : Context, msg : String, type: Int){
            FancyToast.makeText(
                context, msg, FancyToast.LENGTH_LONG,
                type, false
            ).show()
        }
    }
}