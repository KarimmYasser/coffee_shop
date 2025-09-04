package com.example.cofee_shop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.R
import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import com.example.cofee_shop.data.mappers.CoffeeMapper
import com.example.cofee_shop.domain.models.Coffee
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
        val description: String,
        val coffee: Coffee
    )

    private val _favorites = MutableLiveData<List<FavoriteUiModel>>(emptyList())
    val favorites: LiveData<List<FavoriteUiModel>> get() = _favorites

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _navigateToDetail = MutableLiveData<Coffee?>()
    val navigateToDetail: LiveData<Coffee?> get() = _navigateToDetail

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getFavoritesUseCase().collect { list ->
                    _favorites.value = list.map { entity ->
                        val coffee = entity.toCoffee()
                        FavoriteUiModel(
                            id = entity.drinkId,
                            name = entity.title,
                            price = "Rp ${entity.price.toInt()}.000",
                            imageUrl = entity.imageUrl,
                            description = entity.description,
                            coffee = coffee
                        )
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false

            }
        }
    }

    fun removeFavorite(drinkId: Int) {
        viewModelScope.launch {
            try {
                removeFavoriteUseCase(drinkId)
            } catch (e: Exception) {

            }
        }
    }

    fun onCoffeeClicked(coffee: Coffee) {
        _navigateToDetail.value = coffee
    }

    fun onNavigateToDetailComplete() {
        _navigateToDetail.value = null
    }
}

fun FavoriteEntity.toCoffee(): Coffee {
    return Coffee(
        id = this.drinkId,
        title = this.title,
        description = this.description,
        ingredients = this.ingredients,
        image = this.imageUrl,
        isHot = this.isHot,
        price = this.price

    )
}