package com.example.shoppinglist.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository

class ShopListRepositoryImpl(application: Application) : ShopListRepository {

    private val database = AppDatabase.getInstance(application)
    private val mapper = ShopListMapper()

    override suspend fun addShopItem(shopItem: ShopItem) {
        database.shopListDao().addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        database.shopListDao().deleteShopItem(shopItem.id)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        database.shopListDao().addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun getShopItemById(shopItemId: Int): ShopItem {
        return mapper.mapDbModelToEntity(database.shopListDao().getShopItem(shopItemId))
    }

    override fun getShopList(): LiveData<List<ShopItem>> = database.shopListDao().getShopList().map {
        mapper.mapDbModelListToEntityList(it)
    }

}