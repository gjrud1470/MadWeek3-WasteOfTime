package com.example.wasteoftime

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*
import kotlin.concurrent.timerTask

class CoolTimeService: IntentService("CoolTimeService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            if (!appOptHolder.get_cooltime_bool()) {
                stopForeground(true)
                Log.wtf("cooltime", "cooltime boolean is set to false")
                Intent(this@CoolTimeService, GetForegroundService::class.java).also { intent_service ->
                    startService(intent_service)
                }
                return
            }

            val cooltime = appOptHolder.get_cooltime()
            val timerTest = Timer()
            val num_loops = cooltime/1000
            var iteration = 0

            timerTest.schedule(timerTask{
                if (iteration >= num_loops) {
                    Intent(this@CoolTimeService, GetForegroundService::class.java).also { intent_service ->
                        startService(intent_service)
                    }
                    timerTest.cancel()
                    stopForeground(true)
                }

                val foreground_Name = getForegroundName()
                Log.wtf("Cooltime service", "foreground name: ${foreground_Name}, ${cooltime}")
                if (foreground_Name != null && appOptHolder.get_blocked_apps() != null
                    && foreground_Name in appOptHolder.get_blocked_apps()!!) {
                    val cooltimeintent = Intent(this@CoolTimeService, CoolTimeActivity::class.java)
                    startActivity(cooltimeintent)
                }
                iteration++
            }, 0,1000) // 1 sec

        }catch (e : InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    override fun onCreate() {
        super.onCreate()

        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
            //.setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("ON COOLTIME")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(4, builder.build())
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
            currentApp
        } else {
            val manager =
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mm = manager.getRunningTasks(1)[0].topActivity!!.packageName
            //Log.e(TAG, "Current App in foreground is: $mm")
            mm
        }
    }
}