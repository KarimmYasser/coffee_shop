package com.example.cofee_shop.presentation.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.cofee_shop.R
import com.example.cofee_shop.databinding.FragmentCoffeeDetailBinding
import com.example.cofee_shop.domain.models.Coffee
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class CoffeeDetailFragment : Fragment(R.layout.fragment_coffee_detail) {
    private var _binding: FragmentCoffeeDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CoffeeDetailFragmentArgs by navArgs()
    private val coffee: Coffee by lazy { args.coffee }
    private var quantity = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCoffeeDetailBinding.bind(view)
        setupUI()
        setupClickListeners()
        bindCoffeeData()
    }

    private fun setupUI() {
        updateQuantityDisplay()
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }

        binding.btnDecrease.setOnClickListener {
            decreaseQuantity()
        }

        binding.btnIncrease.setOnClickListener {
            increaseQuantity()
        }

        binding.btnBuyNow.setOnClickListener {
            buyNow()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindCoffeeData() {
        binding.apply {
            tvCoffeeName.text = coffee.title
            tvCoffeeDescription.text = coffee.description

            val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
            tvCoffeePrice.text = "Rp ${formatter.format((coffee.price * 3000).toInt())}"

            Glide.with(requireContext())
                .load(coffee.image)
                .centerCrop()
                .placeholder(R.drawable.ic_coffee_placeholder)
                .error(R.drawable.ic_coffee_placeholder)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        Log.w("CoffeeDetailFragment", "Failed to load image: ${coffee.image}", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(ivCoffeeImage)
        }
    }

    private fun decreaseQuantity() {
        if (quantity > 1) {
            quantity--
            updateQuantityDisplay()
        }
    }

    private fun increaseQuantity() {
        quantity++
        updateQuantityDisplay()
    }

    private fun updateQuantityDisplay() {
        binding.tvQuantity.text = quantity.toString()
    }

    private fun toggleFavorite() {
        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
    }

    private fun buyNow() {
        val message = "Added $quantity ${coffee.title} to cart"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CoffeeDetailFragment"
    }
}