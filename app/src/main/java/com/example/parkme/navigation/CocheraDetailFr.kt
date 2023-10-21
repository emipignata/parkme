package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.navArgs
import com.example.parkme.R
import com.example.parkme.databinding.FragmentCocheraDetailBinding
import com.example.parkme.entities.Cochera

class CocheraDetailFr() : Fragment() {
    val args: CocheraDetailFrArgs by navArgs()
    private lateinit var binding: FragmentCocheraDetailBinding
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cochera: Cochera = args.cochera
        binding = FragmentCocheraDetailBinding.inflate(inflater, container, false)
        fragmentManager = requireActivity().supportFragmentManager

        val cocheraDetailText = binding.root.findViewById<TextView>(R.id.cocheraDetailText)
        cocheraDetailText.text = "Cochera Details: ${cochera}"
        Log.e("CocheraDetailFr", "onCreateView: " + cochera)
        return binding.root
    }
}