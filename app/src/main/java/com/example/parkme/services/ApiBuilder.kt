package com.example.parkme.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiBuilder {

    // #CONFIGURO LA URL BASE, TRAER DE ENV.VARS
    private val BASE_URL = "https://dog.ceo/"

    //  #CONFIGURO RETROFIT BUILDER:
    private val retrofit = Retrofit.Builder()
        //   #LE DIGO QUE URL BASE TIENE QUE USAR
        .baseUrl(BASE_URL)
        // #LE DIGO EL TRADUCTOR QUE TIENE QUE USAR
        .addConverterFactory(GsonConverterFactory.create())
        // #FINALMENTE BUILDEO
        .build()

    // #Creo la función para traer la cochera -------- en la medida que tengamos mas entidades vamos a tener que recrear esta funcion
    fun create(): CocheraService {
        return retrofit.create(CocheraService::class.java)
    }
}