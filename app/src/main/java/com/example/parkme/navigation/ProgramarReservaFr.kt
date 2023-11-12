package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.R
import com.example.parkme.databinding.FragmentProgramarReservaBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Reserva
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProgramarReservaFr : Fragment() {
    private lateinit var binding : FragmentProgramarReservaBinding
    val args: CocheraDetailUserFrArgs by navArgs()
    private val cochera: Cochera by lazy { args.cochera }
    private lateinit var reserva: Reserva
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private lateinit var DateSelected : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProgramarReservaBinding.inflate(inflater, container, false)
        binding.etProgramarReservaFechaDesde.setOnClickListener {
            showDatePickerDialog()
        }
        binding.etProgramarReservaHoraDesde.setOnClickListener {
            showTimePickerDialog()
        }

        binding.btnProgramarReservaConfirmar.setOnClickListener {
            confirmarReserva()
        }
        return binding.root
    }

    private fun showDatePickerDialog() {
     val datePicker = DatePickerFr { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFr { onTimeSelected(it) }
        timePicker.show(childFragmentManager, "timePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        DateSelected =  String.format("%02d/%02d/%d", day, month, year)
        binding.etProgramarReservaFechaDesde.setText("$day/$month/$year")
    }

    private fun onTimeSelected(time: String) {
        binding.etProgramarReservaHoraDesde.setText(time)
    }

    private fun confirmarReserva() {
        val navController = binding.root.findNavController()
        navController.popBackStack(R.id.navigation_container, false)
        navController.navigate(R.id.historialFr)
    }

    private fun addReserva() {
        val cochera: Cochera = args.cochera
        reserva.precio = cochera.price
        reserva.usuarioId = uid.toString()
        reserva.cocheraId = cochera.cocheraId
        reserva.ownerId = cochera.owner
        reserva.fecha = DateSelected
        reserva.horaEntrada = ""
        reserva.horaSalida = ""
        reserva.direccion = cochera.direccion
        reserva.urlImage = cochera.urlImage
        reserva.ownerName = cochera.ownerName
        reserva.estado = "Reservada"

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
                db.collection("cocheras").document(cochera.cocheraId)
                    .set(cochera)
            }
            .addOnFailureListener { e ->
                Log.w("ReservaCocheraFr", "Error al agregar el documento", e)
            }
    }

}