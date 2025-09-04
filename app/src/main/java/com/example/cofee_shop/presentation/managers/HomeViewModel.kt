package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.usecases.coffee.GetAllDrinksUseCase
import com.example.cofee_shop.domain.usecases.user.GetUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllDrinksUseCase: GetAllDrinksUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel() {

    private val _allCoffeeList = MutableStateFlow<List<Coffee>>(emptyList())
    val coffeeList: StateFlow<List<Coffee>> = _allCoffeeList.asStateFlow()

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = MutableStateFlow<ErrorState>(ErrorState.None)
    val errorState: StateFlow<ErrorState> = _errorState.asStateFlow()

    // Username state
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    // Track retry attempts
    private var retryCount = 0
    private val maxRetries = 3

    sealed class ErrorState {
        object None : ErrorState()
        object NetworkError : ErrorState()
        data class GenericError(val message: String) : ErrorState()
    }

    init {
        loadUserName()
        loadCoffeeData()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            try {
                val name = getUserNameUseCase()
                _userName.value = name
            } catch (e: Exception) {
                _userName.value = null
            }
        }
    }

    private fun loadCoffeeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = ErrorState.None

            kotlinx.coroutines.delay(500)

            try {
                // Add timeout to prevent indefinite loading
                kotlinx.coroutines.withTimeout(8000) { // 8 seconds timeout
                    getAllDrinksUseCase().collect { coffeeEntities ->
                        _allCoffeeList.value = coffeeEntities.mapIndexed { index, entity ->
                            Coffee(
                                entity.id,
                                entity.title,
                                entity.description,
                                entity.ingredients,
                                entity.imageUrl,
                                price = when (index % 4) {
                                    0 -> 4.5
                                    1 -> 5.0
                                    2 -> 4.0
                                    else -> 5.5
                                },
                                isHot = true
                            )
                        }

                        // Success - reset retry count and clear error
                        retryCount = 0
                        _errorState.value = ErrorState.None
                        _isLoading.value = false
                    }
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                _isLoading.value = false
                _errorState.value = ErrorState.NetworkError
            } catch (e: Exception) {
                _isLoading.value = false
                handleError(e)
            }
        }
    }

    private fun handleError(exception: Exception) {
        when (exception) {
            is UnknownHostException,
            is SocketTimeoutException,
            is IOException -> {
                _errorState.value = ErrorState.NetworkError
            }
            else -> {
                val errorMessage = exception.localizedMessage ?: "An unexpected error occurred"
                _errorState.value = ErrorState.GenericError(errorMessage)
            }
        }
    }

    fun retryLoadingData() {
        if (retryCount < maxRetries) {
            retryCount++
            // Clear error state before retrying
            _errorState.value = ErrorState.None
            loadCoffeeData()
        } else {
            _errorState.value = ErrorState.GenericError(
                "Unable to connect after multiple attempts. Please check your connection and try again later."
            )
            _isLoading.value = false
        }
    }

    fun resetRetryCount() {
        retryCount = 0
    }

    fun hasCachedData(): Boolean {
        return _allCoffeeList.value.isNotEmpty()
    }
}