package com.example.prayertimequran.common

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*


class LangCheck {
    private fun setLocale(activity: Activity, lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)  // error might here
        val resources: Resources = activity.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun checkLang(activity: Activity) {
        if (Locale.getDefault().displayLanguage !== "ar") {
            val langCheck = LangCheck()
            langCheck.setLocale(activity, "ar")
        }
    }
}
