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
import com.bumptech.glide.Glide
import com.example.parkme.R
import com.example.parkme.databinding.FragmentCocheraDetailBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CocheraDetailUserFr : Fragment() {
    private val args: CocheraDetailUserFrArgs by navArgs()
    private lateinit var binding: FragmentCocheraDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private val cochera: Cochera by lazy { args.cochera }
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getUserState()
        binding = FragmentCocheraDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setBinding(){
        var reservarButton: Button = binding.CocheraDetailReservarButton
        var programarButton: Button = binding.CocheraDetailProgramarButton
        if (user.reservaInCheckIn != "") {
            reservarButton.isEnabled = false
            programarButton.text = getString(R.string.realizar_checkout_cochera_anterior)
        } else {
            reservarButton.isEnabled = true
            programarButton.text = getString(R.string.estacionar_ahora)
            reservarButton.setOnClickListener {
                val action = CocheraDetailUserFrDirections.actionCocheraDetailUserFrToReservaCocheraFr(cochera)
                binding.root.findNavController()?.navigate(action)
            }
        }

        if (user.reservaInReservada != "") {
            programarButton.isEnabled = false
            programarButton.text = getString(R.string.alcanzaste_el_limite_de_una_reserva)

        } else {
            programarButton.isEnabled = true
            programarButton.text = getString(R.string.programa_reserva)
            programarButton.setOnClickListener {
                val action = CocheraDetailUserFrDirections.actionCocheraDetailUserFrToReservaCocheraFr(cochera)
                binding.root.findNavController()?.navigate(action)
            }
        }

        binding.ownerIdText.text = cochera.direccion
        binding.cocheraDetailText.text = cochera.direccion
        binding.precioPorHoraDetail.text = cochera.price.toString()
        binding.cocheraOwnerName.text = "Dueño: ${cochera.ownerName}"
        Glide.with(requireContext())
            .load(cochera.urlImage)
            .centerCrop()
            .into(binding.imageView2)

        programarButton.setOnClickListener {
            val action = CocheraDetailUserFrDirections.actionCocheraDetailUserFrToProgramarReservaFr(cochera)
            binding.root.findNavController()?.navigate(action)
        }
    }

    private fun getUserState() {
        if (uid != null) {
            db.collection("users").document(uid!!)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user = document.toObject(User::class.java)!!
                        setBinding()
                    } else {
                        Log.d("ReservaCocheraFr", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ReservaCocheraFr", "get failed with ", exception)
                }
        }
    }
}
