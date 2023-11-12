package com.example.parkme.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.parkme.adapter.HistorialPagerAdapter
import com.example.parkme.databinding.FragmentHistorialReservasBinding
import com.google.android.material.tabs.TabLayoutMediator

class HistorialFr : Fragment() {
    private lateinit var adapter: HistorialPagerAdapter
    private lateinit var binding: FragmentHistorialReservasBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistorialReservasBinding.inflate(inflater, container, false)
        adapter = HistorialPagerAdapter(this)
        binding.historialViewPager.adapter = adapter
        val tabLayout = binding.historialTabLayout
        val viewPager = binding.historialViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Reservas Activas"
            }
            else {
                tab.text = "Reservas Finalizadas"
            }

        }.attach()

        return binding.root
    }
}
