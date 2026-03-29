package com.example.billiard.presentation.customview // Đổi lại theo package của bạn nhé

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.example.billiard.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

class AdminDashboardCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val icon: ImageView
    private val title: TextView
    private val subtitle: TextView
    private val btnDetails: MaterialButton
    private val progressGroup: Group
    private val progressLabel: TextView
    private val progressPercent: TextView
    private val progressBar: LinearProgressIndicator

    init {
        radius = 16f * resources.displayMetrics.density
        setCardBackgroundColor(android.graphics.Color.WHITE)
        cardElevation = 0f

        LayoutInflater.from(context).inflate(R.layout.layout_admin_card, this, true)
        icon = findViewById(R.id.cardIcon)
        title = findViewById(R.id.cardTitle)
        subtitle = findViewById(R.id.cardSubtitle)
        btnDetails = findViewById(R.id.btnDetails)
        progressGroup = findViewById(R.id.progressGroup)
        progressLabel = findViewById(R.id.tvProgressLabel)
        progressPercent = findViewById(R.id.tvProgressPercent)
        progressBar = findViewById(R.id.progressIndicator)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AdminDashboardCard, 0, 0)

            title.text = typedArray.getString(R.styleable.AdminDashboardCard_adc_title)
            subtitle.text = typedArray.getString(R.styleable.AdminDashboardCard_adc_subtitle)
            progressLabel.text = typedArray.getString(R.styleable.AdminDashboardCard_adc_progressLabel)

            val iconRes = typedArray.getResourceId(R.styleable.AdminDashboardCard_adc_icon, 0)
            if (iconRes != 0) icon.setImageResource(iconRes)

            val iconTint = typedArray.getColor(R.styleable.AdminDashboardCard_adc_iconTint, 0)
            if (iconTint != 0) icon.imageTintList = ColorStateList.valueOf(iconTint)

            val showProgress = typedArray.getBoolean(R.styleable.AdminDashboardCard_adc_showProgress, false)
            progressGroup.visibility = if (showProgress) View.VISIBLE else View.GONE

            val progressVal = typedArray.getInt(R.styleable.AdminDashboardCard_adc_progressValue, 0)
            setProgress(progressVal)

            typedArray.recycle()
        }
    }

    fun setProgress(value: Int) {
        progressBar.setProgressCompat(value, true)
        progressPercent.text = "$value%"
    }

    fun setSubtitle(text: String) {
        subtitle.text = text
    }

    fun setOnDetailsClickListener(listener: (View) -> Unit) {
        btnDetails.setOnClickListener { listener(it) }
    }
}