package com.example.cofee_shop.presentation.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.cofee_shop.R
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.databinding.FragmentPaymentBinding
import com.example.cofee_shop.presentation.managers.PaymentResult
import com.example.cofee_shop.presentation.managers.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val paymentViewModel: PaymentViewModel by viewModels()
    private var orderId: String? = null
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = PaymentFragmentArgs.fromBundle(it).orderId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderId?.let { id ->
            paymentViewModel.loadOrderDetails(id)
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnProcessPayment.setOnClickListener {
            processPayment()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeOrderDetails() }
                launch { observeOrderItems() }
                launch { observePaymentProcessing() }
                launch { observePaymentResult() }
                launch { observeErrorMessage() }
            }
        }
    }

    private suspend fun observeOrderDetails() {
        paymentViewModel.orderDetails.collect { order ->
            order?.let { updateOrderSummary(it) }
        }
    }

    private suspend fun observeOrderItems() {
        paymentViewModel.orderItems.collect { items ->
            updateOrderItems(items)
        }
    }

    private suspend fun observePaymentProcessing() {
        paymentViewModel.isProcessingPayment.collect { isProcessing ->
            if (isProcessing) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }

            binding.btnProcessPayment.isEnabled = !isProcessing
            binding.btnCancel.isEnabled = !isProcessing
        }
    }

    private suspend fun observePaymentResult() {
        paymentViewModel.paymentResult.collect { result ->
            when (result) {
                is PaymentResult.Success -> {
                    showSuccessDialog(result.message)
                }
                is PaymentResult.Failed -> {
                    showErrorDialog(result.message)
                }
                null -> { /* No action needed */ }
            }
        }
    }

    private suspend fun observeErrorMessage() {
        paymentViewModel.errorMessage.collect { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                paymentViewModel.clearError()
            }
        }
    }

    private fun updateOrderSummary(order: OrderEntity) {
        binding.apply {
            textOrderId.text = "Order #${order.orderId.takeLast(6)}"
            textSubtotal.text = "Rp ${String.format("%.0f", order.subtotal)}"
            textDeliveryFee.text = "Rp ${String.format("%.0f", order.deliveryFee)}"
            textPackagingFee.text = "Rp ${String.format("%.0f", order.packagingFee)}"
            textTotalAmount.text = "Rp ${String.format("%.0f", order.totalAmount)}"
        }
    }

    private fun updateOrderItems(items: List<OrderItemEntity>) {
        val itemsText = items.joinToString("\n") { item ->
            "${item.quantity}x ${item.coffeeName} - Rp ${String.format("%.0f", item.price * item.quantity)}"
        }
        binding.textOrderItems.text = itemsText
    }

    private fun processPayment() {
        val selectedPaymentMethod = getSelectedPaymentMethod()
        val order = paymentViewModel.orderDetails.value

        if (order != null) {
            paymentViewModel.processPayment(
                orderId = order.orderId,
                amount = order.totalAmount,
                paymentMethod = selectedPaymentMethod
            )
        }
    }

    private fun getSelectedPaymentMethod(): String {
        return when (binding.radioGroupPaymentMethod.checkedRadioButtonId) {
            R.id.radioCreditCard -> "credit_card"
            R.id.radioDebitCard -> "debit_card"
            R.id.radioDigitalWallet -> "digital_wallet"
            else -> "credit_card"
        }
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Payment Successful")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                paymentViewModel.clearPaymentResult()
                // Navigate back to orders or home
                findNavController().navigate(R.id.action_paymentFragment_to_orderFragment)
            }
            .setCancelable(false)
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Payment Failed")
            .setMessage(message)
            .setPositiveButton("Retry") { _, _ ->
                paymentViewModel.clearPaymentResult()
            }
            .setNegativeButton("Cancel") { _, _ ->
                paymentViewModel.clearPaymentResult()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.loading_dialog, null)

            loadingDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()

            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        if (loadingDialog?.isShowing != true) {
            loadingDialog?.show()
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideLoadingDialog()
        loadingDialog = null
        _binding = null
    }

    companion object {
        private const val TAG = "PaymentFragment"
    }
}