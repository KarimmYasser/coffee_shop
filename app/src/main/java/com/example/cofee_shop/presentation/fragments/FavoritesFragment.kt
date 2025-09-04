package com.example.cofee_shop.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cofee_shop.R
import com.example.cofee_shop.adapter.FavoritesAdapter
import com.example.cofee_shop.presentation.viewmodel.FavoritesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerView(view)
        observeViewModel()
    }

    private fun setupViews(view: View) {
        val header = view.findViewById<View>(R.id.headerSection)
        val params = header.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = StatusBarUtils.getStatusBarHeight(requireContext())
        header.layoutParams = params

        progressBar = view.findViewById(R.id.progressBar)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 1 // Each item takes 1 span
                }
            }
        }

        adapter = FavoritesAdapter(emptyList()) { drinkId ->
            viewModel.removeFavorite(drinkId)
            showSnackbar("Removed from favorites")
        }

        recyclerView.adapter = adapter

        // Add item decoration for spacing
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
    }

    private fun observeViewModel() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.updateList(favorites)

            if (favorites.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showEmptyState() {
        emptyStateLayout.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        emptyStateLayout.visibility = View.GONE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}

// 10. GridSpacingItemDecoration for proper spacing
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}

// 11. StatusBarUtils helper
object StatusBarUtils {
    @SuppressLint("InternalInsetResource")
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            // Fallback value
            (24 * context.resources.displayMetrics.density).toInt()
        }
    }
}