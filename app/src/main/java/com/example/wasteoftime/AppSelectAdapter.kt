package com.example.wasteoftime

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class AppSelectAdapter(val context: Context, val appList: ArrayList<AppItem>) :
    RecyclerView.Adapter<AppSelectAdapter.Holder>() {

    override fun getItemCount(): Int {
        return appList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.app_select_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(appList[position], context)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon = itemView?.findViewById<ImageView>(R.id.app_icon)
        val appName = itemView?.findViewById<TextView>(R.id.app_name)
        val appChecked = itemView?.findViewById<ImageView>(R.id.checked)

        fun bind (item: AppItem, context: Context) {
            if (item.getName() != null) {
                appName?.text = getAppName(item.getName()!!)
            }
            if(item.getIcon() != null){
                appIcon.setImageDrawable(item.getIcon())
            }

            if(item.getIsChecked()){
                appChecked.visibility = View.VISIBLE
            } else {
                appChecked.visibility = View.GONE
            }

            itemView.setOnClickListener{
                item.setIsChecked(!item.getIsChecked())
                if(item.getIsChecked()){
                    if(appOptHolder.get_blocked_apps() != null && item.getName() != null){
                        appOptHolder.get_blocked_apps()!!.add(item.getName()!!)
                        appOptHolder.printList()
                    }
                } else {
                    if(appOptHolder.get_blocked_apps() != null && item.getName() != null){
                        appOptHolder.get_blocked_apps()!!.remove(item.getName()!!)
                        appOptHolder.printList()
                    }
                }
                if(item.getIsChecked()) {
                    appChecked.visibility = View.VISIBLE
                } else {
                    appChecked.visibility = View.GONE
                }
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