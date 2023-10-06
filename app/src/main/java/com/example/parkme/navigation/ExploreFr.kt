package com.example.parkme.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.example.parkme.adapter.ParkSlotsAdapter
import com.example.parkme.database.backend
import com.example.parkme.database.PaginateResponse
import com.example.parkme.models.ParkSlot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreFr : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_explore, container, false)
        initRecyclerView(rootView)
        return rootView
    }

    private fun initRecyclerView(rootView: View) {
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        var service = backend.service.listParkSlots().enqueue(object :
            Callback<PaginateResponse<ParkSlot>> {
            override fun onResponse(call: Call<PaginateResponse<ParkSlot>>, response: Response<PaginateResponse<ParkSlot>>) {
                if (response.isSuccessful) {
                    val responseParkSlots = response.body()
                    val parkSlotsAdapter = ParkSlotsAdapter(responseParkSlots?.results ?: emptyList()) { parkSlot ->
                        Toast
                            .makeText(requireContext(), parkSlot.Titulo, Toast.LENGTH_SHORT)
                            .show()
                    }
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = parkSlotsAdapter
                }
            }

            override fun onFailure(call: retrofit2.Call<PaginateResponse<ParkSlot>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}