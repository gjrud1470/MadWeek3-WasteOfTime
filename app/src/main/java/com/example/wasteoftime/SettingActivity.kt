package com.example.wasteoftime

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.edittext.view.*
import kotlinx.android.synthetic.main.setting.*
import org.json.JSONArray


class SettingActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        val pref : SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()

        monitoring_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            appOptHolder.set_monitoring_flag(isChecked)
            editor.putBoolean("monitoring_flag", isChecked)
            editor.apply()

            if (isChecked) {
                Intent(this, GetForegroundService::class.java).also { intent ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    }
                }
            }
        }

        apps_to_monitor.setOnClickListener {
            startActivity(Intent(this, AppSelectActivity::class.java))
        }

        alarm_type.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("알람 유형을 선택하세요") // 필요하면 유형별 설명 추가
                .setPositiveButton("바로 종료하기") { _, _ ->
                    appOptHolder.set_wakeup_opt(0)
                    editor.putInt("wakeup_opt", 0)
                    editor.apply()
                    current_alarm_type.text = "바로 종료하기"
                }
                .setNeutralButton("알람 연장하기") { _, _ ->
                    appOptHolder.set_wakeup_opt(1)
                    editor.putInt("wakeup_opt", 1)
                    editor.apply()
                    current_alarm_type.text = "알람 연장하기"
                }
                .setNegativeButton("알림만 띄우기") { _, _ ->
                    appOptHolder.set_wakeup_opt(2)
                    editor.putInt("wakeup_opt", 2)
                    editor.apply()
                    current_alarm_type.text = "알림만 띄우기"
                }
            builder.show()
        }

        cooltime_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            appOptHolder.set_cooltime_bool(isChecked)
            editor.putBoolean("cooltime_bool", isChecked)
            editor.apply()
        }

        cooltime_time.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.edittext, null)
            val editTime = dialogView.editTime
            editTime.setHint("  ".plus((appOptHolder.get_cooltime()/60000).toString().plus("분, 숫자만 입력하세요")))

            val builder = AlertDialog.Builder(this)
            builder.setTitle("쿨타임을 입력하세요")
                .setView(dialogView)
                .setPositiveButton("확인"){_, _ ->
                    if(!editTime.text.isBlank()){
                        appOptHolder.set_cooltime(editTime.text.toString().toLong() * 60000)    // ms로 변환
                        editor.putInt("cooltime", editTime.text.toString().toInt() * 60000) // ms로 변환
                        editor.apply()
                        current_cooltime_time.text = editTime.text.toString().plus("분")
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
        alarm_time.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.edittext, null)
            val editTime = dialogView.editTime
            editTime.setHint("  ".plus((appOptHolder.get_alarmtime()/60000).toString().plus("분, 숫자만 입력하세요")))

            val builder = AlertDialog.Builder(this)
            builder.setTitle("알람 시간을 입력하세요")
                .setView(dialogView)
                .setPositiveButton("확인"){_, _ ->
                    if(!editTime.text.isBlank()){
                        appOptHolder.set_alarmtime(editTime.text.toString().toLong() * 60000)   //ms로 변환
                        editor.putInt("alarmtime", editTime.text.toString().toInt() * 60000) // ms로 변환
                        editor.apply()
                        current_alarm_time.text = editTime.text.toString().plus("분")
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    override fun onResume() {
        var list_str = ""
        appOptHolder.get_blocked_apps()?.forEach {
            list_str = list_str.plus(getAppName(it).plus(", "))
        }
        if (list_str.length > 3)
            tracked_apps.text = list_str.substring(0, list_str.length-2)
        apps_to_monitor.invalidate()

        monitoring_switch.isChecked = appOptHolder.get_monitoring_flag()
        cooltime_switch.isChecked = appOptHolder.get_cooltime_bool()
        current_alarm_type.text = when (appOptHolder.get_wakeup_opt()) {
            0 -> "바로 종료하기"
            1 -> "알람 연장하기"
            2 -> "알림만 띄우기"
            else -> "error: could not bring settings"
        }

        if (appOptHolder.get_blocked_apps() != null) {
            setStringArrayPref("blocked_apps", appOptHolder.get_blocked_apps()!!)
        }

        super.onResume()
    }

    private fun getAppName(packageName: String): String{
        val pm = packageManager
        val ai: ApplicationInfo?
        ai = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: java.lang.Exception) {
            null
        }
        val applicationName =
            (if (ai != null) pm.getApplicationLabel(ai) else packageName.substring(packageName.lastIndexOf('.') + 1)) as String
        return applicationName
    }

    private fun setStringArrayPref(
        key: String,
        values: ArrayList<String>
    ) {
        val pref : SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()
        val a = JSONArray()
        for (i in 0 until values.size) {
            a.put(values[i])
        }
        if (values.isNotEmpty()) {
            editor.putString(key, a.toString())
        } else {
            editor.putString(key, null)
        }
        editor.apply()
    }
}