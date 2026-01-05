package com.besheger.sonar.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import com.besheger.sonar.data.local.entity.SonarTrackEntity

interface SonarApiService {

    // Fetch tracks stored on the server
    @GET("tracks")
    suspend fun getRemoteTracks(): List<SonarTrackEntity>

    // Upload a local audio file
    @Multipart
    @POST("upload")
    suspend fun uploadTrack(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("artist") artist: RequestBody,
        @Part("category") category: RequestBody
    ): retrofit2.Response<Unit>
}