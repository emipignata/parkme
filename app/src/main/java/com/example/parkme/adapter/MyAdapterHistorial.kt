package com.example.parkme.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R
import com.example.parkme.models.ItemHistorial

class MyAdapterHistorial (private val items: List<ItemHistorial>) : RecyclerView.Adapter<MyAdapterHistorial.ViewHolder>() {

   inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
       val tituloTextView: TextView = view.findViewById(R.id.tituloTextView)
       val descripcionTextView: TextView = view.findViewById(R.id.descripcionTextView)
       val imageView: ImageView = view.findViewById(R.id.imageView)
   }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_historial, parent, false) as LinearLayout
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tituloTextView.text = item.titulo
        holder.descripcionTextView.text = item.descripcion
        holder.imageView.setImageResource(item.imagenResId)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
