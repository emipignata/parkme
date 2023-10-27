package com.example.parkme.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.databinding.FragmentCocheraDetailOwnerBinding
import com.example.parkme.entities.Cochera
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val db = FirebaseFirestore.getInstance()
private val uid = FirebaseAuth.getInstance().currentUser?.uid


class CocheraDetailOwnerFr : Fragment() {
    private val args: CocheraDetailOwnerFrArgs by navArgs()
    private lateinit var binding: FragmentCocheraDetailOwnerBinding
    private val cochera: Cochera by lazy { args.cochera } // Use lazy initialization
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocheraDetailOwnerBinding.inflate(inflater, container, false)
        val cocheraNombre = binding.tVNombreCocheraItemView
        cocheraNombre.text = cochera.nombre
        val cocheraDireccion = binding.tVDireccionItemView
        cocheraDireccion.text = cochera.direccion
        val cocheraPrecio = binding.tVprecioPorHoraContainerItemView
        cocheraPrecio.text = cochera.price.toString()
        val cocheraDisponibilidad = binding.tVDisponibilidadItemView
        cocheraDisponibilidad.text = cochera.ocupada


        val volverButton: Button = binding.buttonVolverItemView
        val eliminarButton: Button = binding.buttonEliminarItemView
        val editarButton: Button = binding.buttonEditarItemView


        volverButton.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }

        editarButton.setOnClickListener {
            val action = CocheraDetailOwnerFrDirections.actionCocheraDetailOwnerFrToCocheraOwnerEditFr(cochera)
            binding.root.findNavController()?.navigate(action)
        }

        eliminarButton.setOnClickListener {
            deleteCochera()
        }

        return binding.root
    }

    private fun deleteCochera() {
        db.collection("cocheras").document(cochera.cocheraId).delete()
        Toast.makeText(requireContext(), "La cochera se ha eliminado", Toast.LENGTH_SHORT).show()
        binding.root.findNavController().navigateUp()
    }


}
