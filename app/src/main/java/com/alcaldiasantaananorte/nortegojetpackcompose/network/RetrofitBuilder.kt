package com.alcaldiasantaananorte.nortegojetpackcompose.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitBuilder {

    private const val BASE_URL = "http://192.168.1.29:8080/api/"
    const val urlImagenes = "http://192.168.1.29:8080/storage/archivos/"

    private val client: OkHttpClient = buildClient()
    private val retrofit: Retrofit = buildRetrofit()

    // Instancia singleton de ApiService
    private val apiService: ApiService = createServiceNoAuth(ApiService::class.java)

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Connection", "close")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    // Método reutilizable para crear servicios
    private fun <T> createServiceNoAuth(service: Class<T>): T {
        return retrofit.create(service)
    }

    // Exponer la instancia singleton del ApiService
    fun getApiService(): ApiService = apiService
}