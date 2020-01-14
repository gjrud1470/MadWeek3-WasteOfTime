package com.example.wasteoftime

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_set_alarm.*
import kotlinx.android.synthetic.main.wakeup_close.*
import kotlinx.android.synthetic.main.wakeup_extend.*
import kotlinx.android.synthetic.main.wakeup_extend.submit_wakeup

class WakeupCloseActivity : AppCompatActivity() {

    var restart_flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wakeup_close)

        init_activity_close()
    }

    private fun init_activity_close() {
        submit_close.setOnClickListener { view ->
            start_cooltime()
            open_home()
        }
    }

    override fun onBackPressed() {
        start_cooltime()
        open_home()
    }

    override fun onStop() {
        if (restart_flag)
            start_cooltime()
        super.onStop()
    }

    private fun start_cooltime() {
        Intent(this, CoolTimeService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
        }
    }

    private fun start_getforeground() {
        Intent(this, GetForegroundService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
        }
    }

    private fun open_home() {
        restart_flag = false
        val startHomescreen = Intent(Intent.ACTION_MAIN)
        startHomescreen.addCategory(Intent.CATEGORY_HOME)
        startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(startHomescreen)
    }
}
