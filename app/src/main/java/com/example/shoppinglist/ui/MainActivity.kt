package com.example.shoppinglist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutShopList: LinearLayout
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        linearLayoutShopList = findViewById(R.id.linearLayoutItems)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.shopList.observe(this) {
            showList(it)
        }
    }

    private fun showList(list: List<ShopItem>) {
        linearLayoutShopList.removeAllViews()
        for (item in list) {
            val layoutId = when {
                item.enabled -> {
                    R.layout.item_shop_enabled
                }
                else -> {
                    R.layout.item_shop_disabled
                }
            }
            val view = LayoutInflater.from(this).inflate(
                layoutId,
                linearLayoutShopList,
                false
            )
            val textViewName = view.findViewById<TextView>(R.id.textViewItemName)
            val textViewCount = view.findViewById<TextView>(R.id.textViewItemCount)
            textViewName.text = item.name
            textViewCount.text = item.count.toString()
            view.setOnLongClickListener {
                viewModel.changeEnabledState(item)
                true
            }
            linearLayoutShopList.addView(view)
        }
    }
}

