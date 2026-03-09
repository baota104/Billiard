package com.example.billiard.core.ext

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

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