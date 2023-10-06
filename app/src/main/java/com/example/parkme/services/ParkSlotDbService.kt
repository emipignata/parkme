package com.example.parkme.services

import com.example.parkme.database.PaginateResponse
import com.example.parkme.models.ParkSlot
import retrofit2.Call
import retrofit2.http.GET

interface ParkSlotDbService {
    @GET("/cocheras")
    fun listParkSlots(): Call<PaginateResponse<ParkSlot>>
}
