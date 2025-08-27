package com.example.cofee_shop.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import com.example.cofee_shop.R
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.databinding.ItemCoffeeBinding


class CoffeeAdapter(
    private var coffeeList: List<Coffee>,
    private val onItemClick: (Coffee) -> Unit,
    private val onFavoriteClick: (Coffee) -> Unit
) : RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {

    inner class CoffeeViewHolder(private val binding: ItemCoffeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coffee: Coffee) {
            binding.coffeeNameTextView.text = coffee.title
            binding.coffeeIngredientsTextView.text = coffee.ingredients.joinToString(", ")
            binding.coffeePriceTextView.text = "$${String.format("%.2f", coffee.price)}"

            // Load image using Glide
            // Load image placeholder for now
            binding.coffeeImageView.setImageResource(R.drawable.coffee_placeholder)

            // Handle clicks
            binding.root.setOnClickListener {
                onItemClick(coffee)
            }

            binding.favoriteImageView.setOnClickListener {
                onFavoriteClick(coffee)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val binding = ItemCoffeeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CoffeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        holder.bind(coffeeList[position])
    }

    override fun getItemCount(): Int = coffeeList.size

    fun updateList(newList: List<Coffee>) {
        coffeeList = newList
        notifyDataSetChanged()
    }
}