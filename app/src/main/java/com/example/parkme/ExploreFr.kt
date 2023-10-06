package com.example.parkme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.adapter.Card_Adapter
import com.example.parkme.adapter.ParkSlotsAdapter
import com.example.parkme.database.ParkSlotDbClient
import com.example.parkme.database.ParkSlotDbResult
import com.example.parkme.services.ParkSlotDbService
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
        var service = ParkSlotDbClient.service.listParkSlots().enqueue(object :
            Callback<ParkSlotDbResult> {
            override fun onResponse(call: Call<ParkSlotDbResult>, response: Response<ParkSlotDbResult>) {
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

            override fun onFailure(call: retrofit2.Call<ParkSlotDbResult>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}