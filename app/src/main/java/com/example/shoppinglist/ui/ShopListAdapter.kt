package com.example.shoppinglist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopListAdapter : ListAdapter<ShopItem, ShopListViewHolder>(
    ShopItemDiffCallback()
) {

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShopListViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("UNKNOWN VIEW TYPE $viewType")
        }
        val view =
            LayoutInflater.from(parent.context).inflate(
                layout,
                parent,
                false
            )
        return ShopListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ShopListViewHolder,
        position: Int
    ) {
        val shopItem = getItem(position)
        holder.textViewName.text = shopItem.name
        holder.textViewCount.text = shopItem.count.toString()
        holder.itemView.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }
        holder.itemView.setOnClickListener {
            onShopItemClickListener?.invoke(shopItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.enabled) {
            true -> VIEW_TYPE_ENABLED
            false -> VIEW_TYPE_DISABLED
        }
    }

    companion object {
        const val VIEW_TYPE_ENABLED = 100
        const val VIEW_TYPE_DISABLED = -100

        const val MAX_POOL_SIZE = 15
    }
}