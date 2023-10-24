package com.example.parkme.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.parkme.R
import com.example.parkme.databinding.FragmentAgregarCocheraBinding
import com.example.parkme.entities.Cochera
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Normalizer

class AgregarCocheraFr : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: FragmentAgregarCocheraBinding
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgregarCocheraBinding.inflate(inflater, container, false)
        disponibilidadFocusListener()
        descripcionFocusListener()
        direccionFocusListener()
        nombreCocheraFocusListener()
        val buttonAgregarCochera = binding.button5
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

        // Observa los cambios en los campos y actualiza el estado del botón
        eTNombreCochera.addTextChangedListener { updateButtonState() }
        eTPrecioPorHora.addTextChangedListener { updateButtonState() }
        eTDireccion.addTextChangedListener { updateButtonState() }
        eTDescripcion.addTextChangedListener { updateButtonState() }
        eTDisponibilidad.addTextChangedListener { updateButtonState() }

        buttonAgregarCochera.setOnClickListener {
            val nombreCochera = eTNombreCochera.text.toString()
            val precioPorHora = eTPrecioPorHora.text.toString()
            val direccion = eTDireccion.text.toString()
            val descripcion = eTDescripcion.text.toString()
            val disponibilidad = eTDisponibilidad.text.toString()

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
            // Agregar la Cochera a la colección en Firestore
        }

        // Deshabilita el botón inicialmente
        buttonAgregarCochera.isEnabled = false

        return binding.root
    }
    private fun disponibilidadFocusListener() : Boolean {
        binding.eTDisponibilidad.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.disponibilidadContainer.helperText = validarDisponibilidad()
//                actualizarEstadoDelBoton()
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
//                actualizarEstadoDelBoton()
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
//                actualizarEstadoDelBoton()
            }
        }
        return binding.direccionContainer.helperText == null
    }

    private fun validarDireccion(): String? {
        //le saco los acentos para validar la expresion regular
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
//                actualizarEstadoDelBoton()
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



