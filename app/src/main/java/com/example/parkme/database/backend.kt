package com.example.parkme.database

import com.example.parkme.services.ParkSlotDbService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object backend {

    val URL_BASE = "http://mockserver.kubernights.com:8080"
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(ParkSlotDbService::class.java)
}
