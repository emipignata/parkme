package com.example.parkme.viewmodels

import androidx.lifecycle.ViewModel
import com.example.parkme.entities.Reserva

class HistorialReservasViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    var reservas : MutableList<Reserva> = ArrayList<Reserva>()

}