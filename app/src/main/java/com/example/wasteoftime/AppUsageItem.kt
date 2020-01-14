package com.example.wasteoftime

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.wasteoftime.R.drawable
import com.example.wasteoftime.R.drawable.ic_launcher_foreground

class AppUsageItem {
    private var icon: Drawable? = null
    private var name: String? = null
    private var usageTime: Long = 0

    fun getIcon(): Drawable? {
        return icon
    }
    fun setIcon(icon: Drawable?){
        this.icon = icon
    }

    fun getName(): String? {
        return name
    }
    fun setName(name: String?){
        this.name = name
    }

    fun getUsageTime(): Long {
        return usageTime
    }
    fun setUsageTime(usageTimeToday: Long){
        usageTime = usageTimeToday
    }
}