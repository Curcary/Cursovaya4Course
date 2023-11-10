package com.rybnickov.taxidrivers.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface TaxiApiClient {

    @GET("Login/{phone}/{password}")
    fun login()

    companion object {
        fun create(context: Context): TaxiApiClient {
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(ApiInterceptor(context))
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl("http://45.9.73.84:4129/Rybn/")
                .build()

            return retrofit.create(TaxiApiClient::class.java)
        }

    }
}