package com.example.parkme.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.bumptech.glide.Glide

class ReservaHolder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View
    init {
        this.view = v
    }


    fun setCard(reservaId: String, usuarioId: String, fecha: String, cocheraId: String, ownerId: String, precio: Float) {
        val cardReservaId: TextView = view.findViewById(R.id.reserva_reservaIdTextView)
        val cardUsuarioId: TextView = view.findViewById(R.id.reserva_usuarioIdTextView)
        val cardFecha: TextView = view.findViewById(R.id.reserva_fechaTextView)
        val cardCocheraId: TextView = view.findViewById(R.id.reserva_cocheraIdTextView)
        val cardOwnerId: TextView = view.findViewById(R.id.reserva_ownerIdTextView)
        val cardPrecio: TextView = view.findViewById(R.id.reserva_precioTextView)

        cardReservaId.text = reservaId
        cardUsuarioId.text = usuarioId
        cardFecha.text = fecha
        cardCocheraId.text = cocheraId
        cardOwnerId.text = ownerId
        cardPrecio.text = precio.toString()

    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.cardReserva)
    }
}