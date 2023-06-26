package com.abdallah.prayertimequran.common

import android.app.Activity
import androidx.appcompat.app.AlertDialog

class BuildDialog(activity: Activity, message: String, title: String,
                  PositiveMessage: String,
                  negativeMessage: String,
                  positiveBtnCallBack: (() -> Unit),
                  negativeBtnCallBack: (() -> Unit )={}) {
    init {
        showDialog(activity, message, title,PositiveMessage,negativeMessage, positiveBtnCallBack , negativeBtnCallBack)
    }

    private fun showDialog(
        activity: Activity,
        message: String,
        title: String,
        PositiveMessage: String,
        negativeMessage: String,
        positiveBtnCallBack: (() -> Unit),
        negativeBtnCallBack: (() -> Unit )={},
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(PositiveMessage) { dialog, which ->
            positiveBtnCallBack()
        }

        builder.setNegativeButton(negativeMessage) { dialog, which ->
            negativeBtnCallBack()
        }
        val dialog = builder.create()
        dialog.show()
    }
}