package com.example.cofee_shop.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cofee_shop.R
import com.example.cofee_shop.adapter.CoffeeAdapter
import com.example.cofee_shop.databinding.FragmentHomeBinding
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.presentation.managers.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var coffeeAdapter: CoffeeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        coffeeAdapter = CoffeeAdapter(
            { coffee ->
                onCoffeeItemClicked(coffee);
            },
            true
        )

        binding.rvRecommendations.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = coffeeAdapter
        }
    }

    private fun setupClickListeners() {
        binding.headerSection.notificationIcon.setOnClickListener {
            handleNotificationClick()
        }

        binding.headerSection.menuIcon.setOnClickListener {
            handleMenuClick()
        }

        binding.cvBestSeller.setOnClickListener {
            handleBestSellerClick()
        }

        binding.cvNewMenu.setOnClickListener {
            handleNewMenuClick()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeCoffeeList()
                observeLoadingState()
                observeErrorState()
            }
        }
    }

    private suspend fun observeCoffeeList() {
        homeViewModel.coffeeList.collect { coffeeList ->
            coffeeAdapter.submitList(coffeeList)
        }
    }

    private suspend fun observeLoadingState() {
        homeViewModel.isLoading.collect { isLoading ->
            // Handle loading state - you can add loading indicators here if needed
        }
    }

    private suspend fun observeErrorState() {
        homeViewModel.errorMessage.collect { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onCoffeeItemClicked(coffee: Coffee) {
        val message = getString(R.string.added_to_cart, coffee.title)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun handleNotificationClick() {
        Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show()
    }

    private fun handleMenuClick() {
        Toast.makeText(requireContext(), "Menu clicked", Toast.LENGTH_SHORT).show()
    }

    private fun handleBestSellerClick() {
        Toast.makeText(requireContext(), "Best seller clicked", Toast.LENGTH_SHORT).show()
    }

    private fun handleNewMenuClick() {
        Toast.makeText(requireContext(), "New menu clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
