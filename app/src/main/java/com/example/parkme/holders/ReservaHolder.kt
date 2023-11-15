package com.example.parkme.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parkme.R
import com.example.parkme.entities.Reserva
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReservaHolder(v: View) : RecyclerView.ViewHolder(v) {
    private var view: View = v

    fun setCard(reserva: Reserva) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        val cardReservaId: TextView = view.findViewById(R.id.tvReservaCardAddress)
        val cardPrecioTotal: TextView = view.findViewById(R.id.tvReservaCardTotalPrice)
        val cardPrecioPorHora: TextView = view.findViewById(R.id.tvReservaCardPricePerHour)
        val cardStartTime: TextView = view.findViewById(R.id.tvReservaCardStartTime)
        val cardEndTime: TextView = view.findViewById(R.id.tvReservaCardFinishTime)
        val cardTotalTime: TextView = view.findViewById(R.id.tvReservaCardTotalTime)
        val cardImagen: ImageView = view.findViewById(R.id.imageView6)
        val cardOwnerName: TextView = view.findViewById(R.id.tvReservaCardNombreDueno)

        reserva.fechaEntrada?.toDate()?.let { start ->
            reserva.fechaSalida?.toDate()?.let { end ->
                val duration = end.time - start.time
                val hours = TimeUnit.MILLISECONDS.toHours(duration)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(hours)
                cardTotalTime.text = "Hs Totales: $hours Hrs $minutes Min"

                // Calculate the price
                val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                val price = calculatePrice(totalMinutes, reserva.precioPorHora)
                cardPrecioTotal.text = "$ Total: $price"
            } ?: run {
                cardTotalTime.text = "En curso"
                cardPrecioTotal.text = "$ Total: En curso"
            }
        }





        reserva.fechaSalida?.toDate()?.let { date ->
            cardEndTime.text = "Salida: ${dateFormat.format(date)}Hs"
        } ?: run {
            cardEndTime.text = "En curso"
        }

        cardStartTime.text = "Entrada: ${dateFormat.format(reserva.fechaEntrada?.toDate())}Hs"
        cardReservaId.text = reserva.direccion
        cardPrecioPorHora.text = "$/Hs: ${reserva.precioPorHora}"
        cardOwnerName.text = reserva.ownerName
        Glide.with(view).load(reserva.urlImage).into(cardImagen)
    }

    private fun calculatePrice(totalMinutes: Long, pricePerHour: Float): Float {
        if (totalMinutes <= 60) {
            return pricePerHour
        } else {
            val hours = totalMinutes / 60
            val additionalMinutes = totalMinutes % 60
            val additionalCharge = if (additionalMinutes > 0) {
                pricePerHour / 4 * ((additionalMinutes - 1) / 15 + 1)
            } else {
                0f
            }
            return pricePerHour * hours + additionalCharge
        }
    }


    fun getCardLayout(): CardView {
        return view.findViewById(R.id.cardReserva)
    }
}
