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

class WakeupExtendActivity : AppCompatActivity() {

    var restart_flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wakeup_extend)

        init_activity_extend()
    }

    private fun init_activity_extend () {
        val items = resources.getStringArray(R.array.my_array)

        var temp_alarmtime = appOptHolder.get_alarmtime()
        submit_wakeup.setOnClickListener { view ->
            appOptHolder.set_alarmtime(temp_alarmtime)

            restart_flag = false
            Intent(this, ExtendAlarmService::class.java).also { intent ->
                startForegroundService(intent)
            }
            if (temp_alarmtime != 0.toLong())
                moveTaskToBack(true)
        }

        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner_wakeup.adapter = myAdapter
        if (appOptHolder.get_alarmtime() > 6 * resources.getInteger(R.integer.min_unit))
            appOptHolder.set_alarmtime(resources.getInteger(R.integer.default_alarm_time).toLong()
                    * resources.getInteger(R.integer.min_unit))

        val default_pos = when (appOptHolder.get_alarmtime()) {
            0.toLong() -> 0
            (-1).toLong() -> 7
            else -> {
                (appOptHolder.get_alarmtime()/resources.getInteger(R.integer.min_unit)).toInt()
            }
        }
        spinner_wakeup.setSelection(default_pos)

        spinner_wakeup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> temp_alarmtime = 0                 // finish app now
                    7 -> temp_alarmtime = -1     // Play indefinitely
                    else -> {
                        temp_alarmtime = position * resources.getInteger(R.integer.min_unit).toLong()  // position * 10 min
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
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

    private fun start_getforeground() {
        Intent(this, GetForegroundService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
        }
    }

    private fun start_cooltime() {
        Intent(this, CoolTimeService::class.java).also { intent ->
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
