package com.example.parkme.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkme.R

class MyAdapterHistorial(private val items: List<String>) : RecyclerView.Adapter<MyAdapterHistorial.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItem: TextView = itemView.findViewById(R.id.textViewItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textViewItem.text = item
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

