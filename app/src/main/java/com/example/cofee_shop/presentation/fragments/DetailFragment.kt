package com.example.cofee_shop.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cofee_shop.R
import coil.load
import com.example.cofee_shop.databinding.DetailBuyNowBinding
import com.example.cofee_shop.domain.models.Coffee
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: DetailBuyNowBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailBuyNowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get coffee from arguments (assume passed as Parcelable)
        val coffee = arguments?.getParcelable<Coffee>("coffee") ?: return
        viewModel.setCoffee(coffee)

        binding.tvCoffeeName.text = coffee.title
        binding.tvCoffeeDescription.text = coffee.description
        binding.tvCoffeePrice.text = "Rp ${coffee.price.toInt()}"
        binding.ivCoffeeImage.load(coffee.image)

        binding.btnIncrease.setOnClickListener { viewModel.increaseQuantity() }
        binding.btnDecrease.setOnClickListener { viewModel.decreaseQuantity() }

        binding.btnFavorite.setOnClickListener { viewModel.toggleFavorite() }

        binding.btnBuyNow.setOnClickListener { viewModel.placeOrder() }

        binding.btnClose.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        lifecycleScope.launchWhenStarted {
            viewModel.quantity.collectLatest {
                binding.tvQuantity.text = it.toString()
            }
        }
        lifecycleScope.launchWhenStarted {

            viewModel.isFavorite.collectLatest { isFav ->
                val icon = if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                binding.btnFavorite.setBackgroundResource(icon)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.orderPlaced.collectLatest { placed ->
                if (placed) {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}