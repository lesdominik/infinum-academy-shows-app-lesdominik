package com.shows_lesdominik

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<LoginAndRegisterResponse>

    @POST("/users/sign_in")
    fun signIn(@Body request: LoginRequest): Call<LoginAndRegisterResponse>
}