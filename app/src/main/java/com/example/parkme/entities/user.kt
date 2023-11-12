package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

data class User(
    val userId: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val historial: List<Message> = emptyList(),
    val cocheras: List<Cochera> = emptyList(),
    val urlImage: String = "",
    var reservaInReservada: String = "",
    var reservaInCheckIn: String = "",
    var reservaInCheckOut: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.createTypedArrayList(Message.CREATOR) ?: emptyList(),
        source.createTypedArrayList(Cochera.CREATOR) ?: emptyList(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(userId)
        writeString(nombre)
        writeString(apellido)
        writeString(email)
        writeTypedList(historial)
        writeTypedList(cocheras)
        writeString(urlImage)
        writeString(reservaInReservada)
        writeString(reservaInCheckIn)
        writeString(reservaInCheckOut)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}
