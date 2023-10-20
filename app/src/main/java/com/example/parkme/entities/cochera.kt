package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

data class Cochera(
    var cocheraId: String,
    val nombre: String,
    val direccion: String,
    val lat: Double,
    val lng: Double,
    val price: Float,
    val urlImage: String,
    val ocupada: String,
    val owner: String
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
        parcel.readString() ?: ""
    )
    // No-argument constructor is added here
    constructor() : this(
        "", "", "", 0.0, 0.0, 0.0f, "", "desocupada", ""
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cocheraId)
        parcel.writeString(nombre)
        parcel.writeString(direccion)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeFloat(price)
        parcel.writeString(urlImage)
        parcel.writeString(ocupada)
        parcel.writeString(owner)
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
