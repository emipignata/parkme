package com.example.parkme.navigation

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.parkme.databinding.AutocompleteAddressActivityBinding
import com.example.parkme.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import java.util.*

class AgregarCocheraFr : Fragment(R.layout.autocomplete_address_activity),
    OnMapReadyCallback {
    private lateinit var mapPanel: View

    private var mapFragment: SupportMapFragment? = null
    private lateinit var coordinates: LatLng
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var checkProximity = false
    private lateinit var binding: AutocompleteAddressActivityBinding
    private var deviceLocation: LatLng? = null
    private val acceptedProximity = 150.0
    private var startAutocompleteIntentListener = View.OnClickListener { view: View ->
        view.setOnClickListener(null)
        startAutocompleteIntent()
    }

    private val startAutocomplete = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult ->
            binding.autocompleteAddress1.setOnClickListener(startAutocompleteIntentListener)
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    Log.d(TAG, "Place: " + place.addressComponents)
                    fillInAddress(place)
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                Log.i(TAG, "User canceled autocomplete")
            }
        } as ActivityResultCallback<ActivityResult>)

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val apiKey = "AIzaSyDW5u0qurjfVpPY3PVH0yZpauP75T2w1FY"
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }
        binding = AutocompleteAddressActivityBinding.inflate(inflater, container, false)
        binding.autocompleteAddress1.setOnClickListener(startAutocompleteIntentListener)
        val saveButton = binding.autocompleteSaveButton
        saveButton.setOnClickListener { saveForm() }
        return binding.root
    }

    private fun saveForm() {
        Log.d(TAG, "checkProximity = $checkProximity")
        if (checkProximity) {
            checkLocationPermissions()
        } else {
            Toast.makeText(requireContext(), R.string.autocomplete_skipped_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getAndCompareLocations()
        } else {
            requestPermissionLauncher.launch(
                permission.ACCESS_FINE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getAndCompareLocations() {
        val enteredLocation = coordinates
        map!!.isMyLocationEnabled = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener(requireActivity()) { location: Location? ->
                if (location == null) {
                    return@addOnSuccessListener
                }
                deviceLocation = LatLng(location.latitude, location.longitude)
                Log.d(TAG, "device location = " + deviceLocation.toString())
                Log.d(TAG, "entered location = $enteredLocation")
                val distanceInMeters: Double =
                    computeDistanceBetween(deviceLocation, enteredLocation)
                if (distanceInMeters <= acceptedProximity) {
                    Log.d(TAG, "location matched")
                } else {
                    Log.d(TAG, "location not matched")
                }
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
        binding.autocompleteAddress1.setText(address1.toString())
        showMap(place)
    }

    private fun showMap(place: Place) {
        coordinates = place.latLng as LatLng
        mapFragment = childFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment?
        if (mapFragment == null) {
            mapPanel = binding.stubMap.inflate()
            val mapOptions = GoogleMapOptions()
            mapOptions.mapToolbarEnabled(false)
            mapFragment = SupportMapFragment.newInstance(mapOptions)
            childFragmentManager
                .beginTransaction()
                .add(
                    R.id.confirmation_map,
                    mapFragment!!,
                    MAP_FRAGMENT_TAG
                )
                .commit()
            mapFragment!!.getMapAsync(this)
        } else {
            updateMap(coordinates)
        }
    }

    private fun updateMap(latLng: LatLng) {
        marker!!.position = latLng
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        if (mapPanel.visibility == View.GONE) {
            mapPanel.visibility = View.VISIBLE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        try {
            val success = map!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        marker = map!!.addMarker(
            MarkerOptions()
                .position(map!!.cameraPosition.target)
                .draggable(true)   // Marker is draggable
        )
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))

        map!!.setOnCameraMoveListener {
            val center = map!!.cameraPosition.target
            marker!!.position = center
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getAndCompareLocations()
        } else {
            Log.d(TAG, "User denied permission")
        }
    }

    companion object {
        private val TAG = AgregarCocheraFr::class.java.simpleName
        private const val MAP_FRAGMENT_TAG = "MAP"
    }
}