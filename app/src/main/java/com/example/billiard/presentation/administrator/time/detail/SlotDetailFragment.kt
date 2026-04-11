package com.example.billiard.presentation.administrator.time.detail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentSlotDetailBinding
import com.example.billiard.domain.model.CreatePriceListParam
import com.example.billiard.domain.model.UpdatePriceListParam
import com.example.billiard.presentation.adapter.TimeSlotDetailAdapter
import com.example.billiard.presentation.administrator.time.PriceListViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimeSlotDetailFragment : BaseFragment<FragmentSlotDetailBinding>(FragmentSlotDetailBinding::inflate) {

    private val viewModel: PriceListViewModel by viewModels()
    private lateinit var adapter: TimeSlotDetailAdapter

    // Lấy loại bàn từ Bundle của màn trước gửi sang (Mặc định là POOL nếu null)
    private val selectedTableType: String by lazy {
        arguments?.getString("TABLE_TYPE") ?: "POOL"
    }

    override fun setupViews() {
        binding.tvHeaderTitle.text = "Loại bàn: $selectedTableType"

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnAddTimeSlot.setOnClickListener {
            val bottomSheet = ManageTimeSlotBottomSheet(timeSlotToEdit = null) { name, start, end, price ->
                val param = CreatePriceListParam(
                    startTime = start, 
                    endTime = end,
                    unitPrice = price.toDouble(),
                    tableType = selectedTableType
                )
                viewModel.createPriceList(param)
            }
            bottomSheet.show(childFragmentManager, "AddTimeSlot")
        }

        adapter = TimeSlotDetailAdapter(
            onEditClick = { slot ->
                val bottomSheet = ManageTimeSlotBottomSheet(timeSlotToEdit = slot) { name, start, end, price ->
                    val param = UpdatePriceListParam(
                        id = slot.id,
                        startTime = start,
                        endTime = end,
                        unitPrice = price.toDouble(),
                        tableType = selectedTableType
                    )
                    viewModel.updatePriceList(param)
                }
                bottomSheet.show(childFragmentManager, "EditTimeSlot")
            },
            onDeleteClick = { slot ->
               // Dùng id để xóa, dùng timeRange (start - end) để hiển thị thông báo cho người dùng
               showConfirmCloseDialog(slot.id, "${slot.startTime.take(5)} - ${slot.endTime.take(5)}")
            }
        )

        binding.rvTimeSlots.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTimeSlots.adapter = adapter
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Lắng nghe danh sách PriceList trả về
                launch {
                    viewModel.priceListsState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                val allPriceLists = state.data
                                
                                // Lọc danh sách PriceList chỉ lấy những cái của TableType hiện tại (POOL/SNOOKER...)
                                val filteredList = allPriceLists.filter { 
                                    it.tableType.equals(selectedTableType, ignoreCase = true) 
                                }

                                adapter.submitList(filteredList)
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                            null -> {}
                        }
                    }
                }

                // 2. Lắng nghe trạng thái Thêm/Sửa/Xóa
                launch {
                    viewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Thao tác thành công", Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }

    private fun showConfirmCloseDialog(priceListId: Int, timeRange: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_close_table)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btnConfirmClose = dialog.findViewById<MaterialButton>(R.id.btnConfirmClose)
        btnConfirmClose.text = "Xóa ngay"
        
        val iconthongbao = dialog.findViewById<ImageView>(R.id.iconthongbao)
        iconthongbao.setImageResource(R.drawable.ic_priority)
        
        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        title.text = "Xác nhận xóa khung giờ"
        
        val desc = dialog.findViewById<TextView>(R.id.tvMessage)
        desc.text = "Bạn có chắc chắn muốn xóa khung giờ\n[$timeRange] không? Hành động này không\nthể hoàn tác."

        btnCancel.setOnClickListener {
            dialog.dismiss() 
        }

        btnConfirmClose.setOnClickListener {
            dialog.dismiss()
            viewModel.deletePriceList(priceListId)
        }

        dialog.show()
    }
}