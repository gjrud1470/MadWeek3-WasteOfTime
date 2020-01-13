package com.example.wasteoftime

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_set_alarm.*


class SetAlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        init_activity()
    }

    private fun init_activity () {
        val items = resources.getStringArray(R.array.my_array)

        var temp_alarmtime = appOptHolder.get_alarmtime()
        submit.setOnClickListener { view ->
            appOptHolder.set_alarmtime(temp_alarmtime)

            Intent(this, AlarmService::class.java).also { intent ->
                startForegroundService(intent)
            }
            if (temp_alarmtime != 0.toLong())
                moveTaskToBack(true)
        }

        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = myAdapter
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
        spinner.setSelection(default_pos)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        moveTaskToBack(true)
    }
}
