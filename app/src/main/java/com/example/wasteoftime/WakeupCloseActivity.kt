package com.example.wasteoftime

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wakeup_close)

        init_activity_close()
    }

    private fun init_activity_close() {
        submit_close.setOnClickListener { view ->
            val startHomescreen = Intent(Intent.ACTION_MAIN)
            startHomescreen.addCategory(Intent.CATEGORY_HOME)
            startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(startHomescreen)
        }
    }

    override fun onBackPressed() {
        val startHomescreen = Intent(Intent.ACTION_MAIN)
        startHomescreen.addCategory(Intent.CATEGORY_HOME)
        startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(startHomescreen)
    }
}
