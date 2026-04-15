package com.example.billiard.presentation.home.openingtable

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentBanDetailBinding
import com.example.billiard.domain.model.Invoice
import com.example.billiard.presentation.adapter.OrderDetailAdapter
import com.example.billiard.presentation.detail.BanDetailViewModel
import com.example.billiard.presentation.home.transfertable.TransferTableBottomSheet
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BanDetailFragment : BaseFragment<FragmentBanDetailBinding>(FragmentBanDetailBinding::inflate) {
    private lateinit var orderServiceAdapter: OrderDetailAdapter
    private val viewModel: BanDetailViewModel by viewModels()
    private var invoiceId: Int = -1
    private var timerJob: Job? = null
    private lateinit var invoicedemo: Invoice

    override fun onResume() {
        super.onResume()
        // Mỗi khi màn hình này hiện lên (kể cả từ màn hình Gọi món quay về)
        // -> Tự động gọi API lấy dữ liệu mới nhất
        if (invoiceId != 0 && invoiceId != -1) {
            Log.d("BanDetail_BUG", "Đang tự động Refresh lại dữ liệu của Invoice: $invoiceId")
            viewModel.loadInvoiceDetail(invoiceId)
        }
    }

    override fun setupViews() {
        invoiceId = arguments?.getInt("INVOICE_ID") ?: 0
        setUpUI()
        setUpRecycle()
//        setupSwipeToDelete() // Đã thêm gọi hàm kích hoạt vuốt xóa
    }

    private fun setUpUI() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnThanhToan.setOnClickListener {
            val bundle = Bundle().apply { putInt("INVOICE_ID", invoiceId) }
            findNavController().navigate(R.id.action_banDetailFragment_to_paymentFragment, bundle)
        }

        binding.btnThemDichVu.setOnClickListener {
            val bundle = Bundle().apply { putInt("INVOICE_ID", invoiceId) }
            findNavController().navigate(R.id.action_banDetailFragment_to_orderServiceFragment, bundle)
        }
    }

    private fun setUpRecycle() {
        orderServiceAdapter = OrderDetailAdapter()
        binding.rvOrderItems.apply {
            adapter = orderServiceAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

//    private fun setupSwipeToDelete() {
//        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
//            0, ItemTouchHelper.LEFT // Chỉ cho phép vuốt sang trái
//        ) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
//
//            // VẼ NỀN ĐỎ VÀ ICON THÙNG RÁC TRẮNG KHI ĐANG VUỐT
//            override fun onChildDraw(
//                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
//                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
//            ) {
//                val itemView = viewHolder.itemView
//                val paint = Paint().apply { color = Color.parseColor("#EF4444") } // Màu đỏ
//
//                if (dX < 0) { // Đang vuốt sang trái
//                    // Vẽ hình chữ nhật đỏ làm nền
//                    c.drawRect(
//                        itemView.right.toFloat() + dX, itemView.top.toFloat(),
//                        itemView.right.toFloat(), itemView.bottom.toFloat(), paint
//                    )
//
//                    // Lấy icon thùng rác mặc định của Android và tô màu trắng
//                    val icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)
//                    icon?.let {
//                        it.setTint(Color.WHITE)
//                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
//                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
//                        val iconBottom = iconTop + it.intrinsicHeight
//                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
//                        val iconRight = itemView.right - iconMargin
//                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//                        it.draw(c)
//                    }
//                }
//
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//            }
//
//            // KHI VUỐT XONG SẼ HIỆN DIALOG XÁC NHẬN
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.adapterPosition
//                if (position == RecyclerView.NO_POSITION) return
//                val deletedItem = orderServiceAdapter.getOrderDetailAt(position)
//
//                // Khôi phục lại UI của item (để nó không bị mất luôn nếu user bấm Hủy)
//                orderServiceAdapter.notifyItemChanged(position)
//
//                // Hiện Dialog Xác Nhận
//                AlertDialog.Builder(requireContext())
//                    .setTitle("Xác nhận xóa")
//                    .setMessage("Bạn có chắc muốn xóa [${deletedItem.productName}] khỏi bàn này không?")
//                    .setPositiveButton("Xóa") { _, _ ->
//                        viewModel.deleteOrderDetail(deletedItem.id)
//                    }
//                    .setNegativeButton("Hủy", null)
//                    .show()
//            }
//        }
//
//        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvOrderItems)
//    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Luồng 2: Lắng nghe trạng thái lấy chi tiết hóa đơn (Đã bọc trong khối launch riêng)
                launch {
                    viewModel.invoiceState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                LoadingUtils.show(requireContext())
                                Log.d("BanDetail_BUG", "2. API đang Loading...")
                            }
                            is Resource.Success -> {
                                LoadingUtils.hide()
                                val invoice = state.data
                                invoicedemo = invoice
                                Log.d("BanDetail_BUG", "3. API Success! Lấy được hóa đơn ID: ${invoice.id}")
                                Log.d("BanDetail_BUG", "   - Thời gian bắt đầu: ${invoice.startTime}")
                                binding.tvTitle.text = "Chi tiết Bàn ${invoice.billiardTableName}"

                                // 1. Cập nhật danh sách món ăn
                                val listOrder = invoice.orderDetails ?: emptyList()
                                orderServiceAdapter.submitList(listOrder)
                                Log.d("BanDetail_BUG", "   - Danh sách món ăn: $listOrder")
                                binding.tvItemCount.text = "${listOrder.size} món"

                                // 2. Tính tiền (tạm thời tự tính, nếu backend cập nhật thì lấy từ invoice)
                                val tamTinh = listOrder.sumOf { (it.price * it.quantity).toInt() }
                                val tienGio = 110000 // Tạm fix cứng

                                binding.valTamTinh.text = formatCurrency(tamTinh)

                                // 3. Kích hoạt đếm giờ
                                if (!invoice.startTime.isNullOrEmpty()) {
                                    Log.d("BanDetail_BUG", "4. Chuẩn bị gọi hàm startTimer()")
                                    startTimer(invoice.startTime)
                                } else {
                                    Log.e("BanDetail_BUG", "LỖI: invoice.startTime đang bị null hoặc rỗng!")
                                }
                            }
                            is Resource.Error -> {
                                Log.e("BanDetail_BUG", "LỖI API: ${state.message}")
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startTimer(startTimeString: String) {
        timerJob?.cancel()
        Log.d("BanDetail_BUG", "5. Bắt đầu xử lý timer với chuỗi: $startTimeString")

        timerJob = viewLifecycleOwner.lifecycleScope.launch {
            val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            val startTimeDate = try {
                serverFormat.parse(startTimeString)
            } catch (e: Exception) {
                Log.e("BanDetail_BUG", "6. CRASH! Không thể Parse chuỗi thời gian: ${e.message}")
                null
            }

            if (startTimeDate == null) {
                Log.e("BanDetail_BUG", "7. Hủy bỏ đếm giờ do Parse thất bại.")
                return@launch
            }

            Log.d("BanDetail_BUG", "8. Parse thành công! Thời gian chuẩn: $startTimeDate")
            val startTimeMillis = startTimeDate.time

            val displayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.tvTimeInfo.text = "Bắt đầu: ${displayFormat.format(startTimeDate)}"

            while (isActive) {
                val currentTimeMillis = System.currentTimeMillis()
                var diff = currentTimeMillis - startTimeMillis

                if (diff < 0) diff = 0

                val days = TimeUnit.MILLISECONDS.toDays(diff)
                diff -= TimeUnit.DAYS.toMillis(days)

                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                diff -= TimeUnit.HOURS.toMillis(hours)

                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                diff -= TimeUnit.MINUTES.toMillis(minutes)

                val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)

                binding.tvday.text = days.toString()
                binding.tvhour.text = String.format("%02d", hours)
                binding.tvminute.text = String.format("%02d", minutes)
                binding.tvsecond.text = String.format("%02d", seconds)

                delay(1000)
            }
        }
    }

    private fun formatCurrency(amount: Int): String {
        return "%,dđ".format(amount).replace(',', '.')
    }

    private fun showWarningDialog() { /* ... */ }
    private fun showConfirmCloseDialog() { /* ... */ }
}