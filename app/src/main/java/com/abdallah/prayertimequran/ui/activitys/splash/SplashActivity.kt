package com.abdallah.prayertimequran.ui.activitys.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.common.LangApp
import com.abdallah.prayertimequran.common.SharedPreferencesApp
import com.abdallah.prayertimequran.databinding.ActivitySplashBinding
import com.abdallah.prayertimequran.ui.activitys.MainActivity
import com.abdallah.prayertimequran.ui.activitys.setupActivity.SetUpActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedInstance: SharedPreferencesApp
    private val viewModel: ViewModelSplash by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashSetUp()
        checkLang()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appBarAndMood()
        sharedInstance = SharedPreferencesApp.getInstance(application)
            checkIsFirstTimeToLaunch()
           }
    private fun checkLang() {
       LangApp().checkLang(this)
    }
    private fun appBarAndMood() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.appbar_light_green)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
    private fun splashSetUp() {
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                viewModel.isLoading.value
            }
        }
    }
    private fun checkIsFirstTimeToLaunch(){
        if(sharedInstance.getBooleanFromShared(Constants.NOT_FIRST_TIME , false)){
            startActivity(Intent(this , MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this , SetUpActivity::class.java))
            finish()
        }
    }
}