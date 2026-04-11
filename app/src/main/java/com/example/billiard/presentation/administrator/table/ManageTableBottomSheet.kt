package com.example.billiard.presentation.administrator.table

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.utils.FileUtils
import com.example.billiard.databinding.BottomSheetManageTableBinding
import com.example.billiard.domain.model.DashboardTable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class ManageTableBottomSheet(
    private val tableToEdit: DashboardTable? = null, 
    private val onSave: (name: String, type: String, isMaintain: Boolean, imageFile: File?) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageTableBinding? = null
    private val binding get() = _binding!!

    // Biến lưu trữ File ảnh sau khi người dùng chọn từ thư viện
    private var selectedImageFile: File? = null

    // Launcher mở thư viện hệ thống
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Hiển thị ảnh vừa chọn lên giao diện bằng Glide
            binding.layoutEmptyImage.hide()
            binding.layoutFilledImage.show()
            Glide.with(this).load(it).into(binding.imgPreview)

            // Convert Uri thành java.io.File để chuẩn bị gửi Retrofit
            selectedImageFile = FileUtils.uriToFile(requireContext(), it)
        }
    }

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
        _binding = BottomSheetManageTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        setupDropdown()
        setupUIByMode()
        setupClickListeners()
    }

    private fun setupDropdown() {
        val types = arrayOf("POOL", "SNOOKER", "CAROM")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupUIByMode() {
        if (tableToEdit == null) {
            binding.tvTitle.text = "Thêm bàn mới"
            binding.tvAsterisk.show()
            binding.btnClose.hide()
            binding.cardMaintenance.hide()
            binding.btnSave.text = "Lưu thông tin"
            binding.actCategory.setText("POOL", false)
            binding.layoutEmptyImage.show()
            binding.layoutFilledImage.hide()
        } else {
            binding.tvTitle.text = "Chỉnh sửa thông tin"
            binding.tvAsterisk.hide()
            binding.btnClose.show()
            binding.cardMaintenance.show()
            binding.btnSave.text = "Lưu thay đổi"

            binding.edtTableName.setText(tableToEdit.name)
            binding.actCategory.setText(tableToEdit.tableType.ifBlank { "POOL" }.uppercase(), false)

            binding.switchMaintenance.isChecked = tableToEdit.status.equals("MAINTAIN", true) || tableToEdit.status.equals("MAINTENANCE", true)

            // Hiển thị ảnh cũ của bàn nếu có
            if (tableToEdit.imageUrl.isNotEmpty()) {
                binding.layoutEmptyImage.hide()
                binding.layoutFilledImage.show()
                Glide.with(this)
                    .load(tableToEdit.imageUrl)
                    .error(R.drawable.img_ban)
                    .into(binding.imgPreview)
            } else {
                binding.layoutEmptyImage.show()
                binding.layoutFilledImage.hide()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { dismiss() }

        // Nhấn vào ô Add Image để gọi ActivityResultLauncher
        binding.layoutImageUpload.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        
        // SỬA LỖI BẤM XÓA ẢNH BỊ MẤT ITEM: Thay vì đính sự kiện vào layoutFilledImage, ta chỉ gán vào vùng an toàn hoặc không đụng chạm đến toàn bộ layout
        binding.imgPreview.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Nút xóa ảnh nằm riêng rẽ, không bao bọc toàn bộ layout
        binding.btnRemoveImage.setOnClickListener {
            selectedImageFile = null
            
            // Xử lý lại UI: Ẩn ảnh đã chọn, hiện lại khung "Nhấn để tải lên"
            binding.layoutFilledImage.hide()
            binding.layoutEmptyImage.show()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtTableName.text.toString().trim()
            val type = binding.actCategory.text.toString()
            val isMaintain = binding.switchMaintenance.isChecked

            if (name.isEmpty()) {
                binding.edtTableName.error = "Vui lòng nhập tên bàn"
                return@setOnClickListener
            }
            
            // Gọi callback truyền kèm file ảnh vừa chọn (nếu user ấn nút xóa ảnh thì selectedImageFile sẽ là null)
            onSave(name, type, isMaintain, selectedImageFile)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}