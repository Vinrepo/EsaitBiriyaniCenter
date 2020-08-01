package com.restaurant.esaitbiriyanicenter
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sectionheader.view.*

class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.section;
}