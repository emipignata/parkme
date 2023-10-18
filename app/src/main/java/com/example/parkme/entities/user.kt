package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

class User(nombre: String, apellido: String, email : String, lng: Double, price: Float, urlImage: String) :
    Parcelable {
    var nombre: String
    var apellido: String
    var email: String
    var lng: Double
    var price: Float
    var urlImage: String

    constructor() : this("","","",0.0,0.0f,"")


    init {
        this.nombre = nombre!!
        this.apellido = apellido!!
        this.email = email!!
        this.lng = lng!!
        this.price = price!!
        this.urlImage = urlImage!!
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readDouble()!!,
        source.readFloat(),
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(nombre)
        writeString(apellido)
        writeString(email)
        writeDouble(lng)
        writeFloat(price)
        writeString(urlImage)
    }

    override fun toString(): String {
        return "User(nombre='$nombre', apellido='$apellido', email='$email', lng='$lng', price='$price', urlImage='$urlImage')"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}