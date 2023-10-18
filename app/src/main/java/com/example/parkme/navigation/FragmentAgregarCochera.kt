import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parkme.R
import com.example.parkme.databinding.FragmentAgregarCocheraBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.navigation.MisCocherasFr
import com.google.firebase.firestore.FirebaseFirestore

class FragmentAgregarCochera : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: FragmentAgregarCocheraBinding
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgregarCocheraBinding.inflate(inflater, container, false)

        binding.button5.setOnClickListener {
            val nombreCochera = binding.eTNombreCochera.text.toString()
            val precioPorHora = binding.eTPrecioPorHora.text.toString()
            val direccion = binding.eTDireccion.text.toString()
            val descripcion = binding.eTDescripcion.text.toString()
            val disponibilidad = binding.eTDisponibilidad.text.toString()

            // Crear una instancia de Cochera con los valores ingresados en los campos
            val cochera = Cochera(
                nombreCochera,
                direccion,
                -33.13017, // Latitud (cambia a la latitud correcta)
                -64.34902, // Longitud (cambia a la longitud correcta)
                precioPorHora.toFloatOrNull() ?: 0.0f,
                "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", // URL de imagen (cambia a la URL correcta)
                disponibilidad.toBoolean(),
                "user1" // Usuario (cambia al usuario correcto)
            )
            fragmentManager = requireActivity().supportFragmentManager

            // Agregar la Cochera a la colección en Firestore
            db.collection("cocheras")
                .add(cochera)
                .addOnSuccessListener { documentReference ->
                    Log.e("ExploreFr", "Cochera Agregada: $cochera")
                    Toast.makeText(requireContext(), "Cochera Agregada: ${cochera.getNombre()}", Toast.LENGTH_SHORT).show()
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, MisCocherasFr())
                        .addToBackStack(null)
                        .commit()
                }
                .addOnFailureListener { e ->
                    Log.w("ExploreFr", "Error al agregar el documento", e)
                }
        }

        return binding.root
    }
}
