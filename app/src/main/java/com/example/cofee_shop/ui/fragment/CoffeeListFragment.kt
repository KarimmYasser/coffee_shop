package com.example.cofee_shop.ui.fragment



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cofee_shop.R
import com.example.cofee_shop.adapter.CoffeeAdapter
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.databinding.FragmentCoffeeListBinding
import com.example.cofee_shop.viewmodel.MenuViewModel

class CoffeeListFragment : Fragment() {

    private var _binding: FragmentCoffeeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var coffeeAdapter: CoffeeAdapter
    private lateinit var menuViewModel: MenuViewModel

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String): CoffeeListFragment {
            val fragment = CoffeeListFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoffeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuViewModel = ViewModelProvider(requireParentFragment())[MenuViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        val category = arguments?.getString(ARG_CATEGORY) ?: "hot"
        if (category == "hot") {
            menuViewModel.loadHotCoffee()
        } else {
            menuViewModel.loadIcedCoffee()
        }
    }

    private fun setupRecyclerView() {
        coffeeAdapter = CoffeeAdapter(
            coffeeList = emptyList(),
            onItemClick = { coffee ->
                // Navigate to detail screen
                findNavController().navigate(
                    R.id.action_menuFragment_to_detailFragment,
                    Bundle().apply { putParcelable("coffee_item", coffee) }
                )
            },
            onFavoriteClick = { coffee ->
                menuViewModel.toggleFavorite(coffee)
            }
        )

        binding.coffeeRecyclerView.apply {
            adapter = coffeeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        val category = arguments?.getString(ARG_CATEGORY) ?: "hot"

        if (category == "hot") {
            menuViewModel.filteredHotCoffeeList.observe(viewLifecycleOwner) { coffeeList ->
                updateUI(coffeeList)
            }
        } else {
            menuViewModel.filteredIcedCoffeeList.observe(viewLifecycleOwner) { coffeeList ->
                updateUI(coffeeList)
            }
        }
    }

    private fun updateUI(coffeeList: List<Coffee>) { // Changed from CoffeeItem
        if (coffeeList.isEmpty()) {
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.coffeeRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateTextView.visibility = View.GONE
            binding.coffeeRecyclerView.visibility = View.VISIBLE
            coffeeAdapter.updateList(coffeeList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}