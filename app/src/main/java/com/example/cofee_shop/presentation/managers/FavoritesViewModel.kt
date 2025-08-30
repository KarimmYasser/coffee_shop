package com.example.cofee_shop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.R
import com.example.cofee_shop.domain.usecases.favourites.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    data class FavoriteUiModel(
        val id: Int,
        val name: String,
        val price: Double,
        val imageRes: Int
    )

    private val _favorites = MutableLiveData<List<FavoriteUiModel>>(emptyList())
    val favorites: LiveData<List<FavoriteUiModel>> get() = _favorites

    fun loadFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { list ->
                _favorites.value = list.map { entity ->
                    FavoriteUiModel(
                        id = entity.drinkId,
                        name = "Coffee ${entity.drinkId}",
                        price = 5.0,
                        imageRes = R.drawable.ic_launcher_foreground
                    )
                }
            }
        }
    }
}
