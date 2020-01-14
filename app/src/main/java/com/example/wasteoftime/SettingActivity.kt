package com.example.wasteoftime

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.edittext.view.*
import kotlinx.android.synthetic.main.setting.*

class SettingActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        monitoring_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            appOptHolder.set_monitoring_flag(isChecked)
        }

        apps_to_monitor.setOnClickListener {
            startActivity(Intent(this, AppSelectActivity::class.java))
        }

        alarm_type.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("알람 유형을 선택하세요") // 필요하면 유형별 설명 추가
                .setPositiveButton("바로 종료하기") { _, _ ->
                    appOptHolder.set_wakeup_opt(0)
                    current_alarm_type.text = "바로 종료하기"
                }
                .setNeutralButton("알람 연장하기") { _, _ ->
                    appOptHolder.set_wakeup_opt(1)
                    current_alarm_type.text = "알람 연장하기"
                }
                .setNegativeButton("알림만 띄우기") { _, _ ->
                    appOptHolder.set_wakeup_opt(2)
                    current_alarm_type.text = "알림만 띄우기"
                }
            builder.show()
        }

        cooltime_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            appOptHolder.set_cooltime_bool(isChecked)
        }

        cooltime_time.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.edittext, null)
            val editTime = dialogView.editTime
            editTime.setHint("  ".plus(appOptHolder.get_cooltime().toString().plus("분, 숫자만 입력하세요")))

            val builder = AlertDialog.Builder(this)
            builder.setTitle("쿨타임을 입력하세요")
                .setView(dialogView)
                .setPositiveButton("확인"){_, _ ->
                    if(!editTime.text.isBlank()){
                        appOptHolder.set_cooltime(editTime.text.toString().toLong())
                        current_cooltime_time.text = editTime.text.toString().plus("분")
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
        alarm_time.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.edittext, null)
            val editTime = dialogView.editTime
            editTime.setHint("  ".plus(appOptHolder.get_alarmtime().toString().plus("분, 숫자만 입력하세요")))

            val builder = AlertDialog.Builder(this)
            builder.setTitle("알람 시간을 입력하세요")
                .setView(dialogView)
                .setPositiveButton("확인"){_, _ ->
                    if(!editTime.text.isBlank()){
                        appOptHolder.set_alarmtime(editTime.text.toString().toLong())
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
}