package com.example.parkme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parkme.Cochera_card_item
import com.example.parkme.R

class Card_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titulo = view.findViewById<TextView>(R.id.tvTituloCard)
    val direccion = view.findViewById<TextView>(R.id.tvDireccionCard)
    val precio = view.findViewById<TextView>(R.id.tvPrecioCard)
    val photo = view.findViewById<ImageView>(R.id.ivCard)

    fun render(cocheraModel: Cochera_card_item) {
        titulo.text = cocheraModel.titulo
        direccion.text = cocheraModel.direccion
        precio.text = cocheraModel.precio
        Glide.with(photo.context).load(cocheraModel.photo).into(photo)
    }
}