package com.example.wasteoftime

import android.app.AppOpsManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.provider.Settings
import android.util.Log

//<div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

class SplashActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        startAct()
    }

    fun startAct(){
        // This method will be executed once the timer is over
        // Start your app main activity
        Handler().postDelayed({
            if(true) {
                startActivity(Intent(this, InfoActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            // close this activity
            finish()
        }, 3000)
    }
}