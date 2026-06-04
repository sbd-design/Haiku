package com.haiku.app.api

import com.haiku.app.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClaudeApiServiceProvider @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    // Re-creates the service each call so it picks up the latest API key from DataStore.
    // In practice the key rarely changes, and Retrofit + OkHttp handle connection pooling.
    suspend fun get(): ClaudeApiService {
        val apiKey = settingsDataStore.apiKey.first()

        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("x-api-key", apiKey)
                        .header("anthropic-version", "2023-06-01")
                        .header("content-type", "application/json")
                        .build()
                )
            })
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClaudeApiService::class.java)
    }
}
