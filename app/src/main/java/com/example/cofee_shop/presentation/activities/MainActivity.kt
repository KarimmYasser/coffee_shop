package com.example.cofee_shop.presentation.activities

import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.Gravity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.cofee_shop.R
import com.example.cofee_shop.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Link BottomNavigationView with NavController (this handles navigation automatically)
        binding.bottomNav.setupWithNavController(navController)

        // Setup custom underline for selected items
        setupCustomUnderline(navController)
    }

    private fun setupCustomUnderline(navController: NavController) {
        // Listen for destination changes to update underline
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val selectedIndex = when (destination.id) {
                R.id.homeFragment -> 0
                R.id.drinkMenuFragment -> 1
                R.id.orderFragment -> 2
                R.id.favoritesFragment -> 3
                else -> 0
            }
            addUnderlineToSelectedItem(selectedIndex)
        }

        // Set initial underline for home
        addUnderlineToSelectedItem(0)
    }

    @SuppressLint("RestrictedApi")
    private fun addUnderlineToSelectedItem(selectedIndex: Int) {
        val bottomNav = binding.bottomNav
        val menuView = bottomNav.getChildAt(0) as BottomNavigationMenuView

        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i)

            if (i == selectedIndex) {
                // Add underline to selected item
                val underlineDrawable = ShapeDrawable(RectShape()).apply {
                    paint.color = ContextCompat.getColor(this@MainActivity, R.color.background_light_cream)
                    intrinsicHeight = resources.getDimensionPixelSize(R.dimen.underline_height)
                }

                // Create layer drawable with underline at bottom and center
                val layerDrawable = LayerDrawable(arrayOf(underlineDrawable))
                layerDrawable.setLayerGravity(0, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                layerDrawable.setLayerHeight(0, resources.getDimensionPixelSize(R.dimen.underline_height))

                // Set custom width for underline (adjust this value as needed)
                val underlineWidth = resources.getDimensionPixelSize(R.dimen.underline_width)
                layerDrawable.setLayerWidth(0, underlineWidth)

                item.background = layerDrawable
            } else {
                // Remove underline from unselected items
                item.background = null
            }
        }
    }
}