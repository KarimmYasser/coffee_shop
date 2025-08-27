package com.example.cofee_shop.adapter



import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cofee_shop.ui.fragment.CoffeeListFragment

class CategoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CoffeeListFragment.newInstance("hot")
            1 -> CoffeeListFragment.newInstance("iced")
            else -> CoffeeListFragment.newInstance("hot")
        }
    }
}