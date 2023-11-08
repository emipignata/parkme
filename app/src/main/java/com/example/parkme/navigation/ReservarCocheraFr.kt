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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

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

        val pagarText = binding.root.findViewById<TextView>(R.id.detalleReservaText)
        pagarText.text = "Usted va a realizar la reserva: ${reserva}"

        val reservarButton = binding.root.findViewById<Button>(R.id.reservarButton)
        reservarButton.setOnClickListener{
            addReserva()
        }
        return binding.root
    }


    fun extractHour(dateString: String): String {
        // Define the original pattern of the date string
        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

        // Parse the date string into a Date object
        val date = originalFormat.parse(dateString)

        // Define the new pattern to extract just the hour
        val hourFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

        // Return the formatted hour
        return hourFormat.format(date)
    }

    fun extractDate(dateString: String): String {
        // Define the original pattern of the date string
        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

        // Parse the date string into a Date object
        val date = originalFormat.parse(dateString)

        // Define the new pattern to extract just the hour
        val hourFormat = SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH)

        // Return the formatted hour
        return hourFormat.format(date)
    }

    private fun addReserva() {
        val cochera: Cochera = args.cochera
        reserva.precio = cochera.price
        reserva.usuarioId = uid.toString()
        reserva.cocheraId = cochera.cocheraId
        reserva.ownerId = cochera.owner
        reserva.fecha = extractDate(Timestamp.now().toDate().toString())
        reserva.horaEntrada = extractHour(Timestamp.now().toDate().toString())
        reserva.horaSalida = "0"
        reserva.direccion = cochera.direccion
        reserva.urlImage = cochera.urlImage
        reserva.ownerName = cochera.ownerName
        reserva.estado = "CheckIn"

        db.collection("historial")
            .add(reserva)
            .addOnSuccessListener { documentReference ->
                val reservaId = documentReference.id
                reserva.reservaId = reservaId
                Log.e("ReservaCocheraFr", "Reserva Agregada: $reserva")
                db.collection("historial").document(reservaId).set(reserva) // Guarda el objeto cochera en Firestore
                Toast.makeText(requireContext(), "Reserva Agregada", Toast.LENGTH_SHORT).show()
                val navController = binding.root.findNavController()
                navController.popBackStack(R.id.navigation_container, false)
                navController.navigate(R.id.historialFr)
            }
            .addOnFailureListener { e ->
                Log.w("ReservaCocheraFr", "Error al agregar el documento", e)
            }
    }
}