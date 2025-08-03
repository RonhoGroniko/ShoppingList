package com.example.shoppinglist.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.databinding.FragmentShopItemBinding
import com.example.shoppinglist.domain.ShopItem

class ShopItemFragment(): Fragment() {

    private lateinit var viewModel: ShopItemViewModel
    private lateinit var onEditSuccessListener: OnEditSuccessListener
    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == NULL")
    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditSuccessListener) {
            onEditSuccessListener = context
        } else {
            throw RuntimeException("OnEditSuccessListener is NOT implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        observeViewModel()
        setupTextChangeListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTextChangeListeners() {
        binding.editTextName.doOnTextChanged { _, _, _, _ ->
            viewModel.resetInputNameError()
        }
        binding.editTextCount.doOnTextChanged { _, _, _, _ ->
            viewModel.resetInputCountError()
        }
    }

    private fun observeViewModel() {
        viewModel.shouldCloseScreenLD.observe(viewLifecycleOwner) {
            onEditSuccessListener.onEditSuccess()
        }
    }

    private fun launchAddMode() {
        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text?.toString()
            val count = binding.editTextCount.text?.toString()
            viewModel.addShopItem(name, count)
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text?.toString()
            val count = binding.editTextCount.text?.toString()
            viewModel.editShopItem(name, count)
        }
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