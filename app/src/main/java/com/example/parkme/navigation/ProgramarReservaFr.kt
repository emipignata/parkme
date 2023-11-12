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
import com.example.parkme.entities.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProgramarReservaFr : Fragment() {
    private lateinit var binding : FragmentProgramarReservaBinding
    private val args: CocheraDetailUserFrArgs by navArgs()
    private val cochera: Cochera by lazy { args.cochera }
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProgramarReservaBinding.inflate(inflater, container, false)
        getUserState()
        return binding.root
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val datePicker = DatePickerFr { day, month, year ->
            onDateSelected(day, month, year, isStartDate)
        }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val timePicker = TimePickerFr { time ->
            onTimeSelected(time, isStartTime)
        }
        timePicker.show(childFragmentManager, "timePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int, isStartDate: Boolean) {
        val formattedDate = String.format("%02d/%02d/%d", day, month, year)
        if (isStartDate) {
            binding.etProgramarReservaFechaDesde.setText(formattedDate)
        } else {
            binding.etProgramarReservaFechaHasta.setText(formattedDate)
        }
    }

    private fun onTimeSelected(time: String, isStartTime: Boolean) {
        if (isStartTime) {
            binding.etProgramarReservaHoraDesde.setText(time)
        } else {
            binding.etProgramarReservaHoraHasta.setText(time)
        }
    }

    private fun confirmarReserva() {
        val reserva = Reserva(
            "",
            uid.toString(),
            Timestamp.now().toDate().toString(),
            cochera.cocheraId,
            cochera.owner,
            "Reservada",
            cochera.price,
            binding.etProgramarReservaFechaDesde.text.toString(),
            binding.etProgramarReservaHoraDesde.text.toString(),
            binding.etProgramarReservaFechaHasta.text.toString(),
            binding.etProgramarReservaHoraHasta.text.toString(),
            cochera.direccion,
            cochera.urlImage,
            cochera.ownerName
        )

        db.collection("historial")
            .add(reserva)
            .addOnSuccessListener { documentReference ->
                val reservaId = documentReference.id
                reserva.reservaId = reservaId
                Log.e("ProgramarReserva", "Reserva Agregada: $reserva")
                db.collection("historial").document(reservaId).set(reserva) // Guarda el objeto cochera en Firestore
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
        if (user.reservaInReservada != "") {
            binding.btnProgramarReservaConfirmar.isEnabled = false
            binding.btnProgramarReservaConfirmar.text = getString(R.string.ya_tiene_una_reserva)
            binding.etProgramarReservaFechaDesde.isEnabled = false
            binding.etProgramarReservaFechaHasta.isEnabled = false
            binding.etProgramarReservaHoraDesde.isEnabled = false
            binding.etProgramarReservaHoraHasta.isEnabled = false

        } else {
            binding.btnProgramarReservaConfirmar.isEnabled = true
            binding.btnProgramarReservaConfirmar.text = getString(R.string.confirmar_reserva)
            binding.etProgramarReservaFechaDesde.isEnabled = true
            binding.etProgramarReservaFechaHasta.isEnabled = true
            binding.etProgramarReservaHoraDesde.isEnabled = true
            binding.etProgramarReservaHoraHasta.isEnabled = true
            binding.etProgramarReservaFechaDesde.setOnClickListener {
                showDatePicker(isStartDate = true)
            }

            binding.etProgramarReservaFechaHasta.setOnClickListener {
                showDatePicker(isStartDate = false)
            }

            binding.etProgramarReservaHoraDesde.setOnClickListener {
                showTimePicker(isStartTime = true)
            }

            binding.etProgramarReservaHoraHasta.setOnClickListener {
                showTimePicker(isStartTime = false)
            }

            binding.btnProgramarReservaConfirmar.setOnClickListener {
                confirmarReserva()
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
                        user.reservaInReservada = reserva.reservaId
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