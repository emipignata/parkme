package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.databinding.FragmentEstadoReservaBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Pago
import com.example.parkme.entities.Reserva
import com.example.parkme.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EstadoReservaFr : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val args: EstadoReservaFrArgs by navArgs()
    private lateinit var binding: FragmentEstadoReservaBinding
    private val reserva: Reserva by lazy { args.reserva }
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    private var user: User? = null
    private var cochera: Cochera? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEstadoReservaBinding.inflate(inflater, container, false)

        // Setup listeners and initialize UI that doesn't depend on Firestore data
        setupUI()

        // Fetch and observe Firestore data
        findUser(reserva.ownerId)
        findCochera(reserva.cocheraId)

        return binding.root
    }

    private fun setupUI() {
        binding.pagarReserva.setOnClickListener {
            val pago = Pago(reserva, reserva.ownerId, reserva.fecha, reserva.fecha,
                reserva.fecha, 100.1)
            binding.root.findNavController().navigate(EstadoReservaFrDirections.actionEstadoReservaFrToProductFragment(pago, reserva))
        }

        binding.estadoReservaVolver.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }

        // Non-Firestore-dependent initializations
        binding.DetailOwnerName.text = reserva.ownerId
        binding.DetailDescripcion.text = reserva.fecha
        binding.cantHsDetailPlaceHolder.text = "${reserva.horaEntrada} - ${reserva.horaSalida}"
    }

    private fun findCochera(cocheraId: String) {
        db.collection("cocheras").document(cocheraId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    cochera = documentSnapshot.toObject(Cochera::class.java)
                    cochera?.let {
                        updateCocheraUI(it)
                    }
                } else {
                    Log.e("EstadoReservaFr", "Cochera not found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EstadoReservaFr", "Error getting documents: ", exception)
            }
    }

    private fun updateCocheraUI(cochera: Cochera) {
        binding.DetailLocation.text = cochera.direccion
        binding.DetailName.text = cochera.nombre
        binding.precioPorHoraDetail.text = cochera.price.toString()
    }

    private fun findUser(ownerId: String) {
        db.collection("users").document(ownerId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User::class.java)
                    // User-related UI updates can be done here if needed
                } else {
                    Log.e("EstadoReservaFr", "User not found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EstadoReservaFr", "Error getting documents: ", exception)
            }
    }
}
