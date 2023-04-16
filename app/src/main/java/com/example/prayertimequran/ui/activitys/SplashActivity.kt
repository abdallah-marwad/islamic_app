package com.example.prayertimequran.ui.activitys

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.prayertimequran.R
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.common.LangApp
import com.example.prayertimequran.common.SharedPreferencesApp
import com.example.prayertimequran.databinding.ActivitySplashBinding
import com.example.prayertimequran.ui.activitys.setupActivity.SetUpActivity
import com.example.prayertimequran.ui.activitys.setupActivity.SetupPresenter

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    lateinit var sharedInstance: SharedPreferencesApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLang()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.appbar_light_green)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        sharedInstance = SharedPreferencesApp.getInstance(application)
        Handler().postDelayed({checkIsFirstTimeToLaunch()},600)


    }

    private fun checkLang() {
       LangApp().checkLang(this)
    }
    private fun checkIsFirstTimeToLaunch(){
        if(sharedInstance.getBooleanFromShared(Constants.NOT_FIRST_TIME , false)){
            startActivity(Intent(this , MainActivity::class.java))

        }else{
            startActivity(Intent(this , SetUpActivity::class.java))
        }
    }
}