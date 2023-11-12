package com.example.parkme.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.parkme.navigation.ReservasFinalizadasFr
import com.example.parkme.navigation.ReservasActivasFr

class HistorialPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReservasActivasFr()
            1 -> ReservasFinalizadasFr()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}