package com.example.parkme.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth

class ExploreFr : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private lateinit var binding: FragmentExploreMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var currentLocation: LatLng
    private val db = FirebaseFirestore.getInstance()
    private val cocherasMarker: MutableList<Cochera> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fragmentManager: FragmentManager
    private val uid = FirebaseAuth.getInstance().currentUser?.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreMapBinding.inflate(inflater, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fragmentManager = requireActivity().supportFragmentManager

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Set the map type to Normal.
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnInfoWindowClickListener(this)

        currentLocation = LatLng(-33.1301719, -64.34902)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.5f))

        getCocheras {
            for (marker in cocherasMarker) {
                val location = LatLng(marker.lat, marker.lng)
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(marker.nombre)
                    .snippet(marker.direccion)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon32))
                    .draggable(true)
                    .visible(true)

                val googleMarker = googleMap.addMarker(markerOptions)
                if (googleMarker != null) {
                    googleMarker.tag = marker
                }
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val cochera = marker.tag as? Cochera
        if (cochera != null) {
            val action = ExploreFrDirections.actionExploreFrToCocheraDetailFr(cochera)
            view?.findNavController()?.navigate(action)
        } else {
            Toast.makeText(
                requireContext(), "Info window clicked with no associated Cochera",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int

        // Check if a click count was set, then display the click count.
        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                requireContext(),
                "${marker.title} has been clicked $newClickCount times.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }


    private fun getCocheras(callback: () -> Unit) {
        db.collection("cocheras")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cocherasMarker.clear()
                    for (document in task.result) {
                        val cochera = document.toObject(Cochera::class.java)
                        if (cochera.owner!= uid) {
                            cocherasMarker.add(cochera)
                        }
                    }
                    callback.invoke() // Call the callback once data is loaded
                } else {
                    Log.e("ExploreFr", "Error getting documents.", task.exception)
                }
            }
    }
}