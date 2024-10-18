package com.alcaldiasantaananorte.nortegojetpackcompose.network

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    //** SERVIDOR
    private const val BASE_URL = "http://145.223.120.223/api/"
    const val urlImagenes = "http://145.223.120.223/storage/archivos/"

    //** LOCAL
    //private const val BASE_URL = "http://192.168.0.122:8080/api/"
    //const val urlImagenes = "http://192.168.0.122:8080/storage/archivos/"

    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null
    private var authenticatedApiService: ApiService? = null

    fun getApiService(): ApiService {
        if (apiService == null) {
            val client = buildClientNoAuth()
            retrofit = buildRetrofit(client)
            apiService = createService(ApiService::class.java)
        }
        return apiService!!
    }

    fun getAuthenticatedApiService(token: String): ApiService {
        if (authenticatedApiService == null) {
            val client = buildClientWithAuth(token)
            retrofit = buildRetrofit(client)
            authenticatedApiService = createService(ApiService::class.java)
        }
        return authenticatedApiService!!
    }

    private fun buildClientNoAuth(): OkHttpClient {
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

    private fun buildClientWithAuth(token: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .header("Accept", "application/json")
                    .header("Connection", "close")
                    .header("Authorization", "Bearer $token")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    private fun buildRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()
    }

    private fun <T> createService(serviceClass: Class<T>): T {
        return retrofit!!.create(serviceClass)
    }
}