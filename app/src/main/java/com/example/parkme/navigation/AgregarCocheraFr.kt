package com.example.parkme.navigation

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.parkme.R
import com.example.parkme.databinding.FragmentAgregarCocheraMapBinding
import com.example.parkme.entities.Cochera
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import java.util.*

class AgregarCocheraFr : Fragment(R.layout.fragment_agregar_cochera_map),
    OnMapReadyCallback {
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: FragmentAgregarCocheraMapBinding
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var placesClient: PlacesClient
    private lateinit var mapPanel: View
    private var mapFragment: SupportMapFragment? = null
    private lateinit var coordinates: LatLng
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var checkProximity = false
    private var deviceLocation: LatLng? = null
    private val acceptedProximity = 150.0
    private var startAutocompleteIntentListener = View.OnClickListener { view: View ->
        view.setOnClickListener(null)
        startAutocompleteIntent()
    }

    companion object {
        private val TAG = AgregarCocheraFr::class.java.simpleName
        private const val MAP_FRAGMENT_TAG = "MAP"
    }

    private val startAutocomplete = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        binding.autocompleteAddress1.setOnClickListener(startAutocompleteIntentListener)
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val place = Autocomplete.getPlaceFromIntent(intent)
                Log.d(TAG, "Place: " + place.addressComponents)
                fillInAddress(place)
            }
        } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
            Log.i(TAG, "User canceled autocomplete")
        }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.GOOGLE_MAPS_API_KEY))
        }
        placesClient = Places.createClient(requireContext())
    }

    private fun saveForm() {
        Log.d(TAG, "checkProximity = $checkProximity")
        if (checkProximity) {
            checkLocationPermissions()
        } else {
            Toast.makeText(requireContext(), R.string.autocomplete_skipped_message, Toast.LENGTH_SHORT).show()
        }
        val nombreCochera = binding.autocompletePostal.text.toString()
        val precioPorHora = binding.autocompleteCountry.text.toString()
        val direccion = binding.autocompleteAddress1.text.toString()
        val descripcion = binding.autocompleteAddress2.text.toString()
        val disponibilidad = binding.autocompleteCity.text.toString()


        if (uid != null) {
            val cochera = Cochera(
                "",
                nombreCochera,
                direccion,
                -33.13017, // Latitud (cambia a la latitud correcta)
                -64.34902, // Longitud (cambia a la longitud correcta)
                precioPorHora.toFloatOrNull() ?: 0.0f,
                "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", // URL de imagen (cambia a la URL correcta)
                disponibilidad,
                uid // Usuario (cambia al usuario correcto)
            )
            db.collection("cocheras")
                .add(cochera)
                .addOnSuccessListener { documentReference ->
                    val cocheraId = documentReference.id
                    cochera.cocheraId = cocheraId

                    Log.e("ExploreFr", "Cochera Agregada: $cochera")
                    db.collection("cocheras").document(cocheraId).set(cochera) // Guarda el objeto cochera en Firestore
                    Toast.makeText(requireContext(), "Cochera Agregada: ${cochera.cocheraId}", Toast.LENGTH_SHORT).show()
                    val navController = binding.root.findNavController()
                    navController.popBackStack(R.id.navigation_container, false)
                    navController.navigate(R.id.misCocherasFr)
                }
                .addOnFailureListener { e ->
                    Log.w("ExploreFr", "Error al agregar el documento", e)
                }
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

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener(requireActivity()) { location: Location? ->
                if (location == null) {
                    return@addOnSuccessListener
                }
                deviceLocation = LatLng(location.latitude, location.longitude)
                Log.d(TAG, "device location = " + deviceLocation.toString())
                Log.d(TAG, "entered location = $enteredLocation")
                val distanceInMeters: Double =
                    SphericalUtil.computeDistanceBetween(deviceLocation, enteredLocation)
                if (distanceInMeters <= acceptedProximity) {
                    Log.d(TAG, "location matched")
                } else {
                    Log.d(TAG, "location not matched")
                }
            }
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgregarCocheraMapBinding.inflate(inflater, container, false)
        //binding = FragmentAgregarCocheraMapBinding.inflate(layoutInflater)
        binding.autocompleteAddress1.setOnClickListener(startAutocompleteIntentListener)

        val checkProximityBox = binding.checkboxProximity
        checkProximityBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            checkProximity = isChecked
        }
        val saveButton = binding.autocompleteSaveButton
        saveButton.setOnClickListener { saveForm() }
        val resetButton = binding.autocompleteResetButton
        resetButton.setOnClickListener { clearForm() }
        return binding.root
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
                    "locality" -> binding.autocompleteCity.setText(component.name)
                    "administrative_area_level_1" -> {
                        binding.autocompleteState.setText(component.shortName)
                    }
                    "country" -> binding.autocompleteCountry.setText(component.name)
                }
            }
        }
        binding.autocompleteAddress1.setText(address1.toString())
        binding.autocompletePostal.setText(postcode.toString())
        binding.autocompleteAddress2.requestFocus()
        showMap(place)
    }

    private fun showMap(place: Place) {
        coordinates = place.latLng as LatLng
        mapFragment =
            childFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment?
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
            mapFragment!!.getMapAsync(requireActivity() as OnMapReadyCallback)
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
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        marker = map!!.addMarker(MarkerOptions().position(coordinates))
    }

    private fun clearForm() {
        binding.autocompleteAddress1.setText("")
        binding.autocompleteAddress2.text.clear()
        binding.autocompleteCity.text.clear()
        binding.autocompleteState.text.clear()
        binding.autocompletePostal.text.clear()
        binding.autocompleteCountry.text.clear()
        mapPanel.visibility = View.GONE
        binding.autocompleteAddress1.requestFocus()
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
}
