package com.example.parkme.entities
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Pago(
    val reserva: Reserva,
    val duenio: String,
    val fecha: String,
    val horaEntrada: Timestamp,
    val horaSalida: Timestamp,
    val precio: Double
) : Parcelable {

    constructor(parcel: Parcel) : this(
         parcel.readParcelable(Reserva::class.java.classLoader)!!,
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader)!! ,
         parcel.readParcelable(Timestamp::class.java.classLoader)!!,
         parcel.readDouble(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(reserva,flags)
        parcel.writeString(duenio)
        parcel.writeString(fecha)
        parcel.writeParcelable(horaEntrada,flags)
        parcel.writeParcelable(horaSalida,flags)
        parcel.writeDouble(precio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Pago> = object : Parcelable.Creator<Pago> {
            override fun createFromParcel(parcel: Parcel): Pago {
                return Pago(parcel)
            }

            override fun newArray(size: Int): Array<Pago?> {
                return arrayOfNulls(size)
            }
        }
    }
}
