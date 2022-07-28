package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ApiModule {
    private const val BASE_URL = "https://tv-shows.infinum.academy/"

    lateinit var retrofit: ShowsApiService

    fun initRetrofit(context: Context) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)))
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }
}

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        val uid = sharedPreferences.getString("USER_EMAIL", null)
        val client = sharedPreferences.getString("CLIENT", null)
        var request = chain.request()
//        if (!accessToken.isNullOrEmpty()) {
//        }
        request = request.newBuilder()
            .header("token-type", "Bearer")
            .header("access-token", accessToken.toString())
            .header("uid", uid.toString())
            .header("client", client.toString())
            .build()

        return chain.proceed(request)
    }
}