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
}
