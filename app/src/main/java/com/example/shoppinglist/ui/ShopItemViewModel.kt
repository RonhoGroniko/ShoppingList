package com.example.shoppinglist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.AddShopItemUseCase
import com.example.shoppinglist.domain.EditShopItemUseCase
import com.example.shoppinglist.domain.GetShopItemByIdUseCase
import com.example.shoppinglist.domain.ShopItem

class ShopItemViewModel : ViewModel() {

    private val repository = ShopListRepositoryImpl
    private val getShopItemByIdUseCase = GetShopItemByIdUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)

    private val _errorInputNameLD = MutableLiveData<Boolean>()
    val errorInputNameLD: LiveData<Boolean>
        get() = _errorInputNameLD

    private val _errorInputCountLD = MutableLiveData<Boolean>()
    val errorInputCountLD: LiveData<Boolean>
        get() = _errorInputCountLD

    private val _shouldCloseScreenLD = MutableLiveData<Unit>()
    val shouldCloseScreenLD: LiveData<Unit>
        get() = _shouldCloseScreenLD
    private val _shopItemLD = MutableLiveData<ShopItem>()
    val shopItemLD: LiveData<ShopItem>
        get() = _shopItemLD

    fun getShopItem(id: Int) {
        val item = getShopItemByIdUseCase.getShopItemById(id)
        _shopItemLD.value = item
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _shopItemLD.value?.let {
                val item = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(item)
                finishWork()
            }
        }
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name = name, count = count, enabled = true)
            addShopItemUseCase.addShopItem(shopItem)
            finishWork()
        }
    }

    private fun parseName(str: String?): String {
        return str?.trim() ?: ""
    }

    private fun parseCount(number: String?): Int {
        return number?.trim()?.toIntOrNull() ?: 0
    }

    private fun validateInput(name: String, count: Int): Boolean {
        if (name.isBlank()) {
            _errorInputNameLD.value = true
            return false
        }
        if (count <= 0) {
            _errorInputCountLD.value = true
            return false
        }
        return true
    }

    fun resetInputNameError() {
        _errorInputNameLD.value = false
    }

    fun resetInputCountError() {
        _errorInputCountLD.value = false
    }

    private fun finishWork() {
        _shouldCloseScreenLD.value = Unit
    }
}