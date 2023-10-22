package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.databinding.FragmentCocheraDetailBinding
import com.example.parkme.entities.Cochera
import com.google.firebase.auth.FirebaseAuth

class ReservaFr : Fragment() {
    private val args: CocheraDetailFrArgs by navArgs()
    private lateinit var binding: FragmentCocheraDetailBinding
    private val cochera: Cochera by lazy { args.cochera } // Use lazy initialization
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocheraDetailBinding.inflate(inflater, container, false)
        val cocheraDetailText: TextView = binding.cocheraDetailText
        cocheraDetailText.text = "Cochera Details: ${cochera}"

        val currentUserText: TextView = binding.currentUserText
        currentUserText.text = "CurrentUserId: $uid"

        val ownerIdText: TextView = binding.ownerIdText
        ownerIdText.text = "OwnerId: ${cochera.owner}"

        val volverButton: Button = binding.CocheraDetailVolverButton
        val reservarButton: Button = binding.CocheraDetailReservarButton
        val programarButton: Button = binding.CocheraDetailProgramarButton
Log.e("CocheraDetailFr", "Cochera: $cochera")
        volverButton.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }

        reservarButton.setOnClickListener {
            val action = CocheraDetailFrDirections.actionCocheraDetailFrToReservaCocheraFr(cochera)
            Log.e("CocheraDetailFr", "Botón Reservar Apretado")
            Log.e("CocheraDetailFr", "Cochera a Reservar: $cochera")
            binding.root.findNavController()?.navigate(action)
        }

        programarButton.setOnClickListener {
            Toast.makeText(requireContext(), "Esta funcionalidad aún no está disponible", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}
