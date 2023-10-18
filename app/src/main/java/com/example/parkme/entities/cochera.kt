package com.example.parkme.entities

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputLayout

class Cochera() : Parcelable {
    private var nombre: String = ""
    private var direccion: String = ""
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var price: Float = 0.0f
    private var urlImage: String = ""
    private var ocupada: Boolean = false
    private var owner: String = ""

    constructor(
        nombre: String,
        direccion: String,
        lat: Double,
        lng: Double,
        price: Float,
        urlImage: String,
        ocupada: Boolean,
        owner: String
    ) : this() {
        this.nombre = nombre
        this.direccion = direccion
        this.lat = lat
        this.lng = lng
        this.price = price
        this.urlImage = urlImage
        this.ocupada = ocupada
        this.owner = owner
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(source: Parcel) : this() {
        nombre = source.readString() ?: ""
        direccion = source.readString() ?: ""
        lat = source.readDouble()
        lng = source.readDouble()
        price = source.readFloat()
        urlImage = source.readString() ?: ""
        ocupada = source.readBoolean()
        owner = source.readString() ?: ""
    }

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

    fun getNombre(): String {
        return nombre
    }

    fun setNombre(nombre: String) {
        this.nombre = nombre
    }

    fun getDireccion(): String {
        return direccion
    }

    fun setDireccion(direccion: String) {
        this.direccion = direccion
    }

    fun getLat(): Double {
        return lat
    }

    fun setLat(lat: Double) {
        this.lat = lat
    }

    fun getLng(): Double {
        return lng
    }

    fun setLng(lng: Double) {
        this.lng = lng
    }

    fun getPrice(): Float {
        return price
    }

    fun setPrice(price: Float) {
        this.price = price
    }

    fun getUrlImage(): String {
        return urlImage
    }

    fun setUrlImage(urlImage: String) {
        this.urlImage = urlImage
    }

    fun isOcupada(): Boolean {
        return ocupada
    }

    fun setOcupada(ocupada: Boolean) {
        this.ocupada = ocupada
    }

    fun getOwner(): String {
        return owner
    }

    fun setOwner(owner: String) {
        this.owner = owner
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
