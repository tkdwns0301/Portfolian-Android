package com.example.portfolian.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var instance: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    private const val BASE_URL = "http://3.36.84.11:3000/"
    private const val CONNECT_TIMEOUT_SEC = 20000L

    fun getInstance(): Retrofit {
        if(instance == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .build()

            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        return instance!!
    }
}