package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parkme.databinding.FragmentCocheraDetailBinding
import com.example.parkme.entities.Cochera

class CocheraDetailFr(cochera: Cochera) : Fragment() {
    private lateinit var binding: FragmentCocheraDetailBinding
    private lateinit var fragmentManager: FragmentManager
    private val cochera = cochera


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocheraDetailBinding.inflate(inflater, container, false)
        fragmentManager = requireActivity().supportFragmentManager
        binding.cocheraDetailText.text = cochera.toString()
        Log.e("CocheraDetailFr", "onCreateView: " + cochera.toString())
        return binding.root
    }



}
