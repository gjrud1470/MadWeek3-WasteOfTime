package com.example.wasteoftime

import android.app.Application
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.app_select.*

class AppItem{
    private var icon: Drawable? = null
    private var name: String? = null
    private var isChecked: Boolean = false

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

    fun getIsChecked(): Boolean{
        return isChecked
    }
    fun setIsChecked(checked: Boolean){
        isChecked = checked
    }
}

class AppSelectActivity: AppCompatActivity(){
    private val appList = ArrayList<AppItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_select)

        //Bring all installed app package names and set Icon & name
        val list = packageManager.getInstalledApplications(0)
        val mask = ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM

        list.forEach {
            if(it.processName.toString().contains("youtube")){

            }
            if(it.category in arrayOf(0, 2, 4) || it.flags and mask == 0) {
                val item = AppItem()
                item.setName(it.processName)
                Log.wtf("REACH", "get process name: ${it.processName} ${it.category}")
                if (packageManager.getApplicationIcon(it) != null) {
                    item.setIcon(packageManager.getApplicationIcon(it))
                }
                if(appOptHolder.get_blocked_apps()?.contains(it.processName)!!){
                    item.setIsChecked(true)
                }
                appList.add(item)
            }
        }
        val rcView = this.appRecyclerView
        val adapter = AppSelectAdapter(this, appList)
        rcView.adapter = adapter
    }

    private fun setRecyclerView(){
        val rcView = this.appRecyclerView
        val adapter = AppSelectAdapter(this, appList)
        rcView.adapter = adapter
    }
}