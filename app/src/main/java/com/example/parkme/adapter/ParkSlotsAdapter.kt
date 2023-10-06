package com.example.parkme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parkme.models.ParkSlot
import com.example.parkme.databinding.ViewParkslotItemBinding


class ParkSlotsAdapter (
    var parkslots: List<ParkSlot>,
    private val parkslotClickedListener: (ParkSlot) -> Unit
) :
    RecyclerView.Adapter<ParkSlotsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkSlotsAdapter.ViewHolder {
        val binding = ViewParkslotItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ParkSlotsAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParkSlotsAdapter.ViewHolder, position: Int) {
        val parkslot = parkslots[position]
        holder.bind(parkslot)
        holder.itemView.setOnClickListener { parkslotClickedListener(parkslot) }
    }

    override fun getItemCount() = parkslots.size

    class ViewHolder(private val binding: ViewParkslotItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parkslot: ParkSlot) {
            binding.parkslotViewTitle.text = parkslot.Titulo
            binding.parkslotAddress.text = parkslot.Direccion
            binding.parkslotPrice.text = parkslot.Precio
            Glide
                .with(binding.root.context)
                .load(parkslot.Imagen)
                .into(binding.parkslotViewImg)
        }

    }
}