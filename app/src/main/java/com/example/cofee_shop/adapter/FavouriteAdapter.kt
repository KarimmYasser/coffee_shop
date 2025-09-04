package com.example.cofee_shop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cofee_shop.R
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.presentation.managers.FavoritesDiffCallback
import com.example.cofee_shop.presentation.viewmodel.FavoritesViewModel
import com.example.cofee_shop.presentation.viewmodel.FavoritesViewModel.*

class FavoritesAdapter(
    private var items: List<FavoritesViewModel.FavoriteUiModel>,
    private val onRemoveClick: (Int) -> Unit,
    private val onItemClick: (Coffee) -> Unit // Add click listener for navigation
) : RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.productImage)
        val title = itemView.findViewById<TextView>(R.id.productTitle)
        val price = itemView.findViewById<TextView>(R.id.productPrice)
        val favoriteIcon = itemView.findViewById<ImageView>(R.id.favoriteIcon)
        val card = itemView.findViewById<CardView>(R.id.favoriteCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fav, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.name
        holder.price.text = item.price

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.coffee_placeholder)
            .error(R.drawable.coffee_placeholder)
            .centerCrop()
            .into(holder.image)

        // Handle favorite icon click (remove from favorites)
        holder.favoriteIcon.setOnClickListener {
            onRemoveClick(item.id)
        }

        // Handle card click for navigation to detail
        holder.card.setOnClickListener {
            onItemClick(item.coffee)
        }

        // Add ripple effect for better UX
        holder.card.isClickable = true
        holder.card.isFocusable = true
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<FavoritesViewModel.FavoriteUiModel>) {
        val diffCallback = FavoritesDiffCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newList
        diffResult.dispatchUpdatesTo(this)
    }
}
