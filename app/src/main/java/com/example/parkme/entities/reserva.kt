package com.example.parkme.entities
import android.os.Parcel
import android.os.Parcelable

data class Reserva(
    val reservaId: Int,
    val usuarioId: Int,
    val fecha: String,
    val horaEntrada: String,
    val horaSalida: String,
    val precio: Double
) : Parcelable {

    constructor(
        reservaId: Int,
        usuarioId: Int,
        fecha: String,
        horaEntrada: String,
        horaSalida: String
    ) : this(reservaId, usuarioId, fecha, horaEntrada, horaSalida, 0.0)

    // Constructor secundario para Parcelable
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(reservaId)
        parcel.writeInt(usuarioId)
        parcel.writeString(fecha)
        parcel.writeString(horaEntrada)
        parcel.writeString(horaSalida)
        parcel.writeDouble(precio)
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
