package com.rybnickov.taxidrivers.api

import android.content.Context
import com.rybnickov.taxidrivers.models.*
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface TaxiApiClient {

    @GET("Login/{phone}/{password}")
    fun login(@Path("phone") phone:String, @Path("password") password:String):Observable<Driver>

    @GET("ActiveOrders")
    fun getActiveOrders():Observable<List<Order>>

    @GET("hasActiveOrder/{driver_id}")
    fun hasActiveOrder(@Path("driver_id") id:Int):Observable<Driver>

    companion object {
        fun create(context: Context): TaxiApiClient {
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(ApiInterceptor(context))
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl("http://api.gfenixl.me/Rybn/")
                .build()

            return retrofit.create(TaxiApiClient::class.java)
        }

    }
}