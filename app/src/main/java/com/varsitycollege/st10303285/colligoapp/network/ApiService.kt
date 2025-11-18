package com.varsitycollege.st10303285.colligoapp.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    // RIDES
    //
    @POST("rides") // Create a new ride
    suspend fun createRide(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("rides") // Get rides with optional status filter
    suspend fun getRides(@Query("status") status: String = "open"): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("rides/mine") // Get rides created by the current user
    suspend fun getMyRides(): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("rides/requested") // Get rides requested by the current user
    suspend fun getRequestedRides(): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("rides/{rideId}") // Get details of a specific ride
    suspend fun getRideDetails(
        @Path("rideId") rideId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("rides/{rideId}/requests") // Get ride requests for a specific ride
    suspend fun getRideRequests(
        @Path("rideId") rideId: String
    ): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @POST("rides/{rideId}/requests") // Send a ride request for a specific ride
    suspend fun sendRideRequest(
        @Path("rideId") rideId: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/requests/{requestId}/accept") // Accept a ride request
    suspend fun acceptRequest(
        @Path("rideId") rideId: String,
        @Path("requestId") requestId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/requests/{requestId}/decline") // Decline a ride request
    suspend fun declineRequest(
        @Path("rideId") rideId: String,
        @Path("requestId") requestId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/cancel") // Cancel a ride
    suspend fun cancelRide(
        @Path("rideId") rideId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("rides/{rideId}/requests/{requestId}/cancel") // Cancel a seat request
    suspend fun cancelSeatRequest(
        @Path("rideId") rideId: String,
        @Path("requestId") requestId: String
    ): Response<Map<String, @JvmSuppressWildcards Any>>



    // LOST & FOUND
    @POST("lost")// Create a new lost item report
    suspend fun createLostItem(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("lost") // Get all lost items
    suspend fun getLostItems(): Response<List<Map<String, @JvmSuppressWildcards Any>>>

    @GET("lost/{id}") // Get details of a specific lost item
    suspend fun getLostItemDetails(@Path("id") id: String): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("lost/{id}/claim")
    suspend fun claimLostItem(@Path("id") id: String): Response<Map<String, @JvmSuppressWildcards Any>>

    @POST("lost/{id}/resolve")
    suspend fun resolveLostItem(@Path("id") id: String): Response<Map<String, @JvmSuppressWildcards Any>>



    // TIMETABLE
    @POST("timetable/{uid}/events")
    suspend fun createEvent(
        @Path("uid") uid: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("timetable/{uid}/events")
    suspend fun getEvents(
        @Path("uid") uid: String
    ): Response<List<Map<String, @JvmSuppressWildcards Any>>>



    // MAP

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
