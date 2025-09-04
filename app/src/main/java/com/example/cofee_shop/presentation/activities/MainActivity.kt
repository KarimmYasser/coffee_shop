package com.example.cofee_shop.presentation.activities

import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
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

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            binding.bottomNav.setupWithNavController(navController)
            setupCustomUnderline(navController)
        } else {
            Log.e("MainActivity", "NavHostFragment not found")
        }
    }

    private fun setupCustomUnderline(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Hide bottom nav on details screen, show on others
            when (destination.id) {
                R.id.coffeeDetailFragment -> { // Replace with your actual details fragment ID
                    binding.bottomNav.visibility = View.GONE
                }
                R.id.homeFragment,
                R.id.drinkMenuFragment,
                R.id.orderFragment,
                R.id.favoritesFragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    val selectedIndex = when (destination.id) {
                        R.id.homeFragment -> 0
                        R.id.drinkMenuFragment -> 1
                        R.id.orderFragment -> 2
                        R.id.favoritesFragment -> 3
                        else -> 0
                    }
                    addUnderlineToSelectedItem(selectedIndex)
                }
                else -> {
                    // For any other fragments, hide the bottom nav
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }

        addUnderlineToSelectedItem(0)
    }

    @SuppressLint("RestrictedApi")
    private fun addUnderlineToSelectedItem(selectedIndex: Int) {
        val bottomNav = binding.bottomNav
        val menuView = bottomNav.getChildAt(0) as? BottomNavigationMenuView ?: return

        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i)

            if (i == selectedIndex) {
                val underlineDrawable = ShapeDrawable(RectShape()).apply {
                    paint.color = ContextCompat.getColor(this@MainActivity, R.color.background_light_cream)
                    intrinsicHeight = resources.getDimensionPixelSize(R.dimen.underline_height)
                }

                val layerDrawable = LayerDrawable(arrayOf(underlineDrawable))
                layerDrawable.setLayerGravity(0, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                layerDrawable.setLayerHeight(0, resources.getDimensionPixelSize(R.dimen.underline_height))

                val underlineWidth = resources.getDimensionPixelSize(R.dimen.underline_width)
                layerDrawable.setLayerWidth(0, underlineWidth)

                item.background = layerDrawable
            } else {
                item.background = null
            }
            }
        }
}