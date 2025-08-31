package com.example.cofee_shop.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cofee_shop.R
import com.example.cofee_shop.adapter.OrdersAdapter
import com.example.cofee_shop.presentation.managers.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class OrderFragment : Fragment() {

    private val viewModel: OrderViewModel by viewModels()

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var btnPlaceOrder: Button
    private lateinit var emptyStateSection: View
    private lateinit var errorStateSection: View

    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder)
        emptyStateSection = view.findViewById(R.id.emptyStateSection)
        errorStateSection = view.findViewById(R.id.errorStateSection)

        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter()
        ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.orders.collectLatest { orders ->
                ordersAdapter.submitList(orders)
                emptyStateSection.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
                ordersRecyclerView.visibility = if (orders.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { error ->
                errorStateSection.visibility = if (error != null) View.VISIBLE else View.GONE
                error?.let {
                    Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->

            }
        }
    }

    private fun setupListeners() {
        btnPlaceOrder.setOnClickListener {
            // Handle place order click
            Snackbar.make(requireView(), "Place Order clicked", Snackbar.LENGTH_SHORT).show()
        }
    }
}