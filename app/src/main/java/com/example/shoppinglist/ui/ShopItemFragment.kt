package com.example.shoppinglist.ui

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.databinding.FragmentShopItemBinding
import com.example.shoppinglist.domain.ShopItem
import javax.inject.Inject
import kotlin.concurrent.thread

class ShopItemFragment() : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ShopItemViewModel::class.java]
    }
    private lateinit var onEditSuccessListener: OnEditSuccessListener
    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == NULL")
    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    private val component by lazy {
        (requireActivity().application as ShopListApp).component
    }

    override fun onAttach(context: Context) {
        component.inject(this)
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
//            viewModel.addShopItem(name, count)
            thread {
                context?.contentResolver?.insert(
                    "content://com.example.shoppinglist/shop_items".toUri(),
                    ContentValues().apply {
                        put("id", 0)
                        put("name", name)
                        put("count", count)
                        put("enabled", true)
                    })
            }
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text?.toString()
            val count = binding.editTextCount.text?.toString()
//            viewModel.editShopItem(name, count)
            thread {
                context?.contentResolver?.update(
                    "content://com.example.shoppinglist/shop_items".toUri(),
                    ContentValues().apply {
                        put("id", shopItemId)
                        put("name", name)
                        put("count", count)
                        put("enabled", true)
                    },
                    null,
                    arrayOf(shopItemId.toString())
                )
            }
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