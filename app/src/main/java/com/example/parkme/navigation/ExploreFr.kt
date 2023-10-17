package com.example.parkme.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import java.lang.Thread.sleep
import com.google.android.gms.location.LocationServices


class ExploreFr : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentExploreMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private val db = FirebaseFirestore.getInstance()
    private val cocherasMarker: MutableList<Cochera> = ArrayList()
    private val PERMISSIONS_REQUEST_LOCATION = 1001

    private fun checkLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val isPermissionGranted = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireContext(), permission)

        if (isPermissionGranted) {
            getCurrentLocation()
        } else {
            requestLocationPermissionLauncher.launch(permission)
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, you can now get the user's location
            getCurrentLocation()
        } else {
            // Permission denied, handle accordingly
            Log.e("ExploreFr", "Permission Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermission()
        //loadSampleCocheras()
        getCocheras()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getCurrentLocation()
        binding = FragmentExploreMapBinding.inflate(inflater, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        sleep(750)
        mapFragment.getMapAsync(this)
        return binding.root
    }
    private fun getCurrentLocation() {
        // Request the current location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Save the location to the variable
                        currentLocation = LatLng(location.latitude, location.longitude)
                        // Now, you can use the 'currentLocation' variable
                        Log.e("ExploreFr", "CurrentLocationnnn: ${currentLocation}")
                    } else {
                        // Handle the case where location is null
                    }
                }
        }
    }
    private fun loadSampleCocheras() {
        thread {
            deleteAllCocheras()
        }
        thread {
            addSampleCocheras()
        }
    }

    private fun addSampleCocheras() {
        val cocheras = listOf(
            Cochera("Pedro1", "Libertador 123455", -33.13017, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", false, "user1"),
            Cochera("Pedro2", "Libertador 123456", -33.1245077, -64.34903, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", true, "user2"),
            Cochera("Pedro3", "Libertador 123457", -33.13019, -64.34901, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", false, "user3"),
            Cochera("Pedro4", "Libertador 123458", -33.13020, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", true, "user4"),
            Cochera("Pedro5", "Libertador 123459", -33.13021, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", false, "user5"),
            Cochera("Pedro6", "Libertador 123450", -33.13022, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", true,"user6")
        )

        for (cochera in cocheras) {
            db.collection("cocheras")
                .add(cochera)
                .addOnSuccessListener { documentReference ->
                    //Log.e("ExploreFr", "Cochera Agregada: $cochera")
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

    override fun onMapReady(googleMap: GoogleMap) {
        // Set the map type to Normal.
        // Add custom markers to the map.
        var count = 0
        Log.e("ExploreFr", "Cocheras: ${cocherasMarker.size}")
        for (marker in cocherasMarker) {
            count++
            val location = LatLng(marker.lat, marker.lng)
            val markerOptions = MarkerOptions()
                .position(location)
                .title(marker.nombre)
                .snippet(marker.direccion)
                .draggable(true)
                .visible(true)
            Log.e("ExploreFr", "MarkerOptions $count: ${markerOptions.title}")
            googleMap.addMarker(markerOptions)
        }
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = true


        // Set the initial camera position (e.g., center of the city).

        if (currentLocation != null) {
            Log.e("ExploreFr", "CurrentLocationObtained: ${currentLocation}")
        } else {
            currentLocation = LatLng(-33.1301719, -64.34902)
            Log.e("ExploreFr", "CurrentLocationForced: ${currentLocation}")
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f))

    }

    private fun getCocheras() {
        db.collection("cocheras")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cocherasMarker.clear()
                    for (document in task.result) {
                        val cochera = document.toObject(Cochera::class.java)
                        cocherasMarker.add(cochera)
                    }
                } else {
                    Log.e("ExploreFr", "Error getting documents.", task.exception)
                }
            }
    }
}
