package com.shows_lesdominik

import okhttp3.MultipartBody
import okhttp3.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<LoginAndRegisterResponse>

    @POST("/users/sign_in")
    fun signIn(@Body request: LoginRequest): Call<LoginAndRegisterResponse>

    @GET("/shows")
    fun getShows(): Call<ShowsResponse>

    @GET("/shows/{id}")
    fun getShowDetails(@Path("id") id: String): Call<ShowDetailsResponse>

    @GET("/shows/{show_id}/reviews")
    fun getShowReviews(@Path("show_id") showId: String): Call<ReviewListResponse>

    @POST("/reviews")
    fun createShowReview(@Body request: ReviewCreateRequest): Call<ReviewCreateResponse>

    @PUT("/users")
    @Multipart
    fun storeUserImage(@Part requestBodyPart: MultipartBody.Part): Call<StoreImageResponse>

    @GET("/users/me")
    fun getUserInfo(): Call<UserInfoResponse>
}