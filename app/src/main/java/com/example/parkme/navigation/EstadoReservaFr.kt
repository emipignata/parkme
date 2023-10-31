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
import com.example.parkme.databinding.FragmentEstadoReservaBinding
import com.example.parkme.entities.Reserva
import com.google.firebase.auth.FirebaseAuth

class EstadoReservaFr : Fragment() {

    private val args: EstadoReservaFrArgs by navArgs()
    private lateinit var binding: FragmentEstadoReservaBinding
    private val reserva: Reserva by lazy { args.reserva } // Use lazy initialization
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEstadoReservaBinding.inflate(inflater, container, false)
        val botonVolver: Button = binding.estadoReservaVolver

        val botonPagar: Button = binding.pagarReserva

        botonVolver.setOnClickListener{
            binding.root.findNavController().navigateUp()

        }

        botonPagar.setOnClickListener{
            binding.root.findNavController().navigate(EstadoReservaFrDirections.actionEstadoReservaFrToCheckoutActivity(100f))
        }


        return binding.root
    }

}