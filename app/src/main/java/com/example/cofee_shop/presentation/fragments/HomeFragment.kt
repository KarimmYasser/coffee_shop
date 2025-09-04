package com.example.cofee_shop.presentation.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
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

    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isNetworkAvailable = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupNetworkMonitoring()
        setupUI()
        observeViewModel()
    }

    private fun setupNetworkMonitoring() {
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        isNetworkAvailable = isNetworkConnected()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (!isNetworkAvailable) {
                    isNetworkAvailable = true
                    activity?.runOnUiThread {
                        homeViewModel.retryLoadingData()
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isNetworkAvailable = false
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
    }

    private fun isNetworkConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun setupUI() {
        setupRecyclerView()
        setupClickListeners()
        setupErrorUI()
    }

    private fun setupRecyclerView() {
        coffeeAdapter = CoffeeAdapter(
            onAddClick = { coffee ->
                onCoffeeAddClicked(coffee)
            },
            onItemClick = { coffee ->
                navigateToCoffeeDetail(coffee)
            },
            isHome = true
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

    private fun setupErrorUI() {
        binding.errorLayout?.btnRetry?.setOnClickListener {
            homeViewModel.retryLoadingData()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeCoffeeList() }
                launch { observeLoadingState() }
                launch { observeErrorState() }
                launch { observeUserName() }
            }
        }
    }

    private suspend fun observeCoffeeList() {
        homeViewModel.coffeeList.collect { coffeeList ->
            if (coffeeList.isNotEmpty()) {
                showContent()
                coffeeAdapter.submitList(coffeeList)
            }
        }
    }

    private suspend fun observeLoadingState() {
        homeViewModel.isLoading.collect { isLoading ->
            if (isLoading) {
                showLoading()
            }
        }
    }

    private suspend fun observeErrorState() {
        homeViewModel.errorState.collect { errorState ->
            when (errorState) {
                is HomeViewModel.ErrorState.None -> {
                    showContent()
                }
                is HomeViewModel.ErrorState.NetworkError -> {
                    if (homeViewModel.coffeeList.value.isEmpty()) {
                        showNetworkError()
                    } else {
                        showNetworkErrorToast()
                    }
                }
                is HomeViewModel.ErrorState.GenericError -> {
                    if (homeViewModel.coffeeList.value.isEmpty()) {
                        showGenericError(errorState.message)
                    } else {
                        Toast.makeText(requireContext(), errorState.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private suspend fun observeUserName() {
        homeViewModel.userName.collect { userName ->
            updateGreetingMessage(userName)
        }
    }

    private fun showContent() {
        binding.mainContent.visibility = View.VISIBLE
        binding.loadingLayout?.root?.visibility = View.GONE
        binding.errorLayout?.root?.visibility = View.GONE
    }

    private fun showLoading() {
        if (homeViewModel.coffeeList.value.isEmpty()) {
            binding.mainContent.visibility = View.GONE
            binding.errorLayout?.root?.visibility = View.GONE
            binding.loadingLayout?.root?.visibility = View.VISIBLE
        }
    }

    private fun showNetworkError() {
        binding.mainContent.visibility = View.GONE
        binding.loadingLayout?.root?.visibility = View.GONE
        binding.errorLayout?.root?.visibility = View.VISIBLE

        binding.errorLayout?.apply {
            ivErrorIcon.setImageResource(R.drawable.ic_no_wifi)
            tvErrorTitle.text = getString(R.string.no_internet_connection)
            tvErrorMessage.text = getString(R.string.no_internet_message)
            btnRetry.text = getString(R.string.retry)
            btnRetry.visibility = View.VISIBLE
        }
    }

    private fun showGenericError(message: String) {
        binding.mainContent.visibility = View.GONE
        binding.loadingLayout?.root?.visibility = View.GONE
        binding.errorLayout?.root?.visibility = View.VISIBLE

        binding.errorLayout?.apply {
            ivErrorIcon.setImageResource(R.drawable.ic_error)
            tvErrorTitle.text = getString(R.string.something_went_wrong)
            tvErrorMessage.text = message
            btnRetry.text = getString(R.string.try_again)
            btnRetry.visibility = View.VISIBLE
        }
    }

    private fun showNetworkErrorToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.network_error_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateGreetingMessage(userName: String?) {
        val greetingText = if (userName != null) {
            "Good morning, $userName"
        } else {
            "Good morning"
        }
        binding.headerSection.greetingLine2TextView.text = greetingText
    }

    private fun onCoffeeAddClicked(coffee: Coffee) {
        val message = getString(R.string.added_to_cart, coffee.title)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCoffeeDetail(coffee: Coffee) {
        val action = HomeFragmentDirections.actionHomeFragmentToCoffeeDetailFragment(coffee)
        findNavController().navigate(action)
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
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}