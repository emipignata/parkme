package com.example.parkme.entities
import android.os.Parcel
import android.os.Parcelable

data class Reserva(
    var reservaId: String,
    var usuarioId: String,
    var fecha: String,
    var cocheraId: String,
    var ownerId: String,
    var estado: String,
    var precio: Float

) : Parcelable {
        constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
            parcel.readString() ?: "",
        parcel.readFloat()
        )
        // No-argument constructor is added here
        constructor() : this(
        "", "", "", "", "", "",0.0f
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reservaId)
        parcel.writeString(usuarioId)
        parcel.writeString(fecha)
        parcel.writeString(cocheraId)
        parcel.writeString(ownerId)
        parcel.writeString(estado)
        parcel.writeFloat(precio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Reserva> = object : Parcelable.Creator<Reserva> {
            override fun createFromParcel(parcel: Parcel): Reserva {
                return Reserva(parcel)
            }

            override fun newArray(size: Int): Array<Reserva?> {
                return arrayOfNulls(size)
            }
        }
    }
}