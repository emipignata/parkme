
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.parkme.R
import com.example.parkme.entities.Cochera
import com.example.parkme.holders.CocheraHolder
import com.example.parkme.navigation.MisCocherasFrDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MisCocherasFirestoreAdapter(options: FirestoreRecyclerOptions<Cochera>) :
    FirestoreRecyclerAdapter<Cochera, CocheraHolder>(options) {

    override fun onBindViewHolder(holder: CocheraHolder, position: Int, model: Cochera) {
        holder.setCard(model.nombre, model.direccion, model.price, model.urlImage)

        holder.itemView.setOnClickListener {
            val action = MisCocherasFrDirections.actionMisCocherasFrToCocheraDetailOwnerFr(model)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocheraHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mis_cocheras_item, parent, false)
        return CocheraHolder(view)
    }
}
