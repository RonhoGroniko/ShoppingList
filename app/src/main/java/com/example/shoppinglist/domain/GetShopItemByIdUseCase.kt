package com.example.shoppinglist.domain

import javax.inject.Inject

class GetShopItemByIdUseCase @Inject constructor (
    private val shopListRepository: ShopListRepository
) {

    suspend fun getShopItemById(shopItemId: Int): ShopItem {
        return shopListRepository.getShopItemById(shopItemId)
    }
}