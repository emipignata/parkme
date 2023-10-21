package com.example.parkme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.parkme.R
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Reserva
import com.example.parkme.holders.CocheraHolder
import com.example.parkme.holders.ReservaHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
class ReservaFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<Reserva>) :
    FirestoreRecyclerAdapter<Reserva, ReservaHolder>(options) {
    override fun onBindViewHolder(holder: ReservaHolder, position: Int, model: Reserva) {
        holder.setCard(model.reservaId, model.usuarioId, model.fecha, model.horaEntrada, model.horaSalida, model.precio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_historial, parent, false)
        return ReservaHolder(view)
    }
}