package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.R
import com.example.parkme.databinding.FragmentEstacionarAhoraBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Reserva
import com.example.parkme.entities.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EstacionarAhoraFr() : Fragment() {
    val args: CocheraDetailUserFrArgs by navArgs()
    private lateinit var binding: FragmentEstacionarAhoraBinding
    private lateinit var fragmentManager: FragmentManager
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var user: User
    private lateinit var reserva: Reserva

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEstacionarAhoraBinding.inflate(inflater, container, false)
        fragmentManager = requireActivity().supportFragmentManager
        getUserState()
        return binding.root
    }

    private fun addReserva() {
        val cochera: Cochera = args.cochera
        reserva.precioPorHora = cochera.price
        reserva.usuarioId = uid.toString()
        reserva.cocheraId = cochera.cocheraId
        reserva.ownerId = cochera.owner
        reserva.fechaCreacion = Timestamp.now()
        reserva.fechaEntrada = Timestamp.now()
        reserva.fechaSalida = null
        reserva.direccion = cochera.direccion
        reserva.urlImage = cochera.urlImage
        reserva.ownerName = cochera.ownerName
        reserva.estado = "CheckIn"

        db.collection("historial")
            .add(reserva)
            .addOnSuccessListener { documentReference ->
                val reservaId = documentReference.id
                reserva.reservaId = reservaId
                db.collection("historial").document(reservaId).set(reserva)
                Toast.makeText(requireContext(), "Reserva Agregada", Toast.LENGTH_SHORT).show()
                val navController = binding.root.findNavController()
                navController.popBackStack(R.id.navigation_container, false)
                navController.navigate(R.id.historialFr)
                db.collection("cocheras").document(cochera.cocheraId)
                    .set(cochera)
                setUserState(reserva)
            }
            .addOnFailureListener { e ->
                Log.w("ReservaCocheraFr", "Error al agregar el documento", e)
            }
    }

    private fun setBinding() {
        reserva = Reserva()
        if (user.reservaInCheckIn != "") {
            binding.estacionarAhoraButton.isEnabled = false
            binding.estacionarAhoraButton.text = getString(R.string.ya_tiene_una_reserva_en_curso)
        } else {
            binding.estacionarAhoraButton.isEnabled = true
            binding.estacionarAhoraButton.text = getString(R.string.estacionar_ahora)
            binding.estacionarAhoraButton.setOnClickListener{
                addReserva()
            }
        }
    }

    private fun getUserState() {
        if (uid != null) {
            db.collection("users").document(uid)
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

    private fun setUserState(reserva: Reserva) {
        if (uid != null) {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user = document.toObject(User::class.java)!!
                        user.reservaInCheckIn = reserva.reservaId
                        db.collection("users").document(uid).set(user)
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