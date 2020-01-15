package com.example.wasteoftime

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.*
import android.os.Build.VERSION
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*
import kotlin.concurrent.timerTask


class GetForegroundService : Service() {

    private val TAG = "GetForegroundService"

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    //var timerTest = Timer()

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                val timerTest = Timer()

                timerTest.schedule(timerTask{
                    if (!appOptHolder.get_monitoring_flag()) {
                        Log.wtf(TAG, "monitor boolean is set to false")
                        timerTest.cancel()
                    }

                    val foreground_Name = getForegroundName()
                    Log.wtf(TAG, "foreground name: ${foreground_Name}")
                    if (foreground_Name != null && appOptHolder.get_blocked_apps() != null
                        && foreground_Name in appOptHolder.get_blocked_apps()!!) {
                        val alarmintent = Intent(this@GetForegroundService, SetAlarmActivity::class.java)
                        startActivity(alarmintent)

                        timerTest.cancel()
                    }
                }, 0,1000) // 1 sec

            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        createNotificationChannel()
        val builder = NotificationCompat.Builder(applicationContext, getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Monitoring App Usage...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        startForeground(1, builder)

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        AsyncTask.execute {
            serviceHandler?.obtainMessage()?.also { msg ->
                msg.arg1 = startId
                serviceHandler?.sendMessage(msg)
            }
        }

        //timerTest = Timer()

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    /*
    override fun onDestroy() {
        super.onDestroy()
        timerTest.cancel()

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        //this.sendBroadcast(broadcastIntent)
    } */

    private fun getForegroundName() : String? {
        return if (VERSION.SDK_INT >= 21) {
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
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /*
    private class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("Broadcast Listened", "Screen Toggled")
            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                && appOptHolder.get_monitoring_flag()){
                Log.i("[BroadcastReceiver]", "Screen ON")
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Log.i("[BroadcastReceiver]", "Screen OFF")
            }
        }
    } */
}
/*
class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Broadcast Listened", "Service tried to stop")
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()
        if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, GetForegroundService::class.java))
        } else {
            context.startService(Intent(context, GetForegroundService::class.java))
        }
    }
} */