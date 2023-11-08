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
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Pago
import com.example.parkme.entities.Reserva
import com.example.parkme.entities.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class EstadoReservaFr : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val args: EstadoReservaFrArgs by navArgs()
    private lateinit var binding: FragmentEstadoReservaBinding
    private val reserva: Reserva by lazy { args.reserva } // Use lazy initialization
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEstadoReservaBinding.inflate(inflater, container, false)
        val botonPagar: Button = binding.pagarReserva
        val botonVolver: Button = binding.estadoReservaVolver
        Log.e("Estado Resservaaaaaaaaaaaa: ", reserva.ownerId)
        findUser(reserva.ownerId)


        botonVolver.setOnClickListener{
            binding.root.findNavController().navigateUp()

        }
        botonPagar.setOnClickListener{
            val pago = Pago(reserva,reserva.ownerId,reserva.fecha, Timestamp.now(),
                Timestamp.now(),100.1)
            binding.root.findNavController().navigate(EstadoReservaFrDirections.actionEstadoReservaFrToCheckoutActivity(pago))
        }


        return binding.root
    }

    private fun findUser(ownerId: String) {
        val ownerId = ownerId
        val usersCollectionRef = db.collection("users")

        usersCollectionRef.document(ownerId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // The user document exists
                    // Here you can convert the documentSnapshot to your User model object
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.let { Log.e("logUser :", it.userId) }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR:" , "No funciono como se esperaba")
            }
    }
}