package com.real.hoop_locater.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.real.hoop_locater.R
import com.real.hoop_locater.menu.SettingMenu

class SettingMenuRVAdapter(val context: Context, val list: MutableList<SettingMenu>): RecyclerView.Adapter<SettingMenuRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingMenuRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_setting_menu, parent, false)
        return ViewHolder(v)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: SettingMenuRVAdapter.ViewHolder, position: Int) {
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener {v ->
                itemClick!!.onClick(v, position)
            }
        }
        holder.bindItems(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: SettingMenu) {
            itemView.findViewById<TextView>(R.id.rvTextView).text = item.krName
        }
    }

}