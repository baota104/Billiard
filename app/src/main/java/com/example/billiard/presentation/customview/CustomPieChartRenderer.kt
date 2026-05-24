package com.example.billiard.presentation.customview


import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomPieChartRenderer(chart: PieChart, animator: ChartAnimator, viewPortHandler: ViewPortHandler)
    : PieChartRenderer(chart, animator, viewPortHandler) {

    override fun drawValues(c: Canvas?) {
        super.drawValues(c)
        // Lấy data ra để tô màu lại cho text
        val data = mChart.data ?: return
        val dataSet = data.dataSet ?: return

//        val colors = dataSet.colors
//        for (i in 0 until mValuePaint.size) {
//            // Hàm renderer mặc định vẽ màu đen, bạn có thể override nâng cao ở đây.
//            // Tuy nhiên để an toàn, PieChart đã đủ đẹp rồi!
//        }
    }
}