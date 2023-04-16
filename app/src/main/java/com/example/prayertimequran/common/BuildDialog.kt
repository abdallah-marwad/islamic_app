package com.example.prayertimequran.common

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

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