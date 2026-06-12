package com.haiku.app.api

import retrofit2.http.Body
import retrofit2.http.POST

interface ClaudeApiService {

    @POST("v1/messages")
    suspend fun createMessage(@Body request: ClaudeRequest): ClaudeResponse
}
