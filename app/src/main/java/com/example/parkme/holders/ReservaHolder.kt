package com.example.parkme.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parkme.R
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Pago
import com.example.parkme.entities.Reserva
import com.example.parkme.entities.User

class ReservaHolder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View
    init {
        this.view = v
    }

    fun setCard(reserva: Reserva) {

        val cardReservaId: TextView = view.findViewById(R.id.tvReservaCardAddress)
        val cardPrecio: TextView = view.findViewById(R.id.tvReservaCardTotalPrice)
        val cardPrecioPorHora: TextView = view.findViewById(R.id.tvReservaCardPricePerHour)
        val cardStartTime: TextView = view.findViewById(R.id.tvReservaCardStartTime)
        val cardEndTime: TextView = view.findViewById(R.id.tvReservaCardFinishTime)
        val cardTotalTime: TextView = view.findViewById(R.id.tvReservaCardTotalTime)
        val cardImagen: ImageView = view.findViewById(R.id.imageView6)

        if (reserva.horaSalida == null ){
            cardEndTime.text = "En curso"
            cardTotalTime.text = "En curso"
        } else {
            cardEndTime.text = reserva.horaSalida
            cardTotalTime.text = reserva.horaSalida
        }

        cardReservaId.text = reserva.direccion
        cardPrecio.text = ((reserva.precio)*2).toString()
        cardPrecioPorHora.text = reserva.precio.toString()
        cardStartTime.text = reserva.fecha

        Glide.with(view).load(reserva.urlImage).into(cardImagen)
    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.cardReserva)
    }
}