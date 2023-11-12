package com.example.parkme.navigation

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.parkme.R
import com.example.parkme.databinding.FragmentExploreMapBinding
import com.example.parkme.entities.Cochera
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExploreFr : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private lateinit var binding: FragmentExploreMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private var currentLocation: LatLng = LatLng(-34.5497616,-58.456725)
    private val db = FirebaseFirestore.getInstance()
    private val cocherasMarker: MutableList<Cochera> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fragmentManager: FragmentManager
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var requestingLocationUpdates: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 5000 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocation(location)
                    if (!requestingLocationUpdates) {
                        mapFragment.getMapAsync(this@ExploreFr)
                    }
                }
            }
        }
    }

    private var startAutocompleteIntentListener = View.OnClickListener { view: View ->
        view.setOnClickListener(null)
        startAutocompleteIntent()
    }

    private val startAutocomplete = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        binding.searchBar.setOnClickListener(startAutocompleteIntentListener)
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val place = Autocomplete.getPlaceFromIntent(intent)
                Log.d(TAG, "Place: " + place.addressComponents)
                fillInAddress(place)
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "User canceled autocomplete")
        }
    }

    private fun fillInAddress(place: Place) {
        val components = place.addressComponents
        val address1 = StringBuilder()
        val postcode = StringBuilder()
        if (components != null) {
            for (component in components.asList()) {
                when (component.types[0]) {
                    "street_number" -> {
                        address1.insert(0, component.name)
                    }
                    "route" -> {
                        address1.append(" ")
                        address1.append(component.shortName)
                    }
                    "postal_code" -> {
                        postcode.insert(0, component.name)
                    }
                    "postal_code_suffix" -> {
                        postcode.append("-").append(component.name)
                    }
                }
            }
        }
        binding.searchBar.clearFocus()
        binding.searchBar.setText(address1.toString())
    }

    private fun startAutocompleteIntent() {
        val fields = listOf(
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.LAT_LNG, Place.Field.VIEWPORT
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setCountries(listOf("AR"))
            //TODO: https://developers.google.com/maps/documentation/places/android-sdk/autocomplete
            .setTypesFilter(listOf(TypeFilter.ADDRESS.toString().lowercase()))
            .build(requireContext())
        startAutocomplete.launch(intent)
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
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    googleMap.isMyLocationEnabled = true
                    currentLocation = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.5f))
                }
            }
        }
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnInfoWindowClickListener(this)

        getCocheras {
            for (marker in cocherasMarker) {
                val location = LatLng(marker.lat, marker.lng)
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(marker.direccion)
                    .snippet("\$ " + marker.price.toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon32))
                    .draggable(true)

                if(marker.available) {
                    markerOptions.visible(true)
                }
                else{
                    markerOptions.visible(false)
                }
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
                        if (cochera.owner != uid) {
                            cocherasMarker.add(cochera)
                        }
                    }
                    callback.invoke() // Call the callback once data is loaded
                } else {
                    Log.e("ExploreFr", "Error getting documents.", task.exception)
                }
            }
    }

    private fun updateLocation(location: android.location.Location) {
        currentLocation = LatLng(location.latitude, location.longitude)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onResume() {
        super.onResume()
        requestingLocationUpdates = true
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        requestingLocationUpdates = false
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
