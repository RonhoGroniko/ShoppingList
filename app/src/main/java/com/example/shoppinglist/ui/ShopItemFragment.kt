package com.example.shoppinglist.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment(
    private val screenMode: String = MODE_UNKNOWN,
    private val shopItemId: Int = ShopItem.UNDEFINED_ID
): Fragment() {

    private lateinit var viewModel: ShopItemViewModel

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutCount: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextCount: TextInputEditText
    private lateinit var buttonSave: Button




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseParams()
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews(view)
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
        observeViewModel()
        setupTextChangeListeners()
    }

    private fun setupTextChangeListeners() {
        editTextName.doOnTextChanged { _, _, _, _ ->
            viewModel.resetInputNameError()
        }
        editTextCount.doOnTextChanged { _, _, _, _ ->
            viewModel.resetInputCountError()
        }
    }

    private fun observeViewModel() {
        viewModel.errorInputNameLD.observe(viewLifecycleOwner) { isError ->
            val message = if (isError) {
                getString(R.string.error_input_name)
            } else {
                null
            }
            textInputLayoutName.error = message
        }
        viewModel.errorInputCountLD.observe(viewLifecycleOwner) { isError ->
            val message = if (isError) {
                getString(R.string.error_input_count)
            } else {
                null
            }
            textInputLayoutCount.error = message
        }
        viewModel.shouldCloseScreenLD.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun launchAddMode() {
        buttonSave.setOnClickListener {
            val name = editTextName.text?.toString()
            val count = editTextCount.text?.toString()
            viewModel.addShopItem(name, count)
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItemLD.observe(viewLifecycleOwner) {
            editTextName.setText(it.name)
            editTextCount.setText(it.count.toString())
        }
        buttonSave.setOnClickListener {
            val name = editTextName.text?.toString()
            val count = editTextCount.text?.toString()
            viewModel.editShopItem(name, count)
        }
    }

    private fun initViews(view: View) {
        textInputLayoutName = view.findViewById(R.id.textInputLayoutName)
        textInputLayoutCount = view.findViewById(R.id.textInputLayoutCount)
        editTextName = view.findViewById(R.id.editTextName)
        editTextCount = view.findViewById(R.id.editTextCount)
        buttonSave = view.findViewById(R.id.buttonSave)
    }

//    private fun parseIntent() {
//        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
//            throw RuntimeException("Param screen mode is absent")
//        }
//        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
//        if (mode != MODE_EDIT && mode != MODE_ADD) {
//            throw RuntimeException("Unknown mode $mode")
//        }
//        screenMode = mode
//        if (screenMode == MODE_EDIT) {
//            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
//                throw RuntimeException("Param shopItemId is absent")
//            }
//            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
//        }
//    }

    private fun parseParams() {
        if (screenMode != MODE_ADD && screenMode != MODE_EDIT) {
            throw RuntimeException("Param screen mode is absent")
        }
        if (screenMode == MODE_EDIT && shopItemId == ShopItem.UNDEFINED_ID) {
            throw RuntimeException("Param shopItemId is absent")
        }
    }

    companion object {
        private const val EXTRA_SCREEN_MODE = "extra_screen_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"

        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment(MODE_ADD)
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment(MODE_EDIT, shopItemId)
        }

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }
    }

}