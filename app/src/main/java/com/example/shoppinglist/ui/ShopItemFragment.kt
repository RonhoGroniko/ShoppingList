package com.example.shoppinglist.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
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

class ShopItemFragment(): Fragment() {

    private lateinit var viewModel: ShopItemViewModel
    private lateinit var onEditSuccessListener: OnEditSuccessListener

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutCount: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextCount: TextInputEditText
    private lateinit var buttonSave: Button

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    override fun onAttach(context: Context) {
        Log.d("ShopItemFragment", "onAttach")
        super.onAttach(context)
        if (context is OnEditSuccessListener) {
            onEditSuccessListener = context
        } else {
            throw RuntimeException("OnEditSuccessListener is NOT implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ShopItemFragment", "onCreate")
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onResume() {
        Log.d("ShopItemFragment", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("ShopItemFragment", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("ShopItemFragment", "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("ShopItemFragment", "onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.d("ShopItemFragment", "onDetach")
        super.onDetach()
    }

    override fun onDestroy() {
        Log.d("ShopItemFragment", "onDestroy")
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ShopItemFragment", "onCreateView")
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("ShopItemFragment", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
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
            onEditSuccessListener.onEditSuccess()
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

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Param mode is unknown: $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shopItemId is absent")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID)
        }
    }

    interface OnEditSuccessListener {

        fun onEditSuccess()
    }

    companion object {
        private const val SCREEN_MODE = "extra_screen_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"

        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}