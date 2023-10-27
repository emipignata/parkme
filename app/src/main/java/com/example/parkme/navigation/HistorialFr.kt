package com.example.parkme.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.example.parkme.adapter.HistorialUserFirestoreAdapter
import com.example.parkme.entities.Reserva
import com.example.parkme.viewmodels.HistorialReservasViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistorialFr : Fragment() {
    private lateinit var adapter: HistorialUserFirestoreAdapter
    private lateinit var viewModel: HistorialReservasViewModel
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial_reservas, container, false)
        //val query = db.collection("historial").whereEqualTo("usuarioId", uid)
        val query = db.collection("historial").whereEqualTo("usuarioId", uid)
        recyclerView = view.findViewById(R.id.recyclerViewHistorialReservas)
        val options = FirestoreRecyclerOptions.Builder<Reserva>()
            .setQuery(query, Reserva::class.java)
            .build()
        viewModel = ViewModelProvider(this).get(HistorialReservasViewModel::class.java)
        adapter = HistorialUserFirestoreAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
