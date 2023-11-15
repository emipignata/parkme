package com.example.parkme.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.bumptech.glide.Glide

class CocheraHolder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View

    init {
        this.view = v
    }

    fun setCard(title: String, address: String, price: Float, img: String,) {
        val cardTitle: TextView = view.findViewById(R.id.misCocherasViewTitle)
        val cardAddress: TextView = view.findViewById(R.id.misCocherasAddress)
        val cardPrice: TextView = view.findViewById(R.id.misCocherasPrice)
        val cardImg: ImageView = view.findViewById(R.id.misCocherasViewImg)
        Glide.with(view)
            .load(img)
            .into(cardImg)

        cardTitle.text = title
        cardAddress.text = address
        cardPrice.text = price.toString()
    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.card)
    }
}