package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.domain.usecases.user.GetUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        getUserName()
    }

    fun getUserName() {
        val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, exception ->
                _userName.update { null }
            }

        viewModelScope.launch(coroutineExceptionHandler) {
            _userName.update {
                try {
                    getUserNameUseCase()
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
}