package com.example.parkme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.example.parkme.entities.Cochera
import com.example.parkme.holders.CocheraHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CocheraFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<Cochera>) :
    FirestoreRecyclerAdapter<Cochera, CocheraHolder>(options) {

    override fun onBindViewHolder(holder: CocheraHolder, position: Int, model: Cochera) {
        holder.setCard(model.nombre, model.direccion, model.price, model.urlImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocheraHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mis_cocheras_item, parent, false)
        return CocheraHolder(view)
    }
}