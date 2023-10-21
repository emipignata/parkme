package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.parkme.databinding.FragmentCocheraDetailBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.User
import com.google.gson.Gson


class CocheraDetailFr() : Fragment() {
    private lateinit var binding: FragmentCocheraDetailBinding
    private lateinit var cochera: Cochera
    private lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocheraDetailBinding.inflate(inflater, container, false)
        // Retrieve the JSON string from the arguments
        val userJson = arguments?.getString("userJson")

        // Deserialize the JSON string back to the User object
        user = Gson().fromJson(userJson, User::class.java)
        val args: CocheraDetailFrArgs by navArgs()
        cochera = args.cochera

        binding.currentUserText.text = "Current User Details: ${user}"
        binding.cocheraDetailText.text = "Cochera Details: ${cochera}"

        Log.e("CocheraDetailFr", "onCreateView: $cochera")
        return binding.root
    }
}
