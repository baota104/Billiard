import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.ext.showToast
import com.example.billiard.databinding.FragmentCreateReceiptBinding
import com.example.billiard.domain.model.ReceiptItemUiModel
import com.example.billiard.presentation.adapter.ReceiptItemAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateReceiptFragment : BaseFragment<FragmentCreateReceiptBinding>(FragmentCreateReceiptBinding::inflate) {

    private lateinit var receiptAdapter: ReceiptItemAdapter
    private var currentReceiptItems = mutableListOf<ReceiptItemUiModel>()

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Hiển thị ngày giờ hiện tại
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.tvImportDate.text = sdf.format(Date())

        setupRecyclerView()
        setupActionButtons()

        // Đổ dữ liệu giả lập theo thiết kế
        fillMockData()
    }

    private fun setupRecyclerView() {
        receiptAdapter = ReceiptItemAdapter { itemToDelete ->
            showConfirmDialog(
                title = "Xóa mặt hàng",
                message = "Bạn muốn xóa ${itemToDelete.name} khỏi phiếu nhập?",
                confirmButtonText = "Xóa"
            ) {
                currentReceiptItems.remove(itemToDelete)
                updateListAndTotal()
            }
        }
        binding.rvReceiptItems.adapter = receiptAdapter
    }

    private fun setupActionButtons() {
        binding.btnAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_createReceiptFragment_to_addExistingProductFragment)
        }

        binding.btnSaveReceipt.setOnClickListener {
            if (currentReceiptItems.isEmpty()) {
                showToast("Phiếu nhập đang trống!")
                return@setOnClickListener
            }
            showToast("Đã lưu phiếu nhập thành công!")
            findNavController().popBackStack()
        }
    }

    private fun updateListAndTotal() {
        // Cập nhật lại RecyclerView
        receiptAdapter.submitList(currentReceiptItems.toList())

        // Tính lại tổng tiền
        val total = currentReceiptItems.sumOf { it.totalPrice }
        binding.tvTotalReceiptPrice.text = "%,dđ".format(total).replace(',', '.')
    }

    private fun fillMockData() {
        currentReceiptItems = mutableListOf(
            ReceiptItemUiModel("1", "Coca Cola (Lon)", 24, 8500),
            ReceiptItemUiModel("2", "Khăn lạnh Bi-a", 100, 1200)
        )
        updateListAndTotal()
    }

    override fun observeData() {}
}