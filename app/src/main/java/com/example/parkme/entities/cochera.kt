package com.example.parkme.entities

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

class Cochera(nombre: String, direccion: String, lat : Double, lng: Double, price: Float, urlImage: String, ocupada: Boolean, owner: String) :
    Parcelable {
    var nombre: String
    var direccion: String
    var lat: Double
    var lng: Double
    var price: Float
    var urlImage: String
    var ocupada: Boolean
    var owner: String


    constructor() : this("","",0.0,0.0,0.0f,"", false, "")


    init {
        this.nombre = nombre!!
        this.direccion = direccion!!
        this.lat = lat!!
        this.lng = lng!!
        this.price = price!!
        this.urlImage = urlImage!!
        this.ocupada = ocupada!!
        this.owner = owner!!
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readDouble()!!,
        source.readDouble()!!,
        source.readFloat(),
        source.readString()!!,
        source.readBoolean()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(nombre)
        writeString(direccion)
        writeDouble(lat)
        writeDouble(lng)
        writeFloat(price)
        writeString(urlImage)
        writeBoolean(ocupada)
        writeString(owner)
    }

    override fun toString(): String {
        return "Cochera(nombre='$nombre', direccion='$direccion', lat='$lat', lng='$lng', price='$price', urlImage='$urlImage', ocupada='$ocupada', owner='$owner')"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Cochera> = object : Parcelable.Creator<Cochera> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun createFromParcel(source: Parcel): Cochera = Cochera(source)
            override fun newArray(size: Int): Array<Cochera?> = arrayOfNulls(size)
        }
    }
}