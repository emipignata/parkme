package com.example.parkme.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.adapter.CocheraFirestoreRecyclerAdapter
import com.example.parkme.entities.Cochera
import com.example.parkme.viewmodels.MisCocherasViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.parkme.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class MisCocherasFr : Fragment() {
    private lateinit var adapter: CocheraFirestoreRecyclerAdapter
    private lateinit var viewModel: MisCocherasViewModel
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_cocheras, container, false)
        val query = db.collection("cocheras").whereEqualTo("owner", uid)
        recyclerView = view.findViewById(R.id.recyclerViewMisCocheras)

        val options = FirestoreRecyclerOptions.Builder<Cochera>()
            .setQuery(query, Cochera::class.java)
            .build()
        viewModel = ViewModelProvider(this).get(MisCocherasViewModel::class.java)
        adapter = CocheraFirestoreRecyclerAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val button3 = view.findViewById<Button>(R.id.button3)

        button3.setOnClickListener {
            val action = MisCocherasFrDirections.actionMisCocherasFrToFragmentAgregarCochera()
            view.findNavController().navigate(action)
        }
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
