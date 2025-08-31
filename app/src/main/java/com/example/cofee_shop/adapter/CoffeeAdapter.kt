package com.example.cofee_shop.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.cofee_shop.R
import com.example.cofee_shop.databinding.ItemCoffeeBinding
import com.example.cofee_shop.databinding.ItemHomeCoffeeBinding
import com.example.cofee_shop.domain.models.Coffee
import java.text.NumberFormat
import java.util.Locale

class CoffeeAdapter(
    private val onAddClick: (Coffee) -> Unit,
    private val onItemClick: (Coffee) -> Unit,
    private val isHome: Boolean
) : ListAdapter<Coffee, CoffeeAdapter.BaseCoffeeViewHolder>(CoffeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCoffeeViewHolder {
        return if (isHome) {
            HomeCoffeeViewHolder(
                ItemHomeCoffeeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            CoffeeViewHolder(
                ItemCoffeeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: BaseCoffeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    abstract inner class BaseCoffeeViewHolder(
        root: View
    ) : RecyclerView.ViewHolder(root) {
        abstract fun bind(coffee: Coffee)
    }

    inner class CoffeeViewHolder(private val binding: ItemCoffeeBinding) :
        BaseCoffeeViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun bind(coffee: Coffee) {
            binding.apply {
                coffeeNameTextView.text = coffee.title
                coffeeDescriptionTextView.text = coffee.description

                val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
                coffeePriceTextView.text = "Rp ${formatter.format((coffee.price * 3000).toInt())}"

                Glide.with(coffeeImageView.context)
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
                            Log.w("CoffeeAdapter", "Failed to load image: ${coffee.image}", e)
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
                    .into(coffeeImageView)

                addButtonCard.setOnClickListener {
                    onAddClick(coffee)
                }

                root.setOnClickListener {
                    onItemClick(coffee)
                }
            }
        }
    }

    inner class HomeCoffeeViewHolder(private val binding: ItemHomeCoffeeBinding) :
        BaseCoffeeViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun bind(coffee: Coffee) {
            binding.apply {
                coffeeNameTextView.text = coffee.title

                val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
                coffeePriceTextView.text = "Rp ${formatter.format((coffee.price * 3000).toInt())}"

                Glide.with(coffeeImageView.context)
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
                            Log.w("CoffeeAdapter", "Failed to load image: ${coffee.image}", e)
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
                    .into(coffeeImageView)

                addButtonCard.setOnClickListener {
                    onAddClick(coffee)
                }

                root.setOnClickListener {
                    onItemClick(coffee)
                }
            }
        }
    }

    private class CoffeeDiffCallback : DiffUtil.ItemCallback<Coffee>() {
        override fun areItemsTheSame(oldItem: Coffee, newItem: Coffee): Boolean {
            return oldItem.id == newItem.id && oldItem.isHot == newItem.isHot
        }

        override fun areContentsTheSame(oldItem: Coffee, newItem: Coffee): Boolean {
            return oldItem == newItem
        }
    }
}