package com.example.wasteoftime

import android.graphics.drawable.Drawable

class AppUsageItem {
    private var icon: Drawable? = null
    private var name: String? = null
    private var usageTimeList: ArrayList<Long>? = null

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

    fun getUsageTimeList(): ArrayList<Long>? {
        return usageTimeList
    }
    fun addUsageTimeToday(usageTimeToday: Long){
        if(usageTimeList == null){
            usageTimeList = ArrayList()
        }
        usageTimeList?.add(usageTimeToday)
    }
}