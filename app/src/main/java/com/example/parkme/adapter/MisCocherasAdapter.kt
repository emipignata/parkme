package com.example.parkme.adapter


import FragmentAgregarCochera
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.FragmentManager // Asegúrate de importar esta clase
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.example.parkme.models.ItemMisCocheras
// usar el adaptador de  Firebase.adapter................

class MisCocherasAdapter(private val cocheras: List<ItemMisCocheras>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<MisCocherasAdapter.MisCocheraViewHolder>() {
    inner class MisCocheraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCochera: TextView = itemView.findViewById(R.id.misCocherasViewTitle)
        val direccionCochera: TextView = itemView.findViewById(R.id.misCocherasAddress)
        val imgCochera: ImageView = itemView.findViewById(R.id.misCocherasViewImg)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisCocheraViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mis_cocheras_item, parent, false)
        return MisCocheraViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MisCocheraViewHolder, position: Int) {
        val cochera = cocheras[position]
        holder.nombreCochera.text = cochera.titulo
        holder.direccionCochera.text = cochera.descripcion
        holder.imgCochera.setImageResource(cochera.imagenResId)

    }

    override fun getItemCount(): Int {
        return cocheras.size
    }
}
