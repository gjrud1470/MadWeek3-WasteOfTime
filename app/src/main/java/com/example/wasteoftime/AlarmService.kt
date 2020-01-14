package com.example.wasteoftime

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat


class AlarmService : IntentService("AlarmService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val alarmtime = appOptHolder.get_alarmtime()

            // selected close application. Then go to home screen
            if (alarmtime == 0.toLong()) {
                //appOptHolder.set_alarmtime(R.integer.default_alarm_time.toLong())
                val startHomescreen = Intent(Intent.ACTION_MAIN)
                startHomescreen.addCategory(Intent.CATEGORY_HOME)
                startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(startHomescreen)
            }

            // selected alarm function. Set alarm x min later then prompt.
            else if (alarmtime > 0.toLong()) {
                Thread.sleep(alarmtime)
                val wakeupintent = Intent(this, WakeupActivity::class.java)
                startActivity(wakeupintent)

                // add here if we want to allow extend time.
            }

            // selected no alarm function. Finish Service without doing anything.
            else {
                //appOptHolder.set_alarmtime(R.integer.default_alarm_time.toLong())
            }
        }catch (e : InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        var builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
            //.setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("STOP PLAYING")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        startForeground(1, builder.build())
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
}