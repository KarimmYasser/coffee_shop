package com.example.cofee_shop.presentation.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cofee_shop.R
import com.example.cofee_shop.adapter.OrdersAdapter
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderFilter
import com.example.cofee_shop.databinding.FragmentOrderBinding
import com.example.cofee_shop.presentation.managers.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter
    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupTabButtons()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter(
            onOrderClick = { order ->
                // Navigate to order details if needed
                Toast.makeText(requireContext(), "Order details: ${order.orderId}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { order ->
                showDeleteConfirmation(order)
            },
            getOrderItems = { orderId ->
                orderViewModel.getOrderItemsById(orderId)
            }
        )

        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter
            addItemDecorationIfNeeded()
        }
    }

    private fun RecyclerView.addItemDecorationIfNeeded() {
        if (itemDecorationCount == 0) {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = resources.getDimensionPixelSize(R.dimen.margin_small)
                }
            })
        }
    }

    private fun setupTabButtons() {
        binding.tabLayout.btnRecently.setOnClickListener {
            orderViewModel.setFilter(OrderFilter.RECENTLY)
        }

        binding.tabLayout.btnPastOrders.setOnClickListener {
            orderViewModel.setFilter(OrderFilter.PAST_ORDERS)
        }
    }

    private fun setupClickListeners() {
        binding.errorStateSection.retryButton.setOnClickListener {
            orderViewModel.clearError()
            orderViewModel.refreshOrders()
        }

        binding.headerSection.notificationIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show()
        }

        binding.headerSection.menuIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Menu clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeCurrentFilter() }
                launch { observeFilteredOrders() }
                launch { observeLoadingState() }
                launch { observeErrorState() }
            }
        }
    }

    private suspend fun observeCurrentFilter() {
        orderViewModel.currentFilter.collect { filter ->
            updateTabButtons(filter)
        }
    }

    private suspend fun observeFilteredOrders() {
        orderViewModel.filteredOrders.collect { orders ->
            Log.d(TAG, "Orders updated: ${orders.size} items for filter ${orderViewModel.currentFilter.value}")
            updateOrdersList(orders)
            updateEmptyState(orders)
        }
    }

    private suspend fun observeLoadingState() {
        orderViewModel.isLoading.collect { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                hideErrorState()
                hideEmptyState()
            } else {
                hideLoadingDialog()
            }
        }
    }

    private suspend fun observeErrorState() {
        orderViewModel.errorMessage.collect { errorMessage ->
            if (errorMessage != null && !orderViewModel.isLoading.value) {
                showErrorState(errorMessage)
            } else {
                hideErrorState()
            }
        }
    }

    private fun updateTabButtons(currentFilter: OrderFilter) {
        when (currentFilter) {
            OrderFilter.RECENTLY -> {
                setTabButtonSelected(binding.tabLayout.btnRecently)
                setTabButtonUnselected(binding.tabLayout.btnPastOrders)
            }
            OrderFilter.PAST_ORDERS -> {
                setTabButtonSelected(binding.tabLayout.btnPastOrders)
                setTabButtonUnselected(binding.tabLayout.btnRecently)
            }
        }
    }

    private fun setTabButtonSelected(button: View) {
        button.setBackgroundResource(R.drawable.selected_tab_background)
        if (button is android.widget.TextView) {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.selected_tab_text))
        }
    }

    private fun setTabButtonUnselected(button: View) {
        button.setBackgroundResource(android.R.color.transparent)
        if (button is android.widget.TextView) {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.unselected_tab_text))
        }
    }

    private fun updateOrdersList(orders: List<OrderEntity>) {
        ordersAdapter.submitList(orders) {
            if (orders.isNotEmpty()) {
                binding.ordersRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun updateEmptyState(orders: List<OrderEntity>) {
        if (orders.isEmpty() && !orderViewModel.isLoading.value && orderViewModel.errorMessage.value == null) {
            showEmptyState()
            hideRecyclerView()
        } else {
            hideEmptyState()
            showRecyclerView()
        }
    }

    private fun showEmptyState() {
        binding.emptyStateSection.root.visibility = View.VISIBLE

        // Update empty state message based on current filter
        val currentFilter = orderViewModel.currentFilter.value
        val emptyMessage = when (currentFilter) {
            OrderFilter.RECENTLY -> getString(R.string.no_recent_orders_message)
            OrderFilter.PAST_ORDERS -> getString(R.string.no_past_orders_message)
        }
        binding.emptyStateSection.emptyStateMessage.text = emptyMessage
    }

    private fun hideEmptyState() {
        binding.emptyStateSection.root.visibility = View.GONE
    }

    private fun showRecyclerView() {
        binding.ordersRecyclerView.visibility = View.VISIBLE
    }

    private fun hideRecyclerView() {
        binding.ordersRecyclerView.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        binding.errorStateSection.root.visibility = View.VISIBLE
        binding.errorStateSection.errorTextView.text = errorMessage
        hideRecyclerView()
        hideEmptyState()
    }

    private fun hideErrorState() {
        binding.errorStateSection.root.visibility = View.GONE
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = AlertDialog.Builder(requireContext())
                .setView(R.layout.loading_dialog)
                .setCancelable(false)
                .create()
        }

        if (loadingDialog?.isShowing != true) {
            loadingDialog?.show()
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private fun showDeleteConfirmation(order: OrderEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_order_title))
            .setMessage(getString(R.string.delete_order_message, order.orderId))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteOrder(order)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun deleteOrder(order: OrderEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                orderViewModel.deleteOrder(order.orderId)
                showSuccessMessage(getString(R.string.order_deleted_successfully))
            } catch (e: Exception) {
                showErrorMessage(getString(R.string.error_deleting_order))
                Log.e(TAG, "Error deleting order", e)
            }
        }
    }

    private fun showSuccessMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                orderViewModel.refreshOrders()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingDialog?.dismiss()
        loadingDialog = null
        _binding = null
    }
}