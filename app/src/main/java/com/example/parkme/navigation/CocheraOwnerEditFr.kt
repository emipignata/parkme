package com.example.parkme.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.databinding.FragmentCocheraOwnerEditBinding
import com.example.parkme.entities.Cochera
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StaticFieldLeak")
private val db = FirebaseFirestore.getInstance()
private val uid = FirebaseAuth.getInstance().currentUser?.uid

class CocheraOwnerEditFr : Fragment() {

    private val args: CocheraOwnerEditFrArgs by navArgs()
    private lateinit var binding: FragmentCocheraOwnerEditBinding
    private val cochera: Cochera by lazy { args.cochera }
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocheraOwnerEditBinding.inflate(inflater, container, false)
        var cocheraNombre = binding.eTNombreCocheraItemView
        cocheraNombre.setText(cochera.nombre)
        var cocheraDireccion = binding.eTDireccionItemView
        cocheraDireccion.setText(cochera.direccion)
        var cocheraPrecio = binding.eTPrecioPorHoraItemView
        cocheraPrecio.setText(cochera.price.toString())
        var urlImage = binding.simpleImageButtonItemView
        var aceptarEditButton: Button = binding.buttonAceptarEdit

//        binding.switch247Editar.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                setFullWeekAvailability()
//            } else {
//                clearFullWeekAvailability()
//            }
//            toggleTimeRangeViews(isChecked)
//        }
//
//        binding.imgAddDateTimeRange.setOnClickListener {
//            addTimeRange()
//        }

        aceptarEditButton.setOnClickListener() {

            val nuevoNombre = cocheraNombre.text.toString()
            val nuevaDireccion = cocheraDireccion.text.toString()
            val nuevoPrecio = cocheraPrecio.text.toString().toDouble()

            cochera.nombre = nuevoNombre
            cochera.direccion = nuevaDireccion
            cochera.price = nuevoPrecio.toFloat()
            cochera.urlImage =  "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"

            db.collection("cocheras").document(cochera.cocheraId).set(cochera)
            Toast.makeText(requireContext(), "La cochera ${cochera.nombre} se ha editado con éxito", Toast.LENGTH_SHORT)
                .show()
            binding.root.findNavController().navigateUp()
            binding.root.findNavController().navigateUp()
        }

//        fun updateButtonState() {
//            val isNombreCocheraValid = eTNombreCochera.text?.isNotEmpty()
//            val isPrecioPorHoraValid = eTPrecioPorHora.text?.toString()?.toFloatOrNull() != null
//            val isDireccionValid = eTDireccion.text?.isNotEmpty()
//            val isDescripcionValid = eTDescripcion.text?.isNotEmpty()
//
//            buttonAgregarCochera.isEnabled =
//                isNombreCocheraValid == true && isPrecioPorHoraValid && isDireccionValid == true && isDescripcionValid == true
//        }

        return binding.root

    }


}
