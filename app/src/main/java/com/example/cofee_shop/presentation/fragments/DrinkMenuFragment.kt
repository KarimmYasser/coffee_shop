package com.example.cofee_shop.presentation.fragments

import android.app.AlertDialog
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cofee_shop.R
import com.example.cofee_shop.adapter.CoffeeAdapter
import com.example.cofee_shop.databinding.FragmentDrinkMenuBinding
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.presentation.managers.CoffeeCategory
import com.example.cofee_shop.presentation.managers.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DrinkMenuFragment : Fragment() {

    private var _binding: FragmentDrinkMenuBinding? = null
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var coffeeAdapter: CoffeeAdapter
    private var loadingDialog: AlertDialog? = null
    private var searchTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrinkMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupSearch()
        setupCategoryButtons()
        setupClickListeners()
    }

    private fun onCoffeeAddClicked(coffee: Coffee) {
        val message = getString(R.string.added_to_cart, coffee.title)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCoffeeDetail(coffee: Coffee) {
        val action = DrinkMenuFragmentDirections.actionDrinkMenuFragmentToCoffeeDetailFragment(coffee)
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        coffeeAdapter = CoffeeAdapter(
            onAddClick = { coffee ->
                onCoffeeAddClicked(coffee)
            },
            onItemClick = { coffee ->
                navigateToCoffeeDetail(coffee)
            },
            isHome = false // Set to false for menu fragment
        )

        binding.coffeeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = coffeeAdapter
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

    private fun setupSearch() {
        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                menuViewModel.searchCoffee(query)
            }
        }

        binding.searchSection.searchEditText.addTextChangedListener(searchTextWatcher)
    }

    private fun setupCategoryButtons() {
        binding.categoryTabs.coldButton.setOnClickListener {
            menuViewModel.setCategory(CoffeeCategory.ICED)
        }

        binding.categoryTabs.hotButton.setOnClickListener {
            menuViewModel.setCategory(CoffeeCategory.HOT)
        }
    }

    private fun setupClickListeners() {
        binding.emptyStateSection.clearSearchButton.setOnClickListener {
            menuViewModel.clearSearch()
        }

        binding.errorStateSection.retryButton.setOnClickListener {
            menuViewModel.clearError()
            menuViewModel.refreshCurrentCategory()
        }

        binding.headerSection.notificationIcon.setOnClickListener {
            handleNotificationClick()
        }

        binding.headerSection.menuIcon.setOnClickListener {
            handleMenuClick()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeCurrentCategory() }
                launch { observeSearchQuery() }
                launch { observeFilteredCoffeeList() }
                launch { observeLoadingState() }
                launch { observeErrorState() }
            }
        }
    }

    private suspend fun observeCurrentCategory() {
        menuViewModel.currentCategory.collect { category ->
            updateButtonStates(category)
            clearSearchFieldSafely()
        }
    }

    private suspend fun observeSearchQuery() {
        menuViewModel.searchQuery.collect { query ->
            updateSearchFieldIfNeeded(query)
        }
    }

    private suspend fun observeFilteredCoffeeList() {
        menuViewModel.filteredCoffeeList.collect { coffeeList ->
            Log.d(TAG, "Coffee list updated: ${coffeeList.size} items")
            updateCoffeeList(coffeeList)
            updateEmptyState(coffeeList)
        }
    }

    private suspend fun observeLoadingState() {
        menuViewModel.isLoading.collect { isLoading ->
            handleLoadingState(isLoading)
        }
    }

    private suspend fun observeErrorState() {
        menuViewModel.errorMessage.collect { errorMessage ->
            handleErrorState(errorMessage)
        }
    }

    private fun updateButtonStates(currentCategory: CoffeeCategory) {
        when (currentCategory) {
            CoffeeCategory.HOT -> {
                setTabButtonSelected(binding.categoryTabs.hotButton)
                setTabButtonUnselected(binding.categoryTabs.coldButton)
            }
            CoffeeCategory.ICED -> {
                setTabButtonSelected(binding.categoryTabs.coldButton)
                setTabButtonUnselected(binding.categoryTabs.hotButton)
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

    private fun clearSearchFieldSafely() {
        val searchEditText = binding.searchSection.searchEditText
        val currentText = searchEditText.text.toString()

        if (currentText.isNotEmpty()) {
            searchEditText.removeTextChangedListener(searchTextWatcher)
            searchEditText.text?.clear()
            searchEditText.addTextChangedListener(searchTextWatcher)
        }
    }

    private fun updateSearchFieldIfNeeded(query: String) {
        val searchEditText = binding.searchSection.searchEditText
        if (searchEditText.text.toString() != query) {
            searchEditText.setText(query)
            searchEditText.setSelection(query.length)
        }
    }

    private fun updateCoffeeList(coffeeList: List<Coffee>) {
        coffeeAdapter.submitList(coffeeList) {
            if (coffeeList.isNotEmpty()) {
                binding.coffeeRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun updateEmptyState(coffeeList: List<Coffee>) {
        val isLoading = menuViewModel.isLoading.value
        val hasError = menuViewModel.errorMessage.value != null

        when {
            coffeeList.isEmpty() && !isLoading && !hasError -> {
                showEmptyState()
            }
            coffeeList.isNotEmpty() -> {
                showCoffeeList()
            }
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            showLoadingDialog()
            hideErrorState()
            hideEmptyState()
        } else {
            hideLoadingDialog()
        }
    }

    private fun handleErrorState(errorMessage: String?) {
        if (errorMessage != null && !menuViewModel.isLoading.value) {
            showErrorState(errorMessage)
        } else {
            hideErrorState()
            if (binding.emptyStateSection.emptyStateContainer.visibility != View.VISIBLE) {
                showCoffeeList()
            }
        }
    }

    private fun showEmptyState() {
        val currentCategory = menuViewModel.currentCategory.value
        val hasSearchQuery = menuViewModel.searchQuery.value.isNotEmpty()

        setViewsVisibility(
            recyclerView = View.GONE,
            emptyState = View.VISIBLE,
            errorState = View.GONE
        )

        configureEmptyState(currentCategory, hasSearchQuery)
    }

    private fun configureEmptyState(currentCategory: CoffeeCategory, hasSearchQuery: Boolean) {
        val emptyState = binding.emptyStateSection

        if (hasSearchQuery) {
            emptyState.emptyStateIcon.setImageResource(R.drawable.ic_search_empty)
            emptyState.emptyStateTitle.text = getString(R.string.no_results_found)
            emptyState.emptyStateMessage.text = getString(
                R.string.empty_search_message,
                menuViewModel.searchQuery.value
            )
            emptyState.clearSearchButton.visibility = View.VISIBLE
        } else {
            val categoryName = getCategoryDisplayName(currentCategory)
            emptyState.emptyStateIcon.setImageResource(R.drawable.ic_coffee_empty)
            emptyState.emptyStateTitle.text = getString(R.string.no_coffee_available, categoryName)
            emptyState.emptyStateMessage.text = getString(R.string.empty_category_message, categoryName)
            emptyState.clearSearchButton.visibility = View.GONE
        }
    }

    private fun showCoffeeList() {
        setViewsVisibility(
            recyclerView = View.VISIBLE,
            emptyState = View.GONE,
            errorState = View.GONE
        )
    }

    private fun showErrorState(errorMessage: String) {
        binding.errorStateSection.errorTextView.text = errorMessage
        setViewsVisibility(
            recyclerView = View.GONE,
            emptyState = View.GONE,
            errorState = View.VISIBLE
        )
    }

    private fun hideEmptyState() {
        binding.emptyStateSection.emptyStateContainer.visibility = View.GONE
    }

    private fun hideErrorState() {
        binding.errorStateSection.errorStateContainer.visibility = View.GONE
    }

    private fun setViewsVisibility(recyclerView: Int, emptyState: Int, errorState: Int) {
        binding.coffeeRecyclerView.visibility = recyclerView
        binding.emptyStateSection.emptyStateContainer.visibility = emptyState
        binding.errorStateSection.errorStateContainer.visibility = errorState
    }

    private fun getCategoryDisplayName(category: CoffeeCategory): String {
        return when (category) {
            CoffeeCategory.HOT -> getString(R.string.hot).lowercase()
            CoffeeCategory.ICED -> getString(R.string.cold).lowercase()
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            createLoadingDialog()
        }

        if (loadingDialog?.isShowing != true) {
            loadingDialog?.show()
        }
    }

    private fun createLoadingDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.loading_dialog, null)

        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun hideLoadingDialog() {
        loadingDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    private fun handleNotificationClick() {
        Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show()
    }

    private fun handleMenuClick() {
        Toast.makeText(requireContext(), "Menu clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cleanupResources()
    }

    private fun cleanupResources() {
        hideLoadingDialog()
        loadingDialog = null
        searchTextWatcher = null
        _binding = null
    }

    companion object {
        private const val TAG = "DrinkMenuFragment"
    }
}