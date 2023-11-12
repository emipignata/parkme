package com.example.parkme.entities
import android.os.Parcel
import android.os.Parcelable

data class Reserva(
    var reservaId: String,
    var usuarioId: String,
    var fechaCreacion: String,
    var cocheraId: String,
    var ownerId: String,
    var estado: String,
    var precio: Float,
    var fechaEntrada: String,
    var horaEntrada: String,
    var fechaSalida: String,
    var horaSalida: String,
    var direccion: String,
    var urlImage: String,
    var ownerName: String
) : Parcelable {
        constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
        parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
            parcel.readFloat(),
            parcel.readString() ?: "",
parcel.readString() ?: "",
parcel.readString() ?: "",
parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",

        )
        // No-argument constructor is added here
        constructor() : this(
        "", "", "", "", "", "",0.0f, "","","","",  "", "", ""
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reservaId)
        parcel.writeString(usuarioId)
        parcel.writeString(fechaCreacion)
        parcel.writeString(cocheraId)
        parcel.writeString(ownerId)
        parcel.writeString(estado)
        parcel.writeFloat(precio)
        parcel.writeString(fechaEntrada)
        parcel.writeString(horaEntrada)
        parcel.writeString(fechaSalida)
        parcel.writeString(horaSalida)
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
