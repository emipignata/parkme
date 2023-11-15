package com.example.parkme.navigation

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.parkme.MainActivity
import com.example.parkme.R
import com.example.parkme.databinding.DialogAddTimeRangeBinding
import com.example.parkme.databinding.FragmentAgregarCocheraBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.DailyAvailability
import com.example.parkme.entities.TimeRange
import com.example.parkme.entities.User
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.time.LocalTime
import java.util.*

class AgregarCocheraFr : Fragment(R.layout.fragment_agregar_cochera), OnMapReadyCallback {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private var storageReference = FirebaseStorage.getInstance().reference
    private lateinit var mapPanel: View
    private var mapFragment: SupportMapFragment? = null
    private lateinit var coordinates: LatLng
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var imageURL: String = "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"
    private lateinit var binding: FragmentAgregarCocheraBinding
    private lateinit var ownerName: String
    private lateinit var cochera: Cochera

    companion object {
        private val TAG = AgregarCocheraFr::class.java.simpleName
        private const val MAP_FRAGMENT_TAG = "MAP"
        private const val PICK_IMAGE_REQUEST = 1
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
            .setTypesFilter(listOf(TypeFilter.ADDRESS.toString().lowercase()))
            .build(requireContext())
        startAutocomplete.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCurrentUserName { userName ->
            if (userName != null) {
                ownerName = userName
            } else {
                Log.d("UserName", "Failed to retrieve the user name.")
            }
        }
    }

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
        cochera = Cochera()
        binding.switch247.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setFullWeekAvailability()
            } else {
                clearFullWeekAvailability()
            }
            toggleTimeRangeViews(isChecked)
        }

        binding.imgAddDateTimeRange.setOnClickListener {
            addTimeRange()
        }

        binding.eTDireccion.setOnClickListener(startAutocompleteIntentListener)
        val imageAgregarFoto = binding.simpleImageButton
        val buttonAgregarCochera = binding.button6
        val eTNombreCochera = binding.eTNombreCochera
        val eTPrecioPorHora = binding.eTPrecioPorHora
        val eTDireccion = binding.eTDireccion
        val eTDescripcion = binding.eTDescripcion

        fun updateButtonState() {
            val isNombreCocheraValid = eTNombreCochera.text?.isNotEmpty()
            val isPrecioPorHoraValid = eTPrecioPorHora.text?.toString()?.toFloatOrNull() != null
            val isDireccionValid = eTDireccion.text?.isNotEmpty()
            val isDescripcionValid = eTDescripcion.text?.isNotEmpty()

            buttonAgregarCochera.isEnabled =
                isNombreCocheraValid == true && isPrecioPorHoraValid && isDireccionValid == true && isDescripcionValid == true
        }

        descripcionFocusListener()
        nombreCocheraFocusListener()
        direccionFocusListener()
        eTNombreCochera.addTextChangedListener { updateButtonState() }
        eTPrecioPorHora.addTextChangedListener { updateButtonState() }
        eTDireccion.addTextChangedListener { updateButtonState() }
        eTDescripcion.addTextChangedListener { updateButtonState() }

        imageAgregarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        buttonAgregarCochera.setOnClickListener {
            agregarCochera()
        }
        buttonAgregarCochera.isEnabled = false
        return binding.root
    }

    private fun toggleTimeRangeViews(isFullWeek: Boolean) {
        binding.imgAddDateTimeRange.visibility = if (isFullWeek) View.GONE else View.VISIBLE
        binding.dividerAddDateTimeRange.visibility = if (isFullWeek) View.GONE else View.VISIBLE
        binding.textAddDateTimeRange.visibility = if (isFullWeek) View.GONE else View.VISIBLE
        binding.disponibilidadContainer.visibility = if (isFullWeek) View.GONE else View.VISIBLE
    }

    private fun setFullWeekAvailability() {
        val midnight = LocalTime.MIDNIGHT.toSecondOfDay().toLong()
        val endOfDay = LocalTime.MAX.toSecondOfDay().toLong()
        val fullWeekAvailability = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
            .map { DailyAvailability(it, mutableListOf(TimeRange(midnight, endOfDay))) }
        cochera.weeklyAvailability.clear()
        cochera.weeklyAvailability.addAll(fullWeekAvailability)
    }

    private fun clearFullWeekAvailability() {
        cochera.weeklyAvailability.clear()
    }

    private fun addTimeRange() {
        val dialogBinding = DialogAddTimeRangeBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBinding.timePickerStart.setIs24HourView(true)
        dialogBinding.timePickerEnd.setIs24HourView(true)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Agregar Disponibilidad")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { dialog, _ ->
                val selectedDay = dialogBinding.spinnerDayOfWeek.selectedItem.toString()
                val startTime = LocalTime.of(dialogBinding.timePickerStart.hour, dialogBinding.timePickerStart.minute)
                val endTime = LocalTime.of(dialogBinding.timePickerEnd.hour, dialogBinding.timePickerEnd.minute)
                addToWeeklyAvailability(selectedDay, startTime, endTime)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()
        dialog.show()
    }

    private fun addToWeeklyAvailability(day: String, start: LocalTime, end: LocalTime) {
        val startSeconds = start.toSecondOfDay().toLong()
        val endSeconds = end.toSecondOfDay().toLong()
        val newTimeRange = TimeRange(startSeconds, endSeconds)

        val dailyAvailability = cochera.weeklyAvailability.find { it.dayOfWeek == day }
        if (dailyAvailability != null) {
            if (!dailyAvailability.timeRanges.any { it.startSeconds == startSeconds && it.endSeconds == endSeconds }) {
                dailyAvailability.timeRanges.add(newTimeRange)
            }
        } else {
            val newDailyAvailability = DailyAvailability(day, mutableListOf(newTimeRange))
            cochera.weeklyAvailability.add(newDailyAvailability)
        }
        addTimeRangeView(newTimeRange, day)
    }

    private fun addTimeRangeView(timeRange: TimeRange, day: String) {
        val timeRangeView = LayoutInflater.from(context).inflate(R.layout.time_range_item, null)

        val txtDay = timeRangeView.findViewById<TextView>(R.id.txtDay)
        val txtStartTime = timeRangeView.findViewById<TextView>(R.id.txtStartTime)
        val txtEndTime = timeRangeView.findViewById<TextView>(R.id.txtEndTime)
        val btnDelete = timeRangeView.findViewById<Button>(R.id.btnDelete)

        txtDay.text = day
        txtStartTime.text = LocalTime.ofSecondOfDay(timeRange.startSeconds).toString()
        txtEndTime.text = LocalTime.ofSecondOfDay(timeRange.endSeconds).toString()

        btnDelete.setOnClickListener {
            removeTimeRangeView(timeRangeView, timeRange, day)
        }

        timeRangeView.setTag(R.id.tag_daily_availability, DailyAvailability(day, mutableListOf(timeRange)))
        binding.disponibilidadContainer.addView(timeRangeView)
    }

    private fun removeTimeRangeView(view: View, timeRange: TimeRange, day: String) {
        // Remove the view from the layout
        binding.disponibilidadContainer.removeView(view)

        // Find the DailyAvailability object for the specified day
        val dayAvailability = cochera.weeklyAvailability.find { it.dayOfWeek == day }

        // Remove the specific TimeRange object from the DailyAvailability
        dayAvailability?.timeRanges?.removeIf { it.startSeconds == timeRange.startSeconds && it.endSeconds == timeRange.endSeconds }

        // If there are no more TimeRanges left for that day, remove the DailyAvailability object from the list
        if (dayAvailability?.timeRanges.isNullOrEmpty()) {
            cochera.weeklyAvailability.remove(dayAvailability)
        }
    }

    private fun getCurrentUserName(onResult: (String?) -> Unit) {
        if (uid == null) {
            onResult(null)
            return
        }
        val usersCollectionRef = db.collection("users")
        usersCollectionRef.document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    onResult(user?.nombre)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                onResult(null)
            }
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
        val weeklyAvailability = if (binding.switch247.isChecked) {
            setFullWeekAvailability()
            cochera.weeklyAvailability
        } else {
            collectCustomWeeklyAvailability()
        }

        val nombreCochera = binding.eTNombreCochera.text.toString()
        val precioPorHora = binding.eTPrecioPorHora.text.toString().toFloatOrNull() ?: 0.0f
        val direccion = binding.eTDireccion.text.toString()
        val descripcion = binding.eTDescripcion.text.toString()
        val lat = coordinates.latitude
        val lng = coordinates.longitude

        val newCochera = Cochera(
            cocheraId = "",
            nombre = nombreCochera,
            direccion = direccion,
            lat = lat,
            lng = lng,
            price = precioPorHora,
            urlImage = imageURL,
            owner = uid ?: "",
            ownerName = ownerName,
            descripcion = descripcion,
            weeklyAvailability = weeklyAvailability.toMutableList(),
            reservas = mutableListOf()
        )

        db.collection("cocheras")
            .add(newCochera)
            .addOnSuccessListener { documentReference ->
                newCochera.cocheraId = documentReference.id
                Log.e(TAG, "Cochera Agregada: $newCochera")
                db.collection("cocheras").document(documentReference.id)
                    .set(newCochera)
                Toast.makeText(
                    requireContext(),
                    "Cochera Agregada: ${newCochera.cocheraId}",
                    Toast.LENGTH_SHORT
                ).show()
                navegarAMisCocheras()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al agregar el documento", e)
            }
    }

    private fun collectCustomWeeklyAvailability(): MutableList<DailyAvailability> {
        return binding.disponibilidadContainer.children.mapNotNull { view ->
            view.getTag(R.id.tag_daily_availability) as? DailyAvailability
        }.toMutableList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val imageUri = data?.data
            imageUri?.let {
                uploadImageToFirebaseStorage(it)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = storageReference.child("images/$filename")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveImageURLToFirestore(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageURLToFirestore(url: String) {
        val data = hashMapOf("imageUrl" to url)
        db.collection("images")
            .add(data)
            .addOnSuccessListener { documentReference ->
                if (isAdded && !isRemoving) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        Glide.with(requireContext())
                            .load(url)
                            .into(binding.simpleImageButton)
                    }
                }
                imageURL = url
            }
            .addOnFailureListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show()
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