package com.example.cofee_shop.presentation.managers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.domain.usecases.user.SaveUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserNameState(
    val userName: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class EnterUsernameViewModel @Inject constructor(
    private val saveUserNameUseCase: SaveUserNameUseCase
) : ViewModel() {

    private val _enterUserState = MutableStateFlow(UserNameState())
    val enterUserState: StateFlow<UserNameState> get() = _enterUserState.asStateFlow()

    fun saveUserName(): Job {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("EnterUsernameViewModel", "Error saving username", exception)
        }

        return viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                saveUserNameUseCase(enterUserState.value.userName)
            }.onFailure {
                Log.e("EnterUsernameViewModel", "Failed to save username", it)
            }
        }
    }

    fun onUserNameChanged(newName: String) {
        _enterUserState.update {
            it.copy(userName = newName, errorMessage = null)
        }
    }

    fun displayError(message: String) {
        _enterUserState.update {
            it.copy(errorMessage = message)
        }
    }
}