package com.example.parkme.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.parkme.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.adapter.MyAdapterHistorial


class HistorialFr : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHistorial)

        // Crea una lista de elementos (puedes reemplazar esto con tus datos)
        val items = listOf("Elemento 1", "Elemento 2", "Elemento 3","Elemento 4", "Elemento 5", "Elemento 6","Elemento 7", "Elemento 8", "Elemento 9","Elemento 10")

        // Crea un adaptador personalizado y configura la RecyclerView
        val adapter = MyAdapterHistorial(items)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }
}
