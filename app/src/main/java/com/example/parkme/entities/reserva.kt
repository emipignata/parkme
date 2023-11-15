package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Reserva(
    var reservaId: String,
    var usuarioId: String,
    var fechaCreacion: Timestamp = Timestamp.now(),
    var cocheraId: String,
    var ownerId: String,
    var estado: String,
    var precio: Float,
    var fechaEntrada: Timestamp? = null,
    var fechaSalida: Timestamp? = null,
    var direccion: String,
    var urlImage: String,
    var ownerName: String
) : Parcelable {
        constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
            Timestamp(parcel.readLong(), parcel.readInt()),
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
            parcel.readFloat(),
            parcel.readParcelable(Timestamp::class.java.classLoader),
            parcel.readParcelable(Timestamp::class.java.classLoader),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",

        )
        // No-argument constructor is added here
        constructor() : this(
        "", "", Timestamp.now(), "", "", "",0.0f, null,null,"","",  "",
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reservaId)
        parcel.writeString(usuarioId)
        parcel.writeParcelable(fechaCreacion, flags)
        parcel.writeString(cocheraId)
        parcel.writeString(ownerId)
        parcel.writeString(estado)
        parcel.writeFloat(precio)
        parcel.writeParcelable(fechaEntrada, flags)
        parcel.writeParcelable(fechaSalida, flags)
        parcel.writeString(direccion)
        parcel.writeString(urlImage)
        parcel.writeString(ownerName)
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
