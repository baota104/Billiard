package com.example.billiard.core.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    /**
     * Chuyển đổi Uri (từ thư viện ảnh) thành java.io.File thực tế
     * bằng cách copy luồng dữ liệu (InputStream) vào thư mục cache của ứng dụng.
     * Điều này bắt buộc để Retrofit có thể gửi file dưới dạng Multipart.
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            // Tạo một file tạm trong thư mục cache của app
            val tempFile = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)
            
            inputStream?.copyTo(outputStream)
            
            inputStream?.close()
            outputStream.close()
            
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}