package com.rybnickov.taxidrivers.api

import android.content.Context
import com.rybnickov.taxidrivers.models.*
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaxiApiClient {

    @GET("Login/{phone}/{password}")
    fun login(@Path("phone") phone:String, @Path("password") password:String):Observable<Driver>

    @GET("ActiveOrders")
    fun getActiveOrders():Observable<List<Order>>

    @GET("driverHasActiveOrder/{driver_id}")
    fun hasActiveOrder(@Path("driver_id") id:Int):Observable<Order>

    @GET("TakeAnOrder/{order_id}/{driver_id}")
    fun takeAnOrder(@Path("order_id") order_id:Int, @Path("driver_id") driver_id:Int):Observable<MessageResponse>

    @GET("EndOrder/{order_id}")
    fun endOrder(@Path("order_id") order_id: Int) : Observable<MessageResponse>

    @GET("Arrived/{order_id}")
    fun confirmArrivement(@Path("order_id") order_id: Int) : Observable<MessageResponse>

    @PUT("DeclineOrder/{order_id}")
    fun declineOrder(@Path("order_id") order_id: Int) : Observable<MessageResponse>

    @GET("GetMonthlyStats/{driver_id}")
    fun getMonthlyStats(@Path("driver_id") driver_id: Int):Observable<Statistic>

    @GET("GetTodaysStats/{driver_id}")
    fun getTodaysStats(@Path("driver_id") driver_id: Int):Observable<Statistic>


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