package com.example.shoppinglist.ui

import android.app.Application
import com.example.shoppinglist.di.DaggerApplicationComponent

class ShopListApp: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}