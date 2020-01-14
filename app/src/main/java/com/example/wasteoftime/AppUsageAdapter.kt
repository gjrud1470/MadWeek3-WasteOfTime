package com.example.wasteoftime

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppUsageAdapter(val context: Context, val appUsageList: ArrayList<AppUsageItem>) :
    RecyclerView.Adapter<AppUsageAdapter.Holder>() {

    override fun getItemCount(): Int {
        return appUsageList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.usage_list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(appUsageList[position], context)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon = itemView?.findViewById<ImageView>(R.id.app_icon)
        val appName = itemView?.findViewById<TextView>(R.id.app_name)
        val usageTime = itemView?.findViewById<TextView>(R.id.usageTime)

        fun bind (usageItem: AppUsageItem, context: Context) {
            if (usageItem.getName() != null) {
                appName?.text = getAppName(usageItem.getName()!!)
            }
            usageTime.text = (usageItem.getUsageTime()/60000).toString().plus("분")
            if(usageItem.getIcon() != null){
                appIcon.setImageDrawable(usageItem.getIcon())
            }
        }
    }
    private fun getAppName(packageName: String): String{
        val pm = context.packageManager
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