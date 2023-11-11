package com.example.parkme.entities

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class Cochera(
    var cocheraId: String,
    var nombre: String,
    var direccion: String,
    var lat: Double,
    var lng: Double,
    var price: Float,
    var urlImage: String,
    var ocupada: String,
    val owner: String,
    val ownerName: String,
    val descripcion: String,
    var available : Boolean
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
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
        parcel.readString() ?: "",
        parcel.readBoolean()
    )
    // No-argument constructor is added here
    constructor() : this(
        "", "", "", 0.0, 0.0, 0.0f, "", "desocupada", "", "", "",true
    )
    @RequiresApi(Build.VERSION_CODES.Q)
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
        parcel.writeString(ownerName)
        parcel.writeString(descripcion)
        parcel.writeBoolean(available)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Cochera> = object : Parcelable.Creator<Cochera> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun createFromParcel(parcel: Parcel): Cochera {
                return Cochera(parcel)
            }

            override fun newArray(size: Int): Array<Cochera?> {
                return arrayOfNulls(size)
            }
        }
    }
}
