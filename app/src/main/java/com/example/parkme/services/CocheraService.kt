package com.example.parkme.services


import com.example.parkme.models.CocheraModel
import com.example.parkme.models.PaginatedResponse
import retrofit2.Call
import retrofit2.http.GET

interface CocheraService {
    @GET("api/breeds/list/all")
    fun getCocheras(): Call<PaginatedResponse<CocheraModel>>

   // https://dog.ceo/api/breed/hound/images/random
    @GET("api/breed/hound/images/random")
    fun getCocheraImg(): Call<PaginatedResponse<CocheraModel>>
  /*  @GET("api/v2/ability")
    fun getPokemonAbility(): Call<PaginateResponse<Activity>>
   */
}
