package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

data class Cochera(
    var cocheraId: String,
    var nombre: String,
    var direccion: String,
    var lat: Double,
    var lng: Double,
    var price: Float,
    var urlImage: String,
    val owner: String,
    val ownerName: String,
    val descripcion: String,
    var weeklyAvailability: List<DailyAvailability> = listOf(),
    val reservas: List<Reserva> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(DailyAvailability.CREATOR) ?: emptyList(),
        parcel.createTypedArrayList(Reserva.CREATOR) ?: emptyList()
    )
    // No-argument constructor is added here
    constructor() : this(
        "", "", "", 0.0, 0.0, 0.0f, "", "", "", "", emptyList(), emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cocheraId)
        parcel.writeString(nombre)
        parcel.writeString(direccion)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeFloat(price)
        parcel.writeString(urlImage)
        parcel.writeString(owner)
        parcel.writeString(ownerName)
        parcel.writeString(descripcion)
        parcel.writeTypedList(weeklyAvailability)
        parcel.writeTypedList(reservas)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Cochera> = object : Parcelable.Creator<Cochera> {
            override fun createFromParcel(parcel: Parcel): Cochera {
                return Cochera(parcel)
            }

            override fun newArray(size: Int): Array<Cochera?> {
                return arrayOfNulls(size)
            }
        }
    }
}
