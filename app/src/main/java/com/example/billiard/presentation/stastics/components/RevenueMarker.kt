package com.example.billiard.presentation.stastics.components

import android.content.Context
import android.widget.TextView
import com.example.billiard.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

// Custome marker cho biểu đồ
class RevenueMarker(
    context: Context,
    layoutResource: Int
) : MarkerView(context, layoutResource) {
    private val text = findViewById<TextView>(R.id.markerText)

    //    Hiển thị marker mỗi khi click vào 1 Entry
    override fun refreshContent(e: Entry?, highlight: Highlight?) {

//        Giống e == Null ? 0f : e.y (Elvis operator)
        val value = e?.y ?: 0f
        text.text = "Doanh thu: %,.0f".format(value)

        super.refreshContent(e, highlight)
    }

    //    Dịch marker sang trái và lên trên so với điểm được click
    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }

    //    Điều chỉnh vị trí marker để không bị tràn viền
    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {

//        Mặc định
        var offsetX = -(width / 2f)
        var offsetY = -height.toFloat()

        val chart = chartView

        chart?.let {

//          Nếu tràn trái, reset về sát mép trái màn hình
            if (posX + offsetX < 0) {
                offsetX = -posX
            }

//            Tương tự với tràn phải
            if (posX + width + offsetX > it.width) {
                offsetX = it.width - posX - width
            }

//            Tương tụ với tràn trên
            if (posY + offsetY < 0) {
                offsetY = 0f
            }
        }

        return MPPointF(offsetX, offsetY)
    }
}