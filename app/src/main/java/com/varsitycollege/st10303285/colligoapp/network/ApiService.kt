package com.varsitycollege.st10303285.colligoapp.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------------------------------------------------------
    // RIDES
    // ---------------------------------------------------------

    @POST("rides")
    suspend fun createRide(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("rides")
    suspend fun getRides(@Query("status") status: String = "open"): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("rides/mine")
    suspend fun getMyRides(): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("rides/requested")
    suspend fun getRequestedRides(): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("rides/{rideId}")
    suspend fun getRideDetails(@Path("rideId") rideId: String): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("rides/{rideId}/requests")
    suspend fun getRideRequests(
        @Path("rideId") rideId: String
    ): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @POST("rides/{rideId}/requests")
    suspend fun sendRideRequest(
        @Path("rideId") rideId: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/requests/{requestId}/accept")
    suspend fun acceptRequest(
        @Path("rideId") rideId: String,
        @Path("requestId") requestId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/requests/{requestId}/decline")
    suspend fun declineRequest(
        @Path("rideId") rideId: String,
        @Path("requestId") requestId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/cancel")
    suspend fun cancelRide(
        @Path("rideId") rideId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/requests/{requestId}/cancel")
    suspend fun cancelSeatRequest(
        @Path("rideId") rideId: String,
        @Path("requestId") requestId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>


    // ---------------------------------------------------------
    // LOST & FOUND
    // ---------------------------------------------------------

    @POST("lost")
    suspend fun createLostItem(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("lost")
    suspend fun getLostItems(): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("lost/{id}")
    suspend fun getLostItemDetails(@Path("id") id: String): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("lost/{id}/claim")
    suspend fun claimLostItem(@Path("id") id: String): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("lost/{id}/resolve")
    suspend fun resolveLostItem(@Path("id") id: String): Response<Map<String, @JvmSuppressWildcards Any>>


    // ---------------------------------------------------------
    // TIMETABLE
    // ---------------------------------------------------------

    @POST("timetable/{uid}/events")
    suspend fun createEvent(
        @Path("uid") uid: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("timetable/{uid}/events")
    suspend fun getEvents(
        @Path("uid") uid: String
    ): Response<List<Map<String, @JvmSuppressWildcards Any>>>


    // ---------------------------------------------------------
    // MAP
    // ---------------------------------------------------------

    @GET("map/locations")
    suspend fun getLocations(): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @POST("map/locations")
    suspend fun addLocation(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, @JvmSuppressWildcards Any>>


    // FCM TOKENS

    @POST("users/{uid}/fcm-token")
    suspend fun registerFcmToken(
        @Path("uid") uid: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @DELETE("users/{uid}/fcm-token")
    suspend fun removeFcmToken(
        @Path("uid") uid: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, @JvmSuppressWildcards Any>>
}
