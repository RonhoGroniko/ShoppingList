package com.example.shoppinglist.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R

class ShopListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textViewName: TextView = view.findViewById(R.id.textViewItemName)
    val textViewCount: TextView = view.findViewById(R.id.textViewItemCount)
}