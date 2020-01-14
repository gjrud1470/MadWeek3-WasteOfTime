package com.example.wasteoftime

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import java.util.*
import kotlin.concurrent.timerTask

class CoolTimeService: IntentService("CoolTimeService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val cooltime = appOptHolder.get_cooltime()
            val timerTest = Timer()

            timerTest.schedule(timerTask{
                val foreground_Name = getForegroundName()
                if (foreground_Name != null && appOptHolder.get_blocked_apps() != null
                    && foreground_Name in appOptHolder.get_blocked_apps()!!) {
                    val cooltimeintent = Intent(this@CoolTimeService, CoolTimeActivity::class.java)
                    startActivity(cooltimeintent)
                }
            }, 0,1000) // 1 sec

            Handler().postDelayed({
                Intent(this, GetForegroundService::class.java).also { intent_service ->
                    startService(intent_service)
                    timerTest.cancel()
                    stopSelf()
                }
            }, cooltime)

        }catch (e : InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    private fun getForegroundName() : String? {
        return if (Build.VERSION.SDK_INT >= 21) {
            var currentApp: String? = null
            val usm =
                this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val applist =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (applist != null && applist.size > 0) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in applist) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName
                }
            }
            //Log.e(TAG, "Current App in foreground is: $currentApp")
            currentApp?.substring(currentApp.lastIndexOf('.')+1)
        } else {
            val manager =
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mm = manager.getRunningTasks(1)[0].topActivity!!.packageName
            //Log.e(TAG, "Current App in foreground is: $mm")
            mm.substring(packageName.lastIndexOf('.')+1)
        }
    }
}