package com.example.billiard.core.ext

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.billiard.R
import com.google.android.material.button.MaterialButton

// Hiện một View
fun View.show() {
    visibility = View.VISIBLE
}

// Ẩn một View (Mất hẳn không gian)
fun View.hide() {
    visibility = View.GONE
}

// Tàng hình một View (Vẫn chiếm không gian)
fun View.invisible() {
    visibility = View.INVISIBLE
}

// Hiển thị thông báo Toast nhanh trong Fragment
fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
fun Fragment.showConfirmDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Xác nhận", // Giá trị mặc định
    cancelButtonText: String = "Hủy",       // Giá trị mặc định
    iconResId: Int = R.drawable.ic_priority, // Icon mặc định
    onConfirm: () -> Unit // Khối lệnh sẽ chạy khi bấm Xác nhận
) {
    val dialog = Dialog(requireContext())
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_confirm_close_table) // Nhớ giữ nguyên tên layout này hoặc đổi tên file XML cho tổng quát hơn
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    // Set độ rộng 85% màn hình
    val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
    dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

    // Ánh xạ View
    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
    val iconImage = dialog.findViewById<ImageView>(R.id.iconthongbao)
    val btnConfirm = dialog.findViewById<MaterialButton>(R.id.btnConfirmClose)
    val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)

    // Gắn dữ liệu động
    tvTitle.text = title
    tvMessage.text = message
    btnConfirm.text = confirmButtonText
    btnCancel.text = cancelButtonText
    iconImage.setImageResource(iconResId)

    // Sự kiện nút
    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    btnConfirm.setOnClickListener {
        dialog.dismiss()
        onConfirm.invoke() // Chạy khối lệnh được truyền vào từ bên ngoài
    }

    dialog.show()
}