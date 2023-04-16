package com.example.prayertimequran.common

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*


class LangApp {
    private fun setLocale(activity: Activity, lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun checkLang(activity: Activity) {
        if (Locale.getDefault().displayLanguage !== "ar") {
           setLocale(activity, "ar")
        }
    }
}
