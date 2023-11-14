package com.example.parkme.navigation

import MisCocherasFirestoreAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.MainActivity
import com.example.parkme.databinding.FragmentMisCocherasBinding
import com.example.parkme.entities.Cochera
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MisCocherasFr : Fragment() {
    private lateinit var adapter: MisCocherasFirestoreAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!
    private lateinit var binding: FragmentMisCocherasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMisCocherasBinding.inflate(inflater, container, false)
        val query = db.collection("cocheras").whereEqualTo("owner", uid)
        recyclerView = binding.recyclerViewMisCocheras
        val options = FirestoreRecyclerOptions.Builder<Cochera>()
            .setQuery(query, Cochera::class.java)
            .build()
        adapter = MisCocherasFirestoreAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        if (activity is MainActivity) {
            (activity as MainActivity).setBottomNavViewVisibility(View.VISIBLE)
        }

        binding.button3.setOnClickListener {
            val action = MisCocherasFrDirections.actionMisCocherasFrToAgregarCocheraFr()
            binding.root.findNavController().navigate(action)
        }
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
