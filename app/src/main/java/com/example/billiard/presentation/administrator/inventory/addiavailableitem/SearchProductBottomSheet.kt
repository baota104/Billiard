package com.example.billiard.presentation.administrator.inventory.addiavailableitem

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetSearchProductBinding
import com.example.billiard.domain.model.Product
import com.example.billiard.presentation.adapter.ProductSearchAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchProductBottomSheet(
    private val allProducts: List<Product>, // Dữ liệu đã fetch sẵn
    private val onProductSelected: (Product) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSearchProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: ProductSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        binding.btnClose.setOnClickListener { dismiss() }

        setupRecyclerView()
        
        // Mặc định hiện tất cả sản phẩm
        searchAdapter.submitList(allProducts)

        // Xử lý tìm kiếm Local
        binding.edtSearchInput.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim() ?: ""
            if (query.isEmpty()) {
                searchAdapter.submitList(allProducts)
            } else {
                val filteredList = allProducts.filter { 
                    it.name.contains(query, ignoreCase = true) 
                }
                searchAdapter.submitList(filteredList)
            }
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = ProductSearchAdapter { selectedProduct ->
            onProductSelected(selectedProduct)
            dismiss() // Đóng BottomSheet khi user chọn xong
        }
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}