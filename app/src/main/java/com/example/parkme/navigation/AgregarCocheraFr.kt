package com.example.parkme.navigation

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.parkme.MainActivity
import com.example.parkme.R
import com.example.parkme.databinding.FragmentAgregarCocheraBinding
import com.example.parkme.entities.Cochera
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Normalizer
import java.util.*

class AgregarCocheraFr : Fragment(R.layout.fragment_agregar_cochera),
    OnMapReadyCallback {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var mapPanel: View
    private var mapFragment: SupportMapFragment? = null
    private lateinit var coordinates: LatLng
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var checkProximity = false
    private lateinit var binding: FragmentAgregarCocheraBinding
    private var deviceLocation: LatLng? = null
    private val acceptedProximity = 150.0

    companion object {
        private val TAG = AgregarCocheraFr::class.java.simpleName
        private const val MAP_FRAGMENT_TAG = "MAP"
    }

    private var startAutocompleteIntentListener = View.OnClickListener { view: View ->
        view.setOnClickListener(null)
        startAutocompleteIntent()
    }

    private val startAutocomplete = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        binding.eTDireccion.setOnClickListener(startAutocompleteIntentListener)
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

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val apiKey = "AIzaSyDW5u0qurjfVpPY3PVH0yZpauP75T2w1FY"
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }
        if (activity is MainActivity) {
            (activity as MainActivity).setBottomNavViewVisibility(View.GONE)
        }
        binding = FragmentAgregarCocheraBinding.inflate(inflater, container, false)
        binding.eTDireccion.setOnClickListener(startAutocompleteIntentListener)
        val buttonAgregarCochera = binding.button6
        val volverAgregarCochera = binding.button5
        val eTNombreCochera = binding.eTNombreCochera
        val eTPrecioPorHora = binding.eTPrecioPorHora
        val eTDireccion = binding.eTDireccion
        val eTDescripcion = binding.eTDescripcion
        val eTDisponibilidad = binding.eTDisponibilidad

        // Función para habilitar o deshabilitar el botón según la validación
        fun updateButtonState() {
            val isNombreCocheraValid = eTNombreCochera.text?.isNotEmpty()
            val isPrecioPorHoraValid = eTPrecioPorHora.text?.toString()?.toFloatOrNull() != null
            val isDireccionValid = eTDireccion.text?.isNotEmpty()
            val isDescripcionValid = eTDescripcion.text?.isNotEmpty()
            val isDisponibilidadValid = eTDisponibilidad.text?.isNotEmpty()

            buttonAgregarCochera.isEnabled =
                isNombreCocheraValid == true && isPrecioPorHoraValid && isDireccionValid == true && isDescripcionValid == true && isDisponibilidadValid == true

        }

        eTNombreCochera.addTextChangedListener { updateButtonState() }
        eTPrecioPorHora.addTextChangedListener { updateButtonState() }
        eTDireccion.addTextChangedListener { updateButtonState() }
        eTDescripcion.addTextChangedListener { updateButtonState() }
        eTDisponibilidad.addTextChangedListener { updateButtonState() }
        buttonAgregarCochera.setOnClickListener {
            agregarCochera()
        }

        volverAgregarCochera.setOnClickListener {
            navegarAMisCocheras()
        }

        buttonAgregarCochera.isEnabled = false

        return binding.root
    }

    private fun navegarAMisCocheras(){
        val navController = binding.root.findNavController()
        navController.popBackStack(R.id.navigation_container, false)
        if (activity is MainActivity) {
            (activity as MainActivity).setBottomNavViewVisibility(View.VISIBLE)
        }
        navController.navigate(R.id.misCocherasFr)
    }

    private fun agregarCochera() {
        disponibilidadFocusListener()
        descripcionFocusListener()
        nombreCocheraFocusListener()
        direccionFocusListener()
        val nombreCochera = binding.eTNombreCochera.text.toString()
        val precioPorHora = binding.eTPrecioPorHora.text.toString()
        val direccion = binding.eTDireccion.text.toString()
        val disponibilidad = binding.eTDisponibilidad.text.toString()
        val lat = coordinates.latitude
        val lng = coordinates.longitude
        if (uid != null) {
            val cochera = Cochera(
                "",
                nombreCochera,
                direccion,
                lat,
                lng,
                precioPorHora.toFloatOrNull() ?: 0.0f,
                "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", // URL de imagen (cambia a la URL correcta)
                disponibilidad,
                uid
            )
            db.collection("cocheras")
                .add(cochera)
                .addOnSuccessListener { documentReference ->
                    val cocheraId = documentReference.id
                    cochera.cocheraId = cocheraId
                    Log.e("ExploreFr", "Cochera Agregada: $cochera")
                    db.collection("cocheras").document(cocheraId)
                        .set(cochera)
                    Toast.makeText(
                        requireContext(),
                        "Cochera Agregada: ${cochera.cocheraId}",
                        Toast.LENGTH_SHORT
                    ).show()
                    navegarAMisCocheras()
                }
                .addOnFailureListener { e ->
                    Log.w("ExploreFr", "Error al agregar el documento", e)
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
        binding.eTDireccion.clearFocus()
        binding.eTDireccion.setText(address1.toString())
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
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
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
                .draggable(true)
        )
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        map!!.setOnCameraMoveListener {
            val center = map!!.cameraPosition.target
            marker!!.position = center
        }
    }

    private fun disponibilidadFocusListener() : Boolean {
        binding.eTDisponibilidad.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.disponibilidadContainer.helperText = validarDisponibilidad()
            }
        }
        return binding.disponibilidadContainer.helperText == null
    }

    private fun validarDisponibilidad(): String? {
        val disponibilidadNormalized = Normalizer.normalize(binding.eTDisponibilidad.text.toString(), Normalizer.Form.NFD)
        if(disponibilidadNormalized.length < 5){
            return "Minimo 5 caracteres"
        }
        if(!disponibilidadNormalized.matches(".*[A-Z].*".toRegex())){
            return "Debe contener al menos 1 mayuscula"
        }
        if(!disponibilidadNormalized.matches(".*[a-z].*".toRegex())){
            return "Debe contener al menos 1 minuscula"
        }
        return null
    }

    private fun descripcionFocusListener() : Boolean{
        binding.eTDescripcion.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.descripcionContainer.helperText = validarDescripcion()
            }
        }
        return binding.descripcionContainer.helperText == null
    }

    private fun validarDescripcion(): String? {
        val descripcionNormalized = Normalizer.normalize(binding.eTDescripcion.text.toString(), Normalizer.Form.NFD)
        if(descripcionNormalized.length < 4){
            return "Minimo 4 caracteres"
        }
        if(!descripcionNormalized.matches(".*[A-Z].*".toRegex())){
            return "Debe contener al menos 1 mayuscula"
        }
        if(!descripcionNormalized.matches(".*[a-z].*".toRegex())){
            return "Debe contener al menos 1 minuscula"
        }
        return null
    }

    private fun direccionFocusListener() : Boolean {
        binding.eTDireccion.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.direccionContainer.helperText = validarDireccion()
            }
        }
        return binding.direccionContainer.helperText == null
    }

    private fun validarDireccion(): String? {
        val direccionNormalized = Normalizer.normalize(binding.eTDireccion.text.toString(), Normalizer.Form.NFD)
        if(direccionNormalized.length < 10){
            return "Minimo 10 caracteres"
        }
        if(!direccionNormalized.matches(".*[A-Z].*".toRegex())){
            return "Debe contener al menos 1 mayuscula"
        }
        if(!direccionNormalized.matches(".*[a-z].*".toRegex())){
            return "Debe contener al menos 1 minuscula"
        }
        if(!direccionNormalized.matches(".*[0-9].*".toRegex())){
            return "Debe contener al menos 1 numero"
        }
        if(direccionNormalized.matches(".*[@$#!%|^&*()_+=].*".toRegex())){
            return "No debe incluir caracteres especiales"
        }
        return null
    }

    private fun nombreCocheraFocusListener() : Boolean{
        binding.eTNombreCochera.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.nombreCocheraContainer.helperText = validarNombreCochera()
            }
        }
        return binding.nombreCocheraContainer.helperText == null
    }

    private fun validarNombreCochera(): String? {
        if(binding.eTNombreCochera.text.toString().length < 8){
            return "Minimo 8 caracteres"
        }
        return null
    }
}