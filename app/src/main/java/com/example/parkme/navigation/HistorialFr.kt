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
import com.example.parkme.models.ItemHistorial


class HistorialFr : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHistorial)

        // Crea una lista de elementos (puedes reemplazar esto con tus datos)
        val items = listOf(
            ItemHistorial("Título 1", "Descripción 1", R.drawable.cochera),
            ItemHistorial("Título 2", "Descripción 2", R.drawable.cochera),
            ItemHistorial("Título 3", "Descripción 3", R.drawable.cochera),
            ItemHistorial("Título 4", "Descripción 4", R.drawable.cochera),
            ItemHistorial("Título 5", "Descripción 5", R.drawable.cochera),
            ItemHistorial("Título 6", "Descripción 6", R.drawable.cochera),
            ItemHistorial("Título 7", "Descripción 7", R.drawable.cochera),
            ItemHistorial("Título 8", "Descripción 8", R.drawable.cochera),
            ItemHistorial("Título 9", "Descripción 9", R.drawable.cochera),
            ItemHistorial("Título 10", "Descripción 10", R.drawable.cochera)
        )
        // Crea un adaptador personalizado y configura la RecyclerView
        val adapter = MyAdapterHistorial(items)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }
}
