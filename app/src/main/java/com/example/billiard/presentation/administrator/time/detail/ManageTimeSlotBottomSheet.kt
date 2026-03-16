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
import com.example.billiard.domain.model.TimeSlotDetailUiModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Locale

class ManageTimeSlotBottomSheet(
    private val timeSlotToEdit: TimeSlotDetailUiModel? = null,
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
            binding.edtSlotName.setText(timeSlotToEdit.title)

            // Giả lập cắt chuỗi timeRange "08:00 AM - 05:00 PM" thành 2 phần (Tùy logic dữ liệu thật của bạn)
            val times = timeSlotToEdit.timeRange.split(" - ")
            if (times.size == 2) {
                binding.edtStartTime.setText(times[0])
                binding.edtEndTime.setText(times[1])
            }

            binding.edtPrice.setText(timeSlotToEdit.price.toString())
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener { dismiss() }

        // Mở TimePicker khi bấm vào ô Start Time
        binding.edtStartTime.setOnClickListener {
            showTimePicker("Chọn giờ bắt đầu", binding.edtStartTime)
        }

        // Mở TimePicker khi bấm vào ô End Time
        binding.edtEndTime.setOnClickListener {
            showTimePicker("Chọn giờ kết thúc", binding.edtEndTime)
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtSlotName.text.toString().trim()
            val startTime = binding.edtStartTime.text.toString().trim()
            val endTime = binding.edtEndTime.text.toString().trim()
            val priceStr = binding.edtPrice.text.toString().trim()

            // Validate sơ bộ
            if (name.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toIntOrNull() ?: 0
            onSave(name, startTime, endTime, price)
            dismiss()
        }
    }

    // Hàm gọi đồng hồ xoay chuẩn Material Design
    private fun showTimePicker(title: String, targetEditText: EditText) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H) // Hiện 12h (AM/PM) như thiết kế
            .setHour(12)
            .setMinute(0)
            .setTitleText(title)
            .build()

        picker.addOnPositiveButtonClickListener {
            // Format giờ và phút (VD: 02:30 PM)
            val hour = picker.hour
            val minute = picker.minute

            val amPm = if (hour >= 12) "PM" else "AM"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12

            val timeString = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
            targetEditText.setText(timeString)
        }

        picker.show(childFragmentManager, "TimePicker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}