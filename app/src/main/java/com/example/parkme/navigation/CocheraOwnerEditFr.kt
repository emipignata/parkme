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
    private val cochera: Cochera by lazy { args.cochera } // Use lazy initialization
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocheraOwnerEditBinding.inflate(inflater, container, false)
        val cocheraNombre = binding.eTNombreCocheraItemView
        cocheraNombre.setText(cochera.nombre)
        val cocheraDireccion = binding.eTDireccionItemView
        cocheraDireccion.setText(cochera.direccion)
        val cocheraPrecio = binding.eTPrecioPorHoraItemView
        cocheraPrecio.setText(cochera.price.toString())
        val cocheraDisponibilidad = binding.eTDisponibilidadItemView
        cocheraDisponibilidad.setText(cochera.ocupada)
        val urlImage = binding.simpleImageButtonItemView
        val aceptarEditButton: Button = binding.buttonAceptarEdit
        val vovlerEditButton: Button = binding.buttonVolverEdit

        vovlerEditButton.setOnClickListener(){
            binding.root.findNavController().navigateUp()
           // binding.root.findNavController().navigateUp() te hace el salto de 2 pantallas esta genial
        }

        val cocheraNueva = Cochera(
            cochera.cocheraId,
            cocheraNombre.text.toString(),
            cocheraDireccion.text.toString(),
            lat = cochera.lat, // Proporciona el valor apropiado para "lat" (puedes cambiar esto)
            lng = cochera.lat,// Proporciona el valor apropiado para "lng" (puedes cambiar esto)
            cocheraPrecio.text.toString().toFloat(),
            if (urlImage.toString() == "") cochera.urlImage else urlImage.toString(),
            cocheraDisponibilidad.text.toString(),
           owner = cochera.owner // Proporciona el valor apropiado para "owner" (puedes cambiar esto)
        )
        aceptarEditButton.setOnClickListener(){

            //val cocheraNueva = Cochera(parcelableCreator<Cochera>(cochera.cocheraId,cocheraNombre,cocheraDireccion,cocheraPrecio,cocheraDisponibilidad))
            db.collection("cocheras").document(cochera.cocheraId).set(cocheraNueva)
            Toast.makeText(requireContext(), "La cochera ${cochera.nombre} se ha editado con éxito", Toast.LENGTH_SHORT).show()
            binding.root.findNavController().navigateUp()
            binding.root.findNavController().navigateUp()
        }


        return binding.root

    }



}
