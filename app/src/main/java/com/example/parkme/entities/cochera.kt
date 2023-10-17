package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

class Cochera(nombre: String, direccion: String, lat : Double, lng: Double, price: Float, urlImage: String) :
    Parcelable {
    var nombre: String
    var direccion: String
    var lat: Double
    var lng: Double
    var price: Float
    var urlImage: String

    constructor() : this("","",0.0,0.0,0.0f,"")


    init {
        this.nombre = nombre!!
        this.direccion = direccion!!
        this.lat = lat!!
        this.lng = lng!!
        this.price = price!!
        this.urlImage = urlImage!!
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readDouble()!!,
        source.readDouble()!!,
        source.readFloat(),
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(nombre)
        writeString(direccion)
        writeDouble(lat)
        writeDouble(lng)
        writeFloat(price)
        writeString(urlImage)
    }

    override fun toString(): String {
        return "Cochera(nombre='$nombre', direccion='$direccion', lat='$lat', lng='$lng', price='$price', urlImage='$urlImage')"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Cochera> = object : Parcelable.Creator<Cochera> {
            override fun createFromParcel(source: Parcel): Cochera = Cochera(source)
            override fun newArray(size: Int): Array<Cochera?> = arrayOfNulls(size)
        }
    }
}