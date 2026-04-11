package com.example.billiard.presentation.administrator.time.detail

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetManageTimeSlotBinding
import com.example.billiard.domain.model.PriceList
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Locale

class ManageTimeSlotBottomSheet(
    private val timeSlotToEdit: PriceList? = null,
    private val onSave: (name: String, startTime: String, endTime: String, price: Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageTimeSlotBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetManageTimeSlotBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUIByMode()
        setupClickListeners()
    }

    private fun setupUIByMode() {
        if (timeSlotToEdit == null) {
            binding.tvTitle.text = "Thêm Khung giờ"
            binding.edtSlotName.setText("")
            binding.edtStartTime.setText("")
            binding.edtEndTime.setText("")
            binding.edtPrice.setText("")
        } else {
            binding.tvTitle.text = "Sửa Khung giờ"
            // Backend không lưu tên "Ca Sáng/Trưa/Tối" mà chỉ lưu giờ
            binding.edtSlotName.setText("Khung giờ ${timeSlotToEdit.tableType}")

            // Truyền trực tiếp giờ lấy từ Backend "08:00:00" -> "08:00"
            binding.edtStartTime.setText(timeSlotToEdit.startTime.take(5))
            binding.edtEndTime.setText(timeSlotToEdit.endTime.take(5))

            binding.edtPrice.setText(timeSlotToEdit.unitPrice.toInt().toString())
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.edtStartTime.setOnClickListener {
            showTimePicker("Chọn giờ bắt đầu", binding.edtStartTime)
        }

        binding.edtEndTime.setOnClickListener {
            showTimePicker("Chọn giờ kết thúc", binding.edtEndTime)
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtSlotName.text.toString().trim()
            val startTime = binding.edtStartTime.text.toString().trim()
            val endTime = binding.edtEndTime.text.toString().trim()
            val priceStr = binding.edtPrice.text.toString().trim()

            if (startTime.isEmpty() || endTime.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập giờ và giá tiền", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Backend yêu cầu định dạng HH:mm:ss, nên nếu người dùng gõ "08:00" ta phải nối thêm ":00"
            val formattedStartTime = if (startTime.length <= 5) "$startTime:00" else startTime
            val formattedEndTime = if (endTime.length <= 5) "$endTime:00" else endTime

            val price = priceStr.toIntOrNull() ?: 0
            onSave(name, formattedStartTime, formattedEndTime, price)
            dismiss()
        }
    }

    private fun showTimePicker(title: String, targetEditText: EditText) {
        val picker = MaterialTimePicker.Builder()
            // Chuyển sang format 24H để dễ định dạng chuẩn backend (HH:mm:ss)
            .setTimeFormat(TimeFormat.CLOCK_24H) 
            .setHour(12)
            .setMinute(0)
            .setTitleText(title)
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute

            // Format thành chuẩn 24h: "14:30"
            val timeString = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            targetEditText.setText(timeString)
        }

        picker.show(childFragmentManager, "TimePicker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}