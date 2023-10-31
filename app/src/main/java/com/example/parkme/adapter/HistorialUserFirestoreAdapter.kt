package com.example.parkme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.parkme.R
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Reserva
import com.example.parkme.holders.ReservaHolder
import com.example.parkme.navigation.HistorialFrDirections
import com.example.parkme.navigation.MisCocherasFrDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
class HistorialUserFirestoreAdapter(options: FirestoreRecyclerOptions<Reserva>) :
    FirestoreRecyclerAdapter<Reserva, ReservaHolder>(options) {
    override fun onBindViewHolder(holder: ReservaHolder, position: Int, model: Reserva) {
        holder.setCard(model.reservaId, model.usuarioId, model.fecha, model.cocheraId, model.ownerId, model.precio)
        holder.itemView.setOnClickListener {
            val action = HistorialFrDirections.actionHistorialFrToEstadoReservaFr(Reserva())
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.historial_item, parent, false)
        return ReservaHolder(view)
    }


}