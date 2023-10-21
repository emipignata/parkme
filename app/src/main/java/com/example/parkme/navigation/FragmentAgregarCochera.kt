import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.parkme.databinding.FragmentAgregarCocheraBinding
import com.example.parkme.entities.Cochera
import com.google.firebase.firestore.FirebaseFirestore

class FragmentAgregarCochera : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: FragmentAgregarCocheraBinding

    @SuppressLint("SuspiciousIndentation")
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

            val cochera = Cochera(
                "",
                nombreCochera,
                direccion,
                -33.13017, // Latitud (cambia a la latitud correcta)
                -64.34902, // Longitud (cambia a la longitud correcta)
                precioPorHora.toFloatOrNull() ?: 0.0f,
                "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg", // URL de imagen (cambia a la URL correcta)
                disponibilidad,
                "firebase" // Usuario (cambia al usuario correcto)
            )

            // Agregar la Cochera a la colección en Firestore
            db.collection("cocheras")
                .add(cochera)
                .addOnSuccessListener { documentReference ->
                    val cocheraId = documentReference.id
                    cochera.cocheraId = cocheraId   // Asigna el ID a la propiedad cocheraId
                    Log.e("ExploreFr", "Cochera Agregada: $cochera")
                    db.collection("cocheras").document(cocheraId).set(cochera) // Guarda el objeto cochera en Firestore
                    Toast.makeText(requireContext(), "Cochera Agregada: ${cochera.cocheraId}", Toast.LENGTH_SHORT).show()
                    binding.root.findNavController().navigateUp()
                }
                .addOnFailureListener { e ->
                    Log.w("ExploreFr", "Error al agregar el documento", e)
                }
        }
        return binding.root
    }
}
