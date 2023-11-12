package com.example.parkme.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parkme.R
import com.example.parkme.entities.Reserva


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
        val cardOwnerName: TextView =  view.findViewById((R.id.tvReservaCardNombreDueno))

        if (reserva.horaSalida == "0" ){
            cardEndTime.text = "En curso"
            cardTotalTime.text = "En curso"
        } else {
            cardEndTime.text = "Hs Salida: " + reserva.horaSalida + "Hs"
            cardTotalTime.text = "Hs Totales: " + reserva.horaSalida + "Hs"
        }

        cardReservaId.text = reserva.direccion
        cardPrecio.text = "$ Total: " + ((reserva.precio)*2).toString()
        cardPrecioPorHora.text = "$/Hs: " + reserva.precio.toString()
        cardStartTime.text = "Hs Entrada: " + reserva.horaEntrada + "Hs"
        cardOwnerName.text = reserva.ownerName

        Glide.with(view).load(reserva.urlImage).into(cardImagen)
    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.cardReserva)
    }
}