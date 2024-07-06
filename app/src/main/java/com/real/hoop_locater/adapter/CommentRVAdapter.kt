package com.real.hoop_locater.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.real.hoop_locater.R
import com.real.hoop_locater.dto.hoop.Comment
import com.real.hoop_locater.menu.SettingMenu

class CommentRVAdapter(val context: Context, val list: List<Comment>): RecyclerView.Adapter<CommentRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_comments, parent, false)
        return ViewHolder(v)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: CommentRVAdapter.ViewHolder, position: Int) {
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
        fun bindItems(item: Comment) {
            itemView.findViewById<TextView>(R.id.commentWriter).text = item.writer
            itemView.findViewById<TextView>(R.id.commentWriteDate).text = item.writeDate
            itemView.findViewById<TextView>(R.id.commentContent).text = item.content
        }
    }

}