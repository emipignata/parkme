package com.example.parkme.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.adapter.HistorialUserFirestoreAdapter
import com.example.parkme.databinding.FragmentReservasActivasBinding
import com.example.parkme.entities.Reserva
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReservasActivasFr : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!
    private lateinit var adapter: HistorialUserFirestoreAdapter
    private lateinit var binding: FragmentReservasActivasBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReservasActivasBinding.inflate(inflater, container, false)
        val query = db.collection("historial")
            .whereEqualTo("usuarioId", uid)
            .whereEqualTo("estado", "CheckIn")

        recyclerView = binding.reservasActivasRecyclerView
        val options = FirestoreRecyclerOptions.Builder<Reserva>()
            .setQuery(query, Reserva::class.java)
            .build()
        adapter = HistorialUserFirestoreAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return binding.root
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