package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.parkme.databinding.FragmentExploreMapBinding
import com.example.parkme.entities.Cochera
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.concurrent.thread
import com.example.parkme.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class ExploreFr : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentExploreMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var currentLocation: LatLng
    private val db = FirebaseFirestore.getInstance()
    private val cocherasMarker: MutableList<Cochera> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //loadSampleCocheras()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreMapBinding.inflate(inflater, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Set the map type to Normal.
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        currentLocation = LatLng(-33.1301719, -64.34902)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.5f))

        getCocheras {
            for (marker in cocherasMarker) {
                val location = LatLng(marker.getLat(), marker.getLng())
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(marker.getNombre())
                    .snippet(marker.getDireccion())
                    .draggable(true)
                    .visible(true)
                googleMap.addMarker(markerOptions)
            }
        }
    }

    private fun loadSampleCocheras() {
        /*thread {
            deleteAllCocheras()
        }*/
        thread {
            addSampleCocheras()
        }
    }

    private fun addSampleCocheras() {
        val cocheras = listOf(
            Cochera("Pedro1", "Libertador 123455", -33.13017, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", false, "user1"),
            Cochera("Pedro2", "Libertador 123456", -33.1245077, -64.34903, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", true, "user2"),
            // Add more cocheras here
        )

        for (cochera in cocheras) {
            db.collection("cocheras")
                .add(cochera)
                .addOnSuccessListener { documentReference ->
                    Log.e("ExploreFr", "Cochera Agregada: $cochera")
                }
                .addOnFailureListener { e -> Log.w("ExploreFr", "Error adding document", e) }
        }
    }

    private fun deleteAllCocheras() {
        db.collection("cocheras")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        db.collection("cocheras").document(document.id).delete()
                    }
                } else {
                    Log.e("ExploreFr", "Error getting documents.", task.exception)
                }
            }
    }

    private fun getCocheras(callback: () -> Unit) {
        db.collection("cocheras")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cocherasMarker.clear()
                    for (document in task.result) {
                        val cochera = document.toObject(Cochera::class.java)
                        cocherasMarker.add(cochera)
                    }
                    callback.invoke() // Call the callback once data is loaded
                } else {
                    Log.e("ExploreFr", "Error getting documents.", task.exception)
                }
            }
    }
}
