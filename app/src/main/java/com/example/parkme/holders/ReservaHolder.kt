package com.example.parkme.holders

import android.media.Image
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

    fun setCard(reservaId: String, usuarioId: String, fecha: String, horaEntrada: String, horaSalida: String, precio: Double) {
        val reservaId: TextView = view.findViewById(R.id.tituloTextView)
        val usuarioId: TextView = view.findViewById(R.id.descripcionTextView)
        val img: ImageView = view.findViewById(R.id.imageView)
        Glide.with(view)
            .load("https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg")
            .into(img)

        reservaId.text = reservaId.toString()
        usuarioId.text = usuarioId.toString()


    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.card)
    }
}