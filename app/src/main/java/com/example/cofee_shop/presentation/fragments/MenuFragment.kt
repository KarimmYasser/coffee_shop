package com.example.cofee_shop.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.example.cofee_shop.adapter.CategoryPagerAdapter
import com.example.cofee_shop.databinding.FragmentDrinkMenuBinding
import com.example.cofee_shop.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentDrinkMenuBinding? = null
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var categoryPagerAdapter: CategoryPagerAdapter

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

        setupViewPager()
        setupTabs()
        setupSearch()
        observeViewModel()
    }

    private fun setupViewPager() {
        categoryPagerAdapter = CategoryPagerAdapter(this)
        binding.categoryViewPager.adapter = categoryPagerAdapter
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.categoryTabLayout, binding.categoryViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Hot"
                1 -> "Cold"
                else -> "Hot"
            }
        }.attach()
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                menuViewModel.searchCoffee(s.toString())
            }
        })
    }

    private fun observeViewModel() {
        menuViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        menuViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.errorTextView.text = errorMessage
                binding.errorTextView.visibility = View.VISIBLE
            } else {
                binding.errorTextView.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}