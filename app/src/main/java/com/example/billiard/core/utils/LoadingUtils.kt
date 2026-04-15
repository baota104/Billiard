package com.example.billiard.core.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.billiard.R

object LoadingUtils {
    private var dialog: Dialog? = null

    /**
     * Hàm hiển thị Loading Dialog.
     * @param context Truyền Fragment/Activity context vào đây (ví dụ: requireContext() hoặc this)
     * @param cancelable Cho phép người dùng bấm ra ngoài để tắt Loading hay không (Mặc định là false để chặn thao tác khi đang gọi API)
     */
    fun show(context: Context, cancelable: Boolean = false) {
        // Tránh lỗi khi Activity/Context đã bị hủy nhưng vẫn cố show Dialog
        if (context is Activity && context.isFinishing) return

        // Ẩn dialog cũ nếu đang hiển thị để tránh bị chồng chéo
        hide()

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_loading)

            // Xóa background trắng mặc định của Dialog để nó trong suốt
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Chặn bấm ra ngoài hoặc bấm nút Back để tắt
            setCancelable(cancelable)

            show()
        }
    }

    /**
     * Hàm ẩn Loading Dialog
     */
    fun hide() {
        if (dialog?.isShowing == true) {
            try {
                dialog?.dismiss()
            } catch (e: Exception) {
                // Bắt lỗi crash nếu Context đã bị hủy trước khi kịp ẩn Dialog
                e.printStackTrace()
            }
        }
        dialog = null
    }
}