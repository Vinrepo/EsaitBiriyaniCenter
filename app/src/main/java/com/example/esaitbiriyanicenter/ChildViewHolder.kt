package com.restaurant.esaitbiriyanicenter
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.section_child.view.*

class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.child;
}