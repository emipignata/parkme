package com.example.parkme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.example.parkme.Cochera_card_item
class Card_Adapter(private val cardCocheraList: List<Cochera_card_item>) :
    RecyclerView.Adapter<Card_ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Card_ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Card_ViewHolder(layoutInflater.inflate(R.layout.cochera_item, parent, false))
    }

    override fun onBindViewHolder(holder: Card_ViewHolder, position: Int) {
        val item = cardCocheraList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = cardCocheraList.size
}
