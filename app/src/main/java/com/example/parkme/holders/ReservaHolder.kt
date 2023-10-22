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
        val reservaId: TextView = view.findViewById(R.id.reserva_reservaIdTextView)
        val usuarioId: TextView = view.findViewById(R.id.reserva_usuarioIdTextView)
        val fecha: TextView = view.findViewById(R.id.reserva_fechaTextView)
        val cocheraId: TextView = view.findViewById(R.id.reserva_cocheraIdTextView)
        val ownerId: TextView = view.findViewById(R.id.reserva_ownerIdTextView)
        val precio: TextView = view.findViewById(R.id.reserva_precioTextView)

        reservaId.text = reservaId.toString()
        usuarioId.text = usuarioId.toString()
        fecha.text = fecha.toString()
        cocheraId.text = cocheraId.toString()
        ownerId.text = ownerId.toString()
        precio.text = precio.toString()



    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.card)
    }
}