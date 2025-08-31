package com.example.cofee_shop.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cofee_shop.R
import com.example.cofee_shop.presentation.managers.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.userName.collectLatest { state ->
                    if (state == null) {
                        startActivity(Intent(this@SplashActivity, UsernameActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    }
                    finish()
                }
            }
        }, 1500)
    }
}