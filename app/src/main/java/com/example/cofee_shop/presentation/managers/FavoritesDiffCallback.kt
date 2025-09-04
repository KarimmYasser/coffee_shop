package com.example.cofee_shop.presentation.managers

import androidx.recyclerview.widget.DiffUtil
import com.example.cofee_shop.presentation.viewmodel.FavoritesViewModel

class FavoritesDiffCallback(
    private val oldList: List<FavoritesViewModel.FavoriteUiModel>,
    private val newList: List<FavoritesViewModel.FavoriteUiModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}