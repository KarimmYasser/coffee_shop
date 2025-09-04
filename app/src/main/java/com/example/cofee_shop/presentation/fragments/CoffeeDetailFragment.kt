package com.example.cofee_shop.presentation.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.example.cofee_shop.presentation.managers.CoffeeDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class CoffeeDetailFragment : Fragment(R.layout.fragment_coffee_detail) {
    private var _binding: FragmentCoffeeDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CoffeeDetailFragmentArgs by navArgs()
    private val coffee: Coffee by lazy { args.coffee }

    private val viewModel: CoffeeDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCoffeeDetailBinding.bind(view)

        viewModel.initializeCoffee(coffee)
        setupUI()
        setupClickListeners()
        bindCoffeeData()
        observeViewModel()
    }

    private fun setupUI() {
        updateFavoriteIcon(false) // Initial state
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.btnDecrease.setOnClickListener {
            viewModel.decreaseQuantity()
        }

        binding.btnIncrease.setOnClickListener {
            viewModel.increaseQuantity()
        }

        binding.btnBuyNow.setOnClickListener {
            showOrderDialog()
        }
    }

    private fun showOrderDialog() {
        val currentQuantity = viewModel.quantity.value
        val coffee = viewModel.uiState.value.coffee ?: return
        val totalPrice = coffee.price * 3000 * currentQuantity
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.order_confirmation_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_coffee_name)?.text = coffee.title
        dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_quantity)?.text = "Quantity: $currentQuantity"
        dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_total_price)?.text = "Total: Rp ${formatter.format(totalPrice.toInt())}"

        dialogView.findViewById<android.widget.Button>(R.id.btn_cancel_order)?.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<android.widget.Button>(R.id.btn_confirm_order)?.setOnClickListener {
            dialog.dismiss()
            createAndProcessOrder(currentQuantity, totalPrice)
        }

        dialog.show()
    }

    private fun createAndProcessOrder(quantity: Int, totalPrice: Double) {
        val loadingDialog = createLoadingDialog()
        loadingDialog.show()

        viewModel.createOrder(
            coffee = coffee,
            quantity = quantity,
            totalPrice = totalPrice,
            onOrderCreated = { orderId ->
                loadingDialog.dismiss()
                // Navigate to payment fragment
                val action = CoffeeDetailFragmentDirections
                    .actionCoffeeDetailFragmentToPaymentFragment(orderId)
                findNavController().navigate(action)
            },
            onError = { error ->
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to create order: $error", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun createLoadingDialog(): AlertDialog {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.loading_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.quantity.collect { quantity ->
                        binding.tvQuantity.text = quantity.toString()
                    }
                }

                launch {
                    viewModel.isFavorite.collect { isFavorite ->
                        updateFavoriteIcon(isFavorite)
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.btnFavorite.isEnabled = !isLoading
                    }
                }

                launch {
                    viewModel.uiState.collect { uiState ->
                        // Handle messages
                        uiState.message?.let { message ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            viewModel.clearMessage()
                        }

                        // Handle errors
                        uiState.error?.let { error ->
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                            viewModel.clearError()
                        }

                        // Handle navigation
                        if (uiState.shouldNavigateBack) {
                            findNavController().navigateUp()
                            viewModel.navigationHandled()
                        }
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_favorite_filled // You'll need to create this
        } else {
            R.drawable.ic_favorite_outline // You'll need to create this
        }

        val icon = ContextCompat.getDrawable(requireContext(), iconRes)
        binding.btnFavorite.setImageDrawable(icon)

        // Optional: Add color tint for better visual feedback
        val colorRes = if (isFavorite) {
            R.color.favorite_active // You'll need to define this color
        } else {
            R.color.favorite_inactive // You'll need to define this color
        }

        binding.btnFavorite.imageTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CoffeeDetailFragment"
    }
}