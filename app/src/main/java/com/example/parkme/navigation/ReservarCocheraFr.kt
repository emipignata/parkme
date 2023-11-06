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
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.R
import com.example.parkme.databinding.FragmentReservarCocheraBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Reserva
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReservarCocheraFr() : Fragment() {
    val args: CocheraDetailUserFrArgs by navArgs()
    private lateinit var binding: FragmentReservarCocheraBinding
    private lateinit var fragmentManager: FragmentManager
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var reserva: Reserva
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReservarCocheraBinding.inflate(inflater, container, false)
        fragmentManager = requireActivity().supportFragmentManager
        reserva = Reserva()

        val pagarText = binding.detalleReservaText
        pagarText.text = "Usted va a realizar la reserva: ${reserva}"

        val reservarButton = binding.reservarButton
        reservarButton.setOnClickListener{
            addReserva()
        }
        return binding.root
    }

    private fun addReserva() {
        val cochera: Cochera = args.cochera
        reserva.precio = cochera.price
        reserva.usuarioId = uid.toString()
        reserva.cocheraId = cochera.cocheraId
        reserva.ownerId = cochera.owner
        reserva.fecha = "2023-10-21"

        db.collection("historial")
            .add(reserva)
            .addOnSuccessListener { documentReference ->
                val reservaId = documentReference.id
                reserva.reservaId = reservaId
                Log.e("ReservaCocheraFr", "Reserva Agregada: $reserva")
                db.collection("historial").document(reservaId).set(reserva) // Guarda el objeto cochera en Firestore
                Toast.makeText(requireContext(), "Reserva Agregada: ${reserva.reservaId}", Toast.LENGTH_SHORT).show()
                val navController = binding.root.findNavController()
                navController.popBackStack(R.id.navigation_container, false)
                navController.navigate(R.id.historialFr)
            }
            .addOnFailureListener { e ->
                Log.w("ReservaCocheraFr", "Error al agregar el documento", e)
            }
    }
}