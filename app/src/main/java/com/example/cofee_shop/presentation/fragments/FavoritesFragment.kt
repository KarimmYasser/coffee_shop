package com.example.cofee_shop.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cofee_shop.R
import com.example.cofee_shop.presentation.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val header = view.findViewById<View>(R.id.headerSection)
        val params = header.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = 30
        header.layoutParams = params

        val recyclerView = view.findViewById<RecyclerView>(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = FavoritesAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)
        }

        viewModel.loadFavorites()
    }
}

class FavoritesAdapter(private var items: List<FavoritesViewModel.FavoriteUiModel>) :
    RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<android.widget.ImageView>(R.id.productImage)
        val title = itemView.findViewById<android.widget.TextView>(R.id.productTitle)
        val price = itemView.findViewById<android.widget.TextView>(R.id.productPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fav, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.name
        holder.price.text = "$${item.price}"
        holder.image.setImageResource(item.imageRes)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<FavoritesViewModel.FavoriteUiModel>) {
        items = newList
        notifyDataSetChanged()
    }
}
