package com.example.wasteoftime

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Process.myUid
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class AppOptHolder {
    private var blocked_apps : ArrayList<String>? = null
    private var cooltime_bool : Boolean = true
    private var cooltime : Long = R.integer.default_alarm_time.toLong()   // default 30 min
    private var alarmtime : Long = R.integer.default_alarm_time.toLong()    // default 30 min

    fun get_blocked_apps () : ArrayList<String>? {
        return blocked_apps
    }

    fun set_blocked_apps (list : ArrayList<String>) {
        blocked_apps = list
    }

    fun get_cooltime () : Long {
        return cooltime
    }

    fun set_cooltime (time : Long) {
        cooltime = time
    }

    fun get_alarmtime () : Long {
        return alarmtime
    }

    fun set_alarmtime (time : Long) {
        alarmtime = time
    }
}

val appOptHolder = AppOptHolder()

class MainActivity : AppCompatActivity() {

    private val TAG = "WORK"
    private val appUsageList = ArrayList<AppUsageItem>()
    var mAdapter: AppUsageAdapter? = null
    val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appOptHolder.set_blocked_apps(arrayListOf("youtube"))

        if (!checkForPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))    // permission settings
        }
        else {
            Intent(this, GetForegroundService::class.java).also { intent ->
                startForegroundService(intent)
            }
        }

        setRecyclerView()

//        if (true) { //tutorial 끝날 때 isFirst를 true로 바꿔주기
//            mHandler.post {
//                setContentView(R.layout.inform_1)
//                next_page.setOnClickListener {
//                    setContentView(R.layout.inform_2)
//                    get_started.setOnClickListener {
//                        setContentView(R.layout.activity_main)
//                        setUsageList()
//                    }
//                }
//            }
//        } else {
//            setUsageList()
//        }
    }
    fun setRecyclerView(){
        val rcView = mRecyclerView
        val adapter = AppUsageAdapter(this, appUsageList)
        rcView.adapter = adapter
    }

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), packageName)
        return mode == MODE_ALLOWED
    }

    private fun getAppUsageStats(): MutableList<UsageStats> {
        val beginCal = Calendar.getInstance()
        beginCal.add(Calendar.DATE, -1)    // 1
        val endCal = Calendar.getInstance()

        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // 2
        val queryUsageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, beginCal.timeInMillis, endCal.timeInMillis // 3
        )
        return queryUsageStats
    }

    private fun setAppUsageStats(usageStats: MutableList<UsageStats>) {
        usageStats.sortWith(Comparator { left, right ->
            compareValues(
                left.packageName.substring(left.packageName.lastIndexOf('.') + 1),
                right.packageName.substring(right.packageName.lastIndexOf('.') + 1)
            )
        })
        if (usageStats.size == 0) {
            Log.wtf(TAG, "empty!")
        }

        usageStats.forEach { it ->
            if (it.totalTimeInForeground > 10000) { //used more than a second
                val name = it.packageName.substring(it.packageName.lastIndexOf('.') + 1)
                val idx = appListIdx(name)
                if (idx == -1) {
                    val item = AppUsageItem()
                    val pm = packageManager
                    try {
                        item.setIcon(pm.getApplicationIcon(it.packageName))
                        item.setName(name)
                        item.setUsageTime(it.totalTimeInForeground)
                        appUsageList.add(item) // if the app is already in the list?
                    } catch (e: Exception) {
                        Log.wtf(TAG, "something's gone wrong")
                        e.printStackTrace()
                    }
                    //Log.wtf(TAG, "appName: ${it.packageName.substring(it.packageName.lastIndexOf('.') + 1)}, totalTimeInForeground: ${it.totalTimeInForeground}")
                } else {
                    //Log.d(TAG, "already in list") -> apps with same name?
                    appUsageList.get(idx).setUsageTime(it.totalTimeInForeground)
                }
            }
        }
    }

    private fun appListIdx(name: String): Int {
        if (!appUsageList.isEmpty()) {
            for (i in appUsageList.indices) {
                if (appUsageList.get(i).getName().equals(name)) {
                    //Log.d("Index Found", i.toString())
                    return i
                }
            }
        }
        return -1
    }

//    private fun getForegroundPackageName(): String? {
//        var packageName: String? = null
//        val usageStatsManager =
//            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val endTime = System.currentTimeMillis()
//        val beginTime = endTime - 10000
//        val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
//        while (usageEvents.hasNextEvent()) {
//            val event = UsageEvents.Event()
//            usageEvents.getNextEvent(event)
//            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
//                packageName = event.packageName
//            }
//        }
//        return packageName
//    }

    private fun logAppList() {
        Log.wtf("PRINT", "LIST")
        appUsageList.forEach { item ->
            Log.d(TAG, item.getName())
            Log.d(TAG, item.getUsageTime().toString())
        }
    }
}