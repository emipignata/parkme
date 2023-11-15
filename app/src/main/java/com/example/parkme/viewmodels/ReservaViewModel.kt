package com.example.parkme.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parkme.entities.Reserva
import com.example.parkme.entities.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ReservaViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _reservaState = MutableLiveData<Reserva>()
    val reservaState: LiveData<Reserva> = _reservaState

    fun updateReservaState(reserva: Reserva, newState: String) {
        val currentTimestamp = Timestamp.now()
        reserva.fechaSalida = currentTimestamp
        reserva.estado = newState
        val updates = mapOf(
            "estado" to newState,
            "fechaSalida" to currentTimestamp
        )
        db.collection("historial").document(reserva.reservaId)
            .update(updates)
            .addOnSuccessListener {
                _reservaState.value = reserva
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    fun getUserState(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.let { onSuccess(it) }
                } else {
                    // Handle case where user does not exist
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener(onFailure)
    }

    fun updateUserState(userId: String, reserva: Reserva, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val updates = when (reserva.estado) {
            "Reservada" -> mapOf("reservaInCheckIn" to reserva.reservaId, "reservaInReservada" to "")
            "CheckIn" -> mapOf("reservaInCheckOut" to reserva.reservaId, "reservaInCheckIn" to "")
            else -> return
        }
        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onFailure)
    }
}