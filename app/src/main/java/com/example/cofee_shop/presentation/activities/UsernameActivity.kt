package com.example.cofee_shop.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cofee_shop.R
import com.example.cofee_shop.presentation.managers.EnterUsernameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class UsernameActivity : AppCompatActivity() {

    private val btnContinue: Button by lazy { findViewById(R.id.btnContinue) }
    private val etUsername by lazy { findViewById<android.widget.EditText>(R.id.etUsername) }
    private val viewModel: EnterUsernameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)
        lifecycleScope.launch {
            viewModel.enterUserState.collect { usernameState ->
                if (etUsername.text.toString() != usernameState.userName) {
                    etUsername.setText(usernameState.userName)
                }
                etUsername.error = usernameState.errorMessage
            }
        }
        setupListeners()
    }

    private fun setupListeners() {
        btnContinue.setOnClickListener {
            if (etUsername.text.isNullOrBlank()) {
                viewModel.displayError("Username cannot be empty")
                return@setOnClickListener
            }
            viewModel.saveUserName().invokeOnCompletion {
                runOnUiThread { navigateToMain() }
            }
        }
        etUsername.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onUserNameChanged(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}