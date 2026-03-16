package com.example.billiard.presentation.administrator.time.detail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentSlotDetailBinding
import com.example.billiard.domain.model.SlotThemeType
import com.example.billiard.domain.model.TimeSlotDetailUiModel
import com.example.billiard.presentation.adapter.TimeSlotDetailAdapter
import com.google.android.material.button.MaterialButton

class TimeSlotDetailFragment : BaseFragment<FragmentSlotDetailBinding>(FragmentSlotDetailBinding::inflate) {

    private lateinit var adapter: TimeSlotDetailAdapter

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnAddTimeSlot.setOnClickListener {
            val bottomSheet = ManageTimeSlotBottomSheet(timeSlotToEdit = null) { name, start, end, price ->
                Toast.makeText(requireContext(), "Đã thêm: $name", Toast.LENGTH_SHORT).show()
            }
            bottomSheet.show(childFragmentManager, "AddTimeSlot")
        }

        adapter = TimeSlotDetailAdapter(
            onEditClick = { slot ->
                val bottomSheet = ManageTimeSlotBottomSheet(timeSlotToEdit = slot) { name, start, end, price ->
                    Toast.makeText(requireContext(), "Đã sửa thành: $name", Toast.LENGTH_SHORT).show()
                }
                bottomSheet.show(childFragmentManager, "EditTimeSlot")
            },
            onDeleteClick = { slot ->
               showConfirmCloseDialog(slot.title)
            }
        )

        binding.rvTimeSlots.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTimeSlots.adapter = adapter

        createMockData()
    }

    private fun createMockData() {
        val mockData = listOf(
            TimeSlotDetailUiModel("1", "Sáng", "Khung giờ phổ thông", "08:00 - 17:00", 60000, SlotThemeType.MORNING),
            TimeSlotDetailUiModel("2", "Chiều", "Giờ cao điểm", "17:00 - 22:00", 80000, SlotThemeType.AFTERNOON),
            TimeSlotDetailUiModel("3", "Đêm", "Giờ khuya", "22:00 - 08:00", 50000, SlotThemeType.NIGHT),
            TimeSlotDetailUiModel("4", "Cuối tuần", "Giá đặc biệt", "T7 - CN (Cả ngày)", 90000, SlotThemeType.WEEKEND)
        )
        adapter.submitList(mockData)
    }
    private fun showConfirmCloseDialog(name:String) {
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
        btnConfirmClose.setText("Xác ngay")
        val iconthongbao = dialog.findViewById<ImageView>(R.id.iconthongbao)
        iconthongbao.setImageResource(R.drawable.ic_priority)
        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        title.setText("Xác nhận xóa ")
        val desc = dialog.findViewById<TextView>(R.id.tvMessage)

        desc.setText("Bạn có chắc chắn muốn xóa khung giở \"$name\"\n" +
                "khỏi hệ thống không? Hành động này không\n" +
                "thể hoàn tác.")

        btnCancel.setOnClickListener {
            dialog.dismiss() // Chỉ tắt hộp thoại
        }

        btnConfirmClose.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Đã đóng bàn thành công!", Toast.LENGTH_SHORT).show()
            // findNavController().popBackStack()
        }

        dialog.show()
    }

    override fun observeData() {}
}