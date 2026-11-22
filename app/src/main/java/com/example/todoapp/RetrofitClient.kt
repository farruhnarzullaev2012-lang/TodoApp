package com.example.todoapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // localhost:3000 — "adb reverse tcp:3000 tcp:3000" orqali qurilma (emulyator yoki
    // USB orqali ulangan real telefon)ning localhost:3000'i host kompyuterning
    // localhost:3000'iga yo'naltiriladi. Shu usul ikkalasi uchun ham ishlaydi.
    private const val BASE_URL = "http://localhost:3000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
