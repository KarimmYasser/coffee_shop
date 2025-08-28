package com.example.cofee_shop.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.R
import com.example.cofee_shop.domain.usecases.user.SaveUserNameUseCase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class UsernameActivity : AppCompatActivity() {

    private val btnContinue: Button by lazy { findViewById(R.id.btnContinue) }
    private val etUsername by lazy { findViewById<android.widget.EditText>(R.id.etUsername) }
    private val enterUsernameViewModel: EnterUsernameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)
        setupListeners()
    }

    private fun setupListeners() {
        btnContinue.setOnClickListener {
            if (etUsername.text.isNullOrBlank()) return@setOnClickListener
            enterUsernameViewModel.saveUserName().invokeOnCompletion {
                runOnUiThread { navigateToMain() }
            }
        }
        etUsername.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterUsernameViewModel.onUserNameChanged(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@HiltViewModel
class EnterUsernameViewModel @Inject constructor(
    private val saveUserNameUseCase: SaveUserNameUseCase
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName.asStateFlow()

    fun saveUserName(): Job {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("EnterUsernameViewModel", "Error saving username", exception)
        }

        return viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                saveUserNameUseCase(userName.value ?: "")
            }.onFailure {
                Log.e("EnterUsernameViewModel", "Failed to save username", it)
            }
        }
    }

    fun onUserNameChanged(newName: String) {
        _userName.update { newName }
    }
}