package com.example.wasteoftime

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*


class AlarmService : IntentService("AlarmService") {

    var mNotificationManager: NotificationManagerCompat? = null

    override fun onHandleIntent(intent: Intent?) {
        try {
            val alarmtime = appOptHolder.get_alarmtime()

            // selected close application. Then go to home screen
            if (alarmtime == 0.toLong()) {
                //appOptHolder.set_alarmtime(R.integer.default_alarm_time.toLong())
                start_getforeground()
                open_home()
            }

            // selected alarm function. Set alarm x min later then prompt.
            else if (alarmtime > 0.toLong()) {
                Thread.sleep(alarmtime)
                val foreground_Name = getForegroundName()
                if (foreground_Name != null && appOptHolder.get_blocked_apps() != null
                    && foreground_Name in appOptHolder.get_blocked_apps()!!
                ) {
                    val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    if (appOptHolder.get_wakeup_opt() == 2) {
                        val builder = NotificationCompat.Builder(
                            applicationContext,
                            getString(R.string.channel_name_high)
                        )
                            .setSmallIcon(R.mipmap.waste_icon_foreground)
                            .setContentTitle("Waste of Time")
                            .setContentText("Time to stop Playing!")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setFullScreenIntent(fullScreenPendingIntent, true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setTicker("Time to stop Playing!")
                            .build()

                        mNotificationManager?.notify(3, builder)
                    } else if (appOptHolder.get_wakeup_opt() == 1) {
                        val wakeupintent = Intent(this, WakeupExtendActivity::class.java)
                        startActivity(wakeupintent)
                    } else if (appOptHolder.get_wakeup_opt() == 0) {
                        val wakeupintent = Intent(this, WakeupCloseActivity::class.java)
                        startActivity(wakeupintent)
                    }
                }
            }

            // selected no alarm function. Finish Service without doing anything.
            else {
                //appOptHolder.set_alarmtime(R.integer.default_alarm_time.toLong())
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        mNotificationManager = NotificationManagerCompat.from(this)

        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
            //.setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("STOP PLAYING")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(2, builder.build())
    }

    private fun getForegroundName(): String? {
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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name_high)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(getString(R.string.channel_name_high), name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
        val startHomescreen = Intent(Intent.ACTION_MAIN)
        startHomescreen.addCategory(Intent.CATEGORY_HOME)
        startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(startHomescreen)
    }
}