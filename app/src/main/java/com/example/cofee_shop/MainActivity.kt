package com.example.cofee_shop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.cofee_shop.databinding.ActivityMainBinding
import com.example.cofee_shop.presentation.fragments.DrinkMenuFragment
import com.example.cofee_shop.presentation.fragments.FavoritesFragment
import com.example.cofee_shop.presentation.fragments.HomeFragment
import com.example.cofee_shop.presentation.fragments.OrderFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Link BottomNavigationView with NavController
        binding.bottomNav.setupWithNavController(navController)
    }
}