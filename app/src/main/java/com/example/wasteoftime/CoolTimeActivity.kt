package com.example.wasteoftime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_cool_time.*

class CoolTimeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cool_time)

        init_activity_cooltime()
    }

    private fun init_activity_cooltime() {
        submit_cooltime.setOnClickListener { view ->
            val startHomescreen = Intent(Intent.ACTION_MAIN)
            startHomescreen.addCategory(Intent.CATEGORY_HOME)
            startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(startHomescreen)
            finish()
        }
    }

    override fun onBackPressed() {
        val startHomescreen = Intent(Intent.ACTION_MAIN)
        startHomescreen.addCategory(Intent.CATEGORY_HOME)
        startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(startHomescreen)
        finish()
    }
}
