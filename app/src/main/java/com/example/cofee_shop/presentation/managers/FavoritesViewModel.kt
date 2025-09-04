package com.example.cofee_shop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.R
import com.example.cofee_shop.domain.usecases.favourites.GetFavoritesUseCase
import com.example.cofee_shop.domain.usecases.favourites.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    data class FavoriteUiModel(
        val id: Int,
        val name: String,
        val price: String,
        val imageUrl: String,
        val description: String
    )

    private val _favorites = MutableLiveData<List<FavoriteUiModel>>(emptyList())
    val favorites: LiveData<List<FavoriteUiModel>> get() = _favorites

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getFavoritesUseCase().collect { list ->
                    _favorites.value = list.map { entity ->
                        FavoriteUiModel(
                            id = entity.drinkId,
                            name = entity.title,
                            price = "Rp ${entity.price}",
                            imageUrl = entity.imageUrl,
                            description = entity.description
                        )
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error
            }
        }
    }

    fun removeFavorite(drinkId: Int) {
        viewModelScope.launch {
            try {
                removeFavoriteUseCase(drinkId)
                // The Flow will automatically update the UI
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}