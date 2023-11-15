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
import com.example.parkme.databinding.FragmentCocheraDetailOwnerBinding
import com.example.parkme.entities.Cochera
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("StaticFieldLeak")
private val db = FirebaseFirestore.getInstance()


class CocheraDetailOwnerFr : Fragment() {
    private val args: CocheraDetailOwnerFrArgs by navArgs()
    private lateinit var binding: FragmentCocheraDetailOwnerBinding
    private val cochera: Cochera by lazy { args.cochera }

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
        val cocheraDescripcion = binding.tVDescripcionItemView
        cocheraDescripcion.text = cochera.descripcion

        val eliminarButton: Button = binding.buttonEliminarItemView
        val editarButton: Button = binding.buttonEditarItemView

        editarButton.setOnClickListener {
            val action = CocheraDetailOwnerFrDirections.actionCocheraDetailOwnerFrToCocheraOwnerEditFr(cochera)
            binding.root.findNavController().navigate(action)
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
