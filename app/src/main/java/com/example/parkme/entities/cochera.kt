package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

data class Cochera(
    var cocheraId: String = "",
    var nombre: String = "",
    var direccion: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var price: Float = 0.0f,
    var urlImage: String = "",
    val owner: String = "",
    val ownerName: String = "",
    val descripcion: String = "",
    var weeklyAvailability: MutableList<DailyAvailability> = mutableListOf(),
    val reservas: MutableList<Reserva> = mutableListOf()
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
        parcel.createTypedArrayList(DailyAvailability.CREATOR) ?: mutableListOf(),
        parcel.createTypedArrayList(Reserva.CREATOR) ?: mutableListOf()
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
