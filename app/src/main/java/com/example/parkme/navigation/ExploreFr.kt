package com.example.parkme.navigation

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.databinding.FragmentExploreMapBinding
import com.example.parkme.entities.Cochera
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.example.parkme.R
import com.example.parkme.entities.User
import com.example.parkme.viewmodels.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson

class ExploreFr : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private lateinit var binding: FragmentExploreMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var currentLocation: LatLng
    private val db = FirebaseFirestore.getInstance()
    private val cocherasMarker: MutableList<Cochera> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cocherasLoadedCallback: (() -> Unit)? = null
    private lateinit var user: User
    private lateinit var userViewModel: UserViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

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
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (mapFragment == null) {
            // Initialize mapFragment only if it's null
            mapFragment = SupportMapFragment()
        }
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onPause() {
        super.onPause()

        // Remove the mapFragment to avoid potential issues
        mapFragment?.let {
            childFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.setOnInfoWindowClickListener(this)

        currentLocation = LatLng(-33.1301719, -64.34902)
        requireActivity().runOnUiThread{
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.5f))
        }
        loadCocheraMarkers(googleMap)
    }

    private fun loadCocheraMarkers(googleMap: GoogleMap) {
        getCocheras {
            requireActivity().runOnUiThread{
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
    }

    fun setOnCocherasLoadedCallback(callback: () -> Unit) {
        cocherasLoadedCallback = callback
    }

    override fun onInfoWindowClick(marker: Marker) {
        if (::user.isInitialized) {
            val cochera = marker.tag as? Cochera
            if (cochera != null) {
                val action = ExploreFrDirections.actionExploreFrToCocheraDetailFr(cochera, user)
                view?.findNavController()?.navigate(action)
            } else {
                Toast.makeText(
                    requireContext(), "Info window clicked with no associated Cochera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Handle the case where user is not initialized
            Toast.makeText(
                requireContext(), "User is not initialized",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val clickCount = marker.tag as? Int
        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                requireContext(),
                "${marker.title} has been clicked $newClickCount times.",
                Toast.LENGTH_SHORT
            ).show()
        }
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
                        cocherasMarker.add(cochera)
                    }
                    requireActivity().runOnUiThread{
                        callback.invoke()
                    }
                } else {
                    Log.e("ExploreFr", "Error getting documents.", task.exception)
                }
            }
    }
}
