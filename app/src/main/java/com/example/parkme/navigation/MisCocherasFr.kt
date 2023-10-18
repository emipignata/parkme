package com.example.parkme.navigation


import FragmentAgregarCochera
import com.example.parkme.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.adapter.MisCocherasAdapter
import com.example.parkme.models.ItemMisCocheras

class MisCocherasFr : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fragmentManager: FragmentManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_cocheras, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMisCocheras) // Asegúrate de que el RecyclerView esté definido en fragment_mis_cocheras.xml

        // Crea una lista de datos de ejemplo (puedes reemplazar esto con tus propios datos)
        val cocherasList = listOf(
        ItemMisCocheras("Título 1", "Descripción 1", R.drawable.cochera),
        ItemMisCocheras("Título 2", "Descripción 2", R.drawable.cochera),
        ItemMisCocheras("Título 3", "Descripción 3", R.drawable.cochera),
        ItemMisCocheras("Título 4", "Descripción 4", R.drawable.cochera),
        ItemMisCocheras("Título 5", "Descripción 5", R.drawable.cochera),
        ItemMisCocheras("Título 6", "Descripción 6", R.drawable.cochera),
        ItemMisCocheras("Título 7", "Descripción 7", R.drawable.cochera),
        ItemMisCocheras("Título 8", "Descripción 8", R.drawable.cochera),
        ItemMisCocheras("Título 9", "Descripción 9", R.drawable.cochera),
        ItemMisCocheras("Título 10", "Descripción 10", R.drawable.cochera)
        )

       // Configura un adaptador personalizado para el RecyclerView
        val adapter = MisCocherasAdapter(cocherasList, childFragmentManager)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fragmentManager = requireActivity().supportFragmentManager



          val button3 = view.findViewById<Button>(R.id.button3)

        button3.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentAgregarCochera())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
