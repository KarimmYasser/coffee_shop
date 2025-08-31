package com.example.cofee_shop.adapter

import android.annotation.SuppressLint
import com.bumptech.glide.load.DataSource
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.cofee_shop.R
import com.example.cofee_shop.databinding.ItemHomeCoffeeBinding
import com.example.cofee_shop.domain.models.Coffee
import java.util.Locale
import java.text.NumberFormat

class HomeCoffeeAdapter(
    private val onItemClick: (Coffee) -> Unit
) : ListAdapter<Coffee, HomeCoffeeAdapter.HomeCoffeeViewHolder>(CoffeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCoffeeViewHolder {
        return HomeCoffeeViewHolder(
            ItemHomeCoffeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeCoffeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HomeCoffeeViewHolder(private val binding: ItemHomeCoffeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(coffee: Coffee) {
            binding.apply {
                coffeeNameTextView.text = coffee.title

                // Format price for Indonesian Rupiah
                val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
                coffeePriceTextView.text = "Rp ${formatter.format((coffee.price * 3000).toInt())}"

                // Load coffee image
                loadCoffeeImage(coffeeImageView, coffee)

                // Set click listener for navigation to detail
                root.setOnClickListener {
                    onItemClick(coffee)
                }
            }
        }

        private fun loadCoffeeImage(imageView: ImageView, coffee: Coffee) {
            Glide.with(imageView.context)
                .load(coffee.image)
                .centerCrop()
                .placeholder(R.drawable.ic_coffee_placeholder)
                .error(R.drawable.ic_coffee_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.w("HomeCoffeeAdapter", "Failed to load image: ${coffee.image}", e)
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
                .into(imageView)
        }
    }

    private class CoffeeDiffCallback : DiffUtil.ItemCallback<Coffee>() {
        override fun areItemsTheSame(oldItem: Coffee, newItem: Coffee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Coffee, newItem: Coffee): Boolean {
            return oldItem == newItem
        }
    }
}