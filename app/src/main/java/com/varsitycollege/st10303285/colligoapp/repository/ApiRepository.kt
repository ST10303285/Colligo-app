package com.varsitycollege.st10303285.colligoapp.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.varsitycollege.st10303285.colligoapp.network.ApiService
import com.varsitycollege.st10303285.colligoapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiRepository {

    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private fun uid(): String? = Firebase.auth.currentUser?.uid

    // RIDES

    suspend fun createRide(body: Map<String, Any>) =
        withContext(Dispatchers.IO) { api.createRide(body) }

    suspend fun getRides(status: String = "open") =
        withContext(Dispatchers.IO) { api.getRides(status) }

    suspend fun getMyRides() =
        withContext(Dispatchers.IO) { api.getMyRides() }

    suspend fun getRequestedRides() =
        withContext(Dispatchers.IO) { api.getRequestedRides() }

    suspend fun getRideDetails(rideId: String) =
        withContext(Dispatchers.IO) { api.getRideDetails(rideId) }

    suspend fun getRideRequests(rideId: String) =
        withContext(Dispatchers.IO) { api.getRideRequests(rideId) }

    suspend fun requestSeat(rideId: String, body: Map<String, Any>) =
        withContext(Dispatchers.IO) { api.sendRideRequest(rideId, body) }

    suspend fun acceptRequest(rideId: String, requestId: String) =
        withContext(Dispatchers.IO) { api.acceptRequest(rideId, requestId) }

    suspend fun declineRequest(rideId: String, requestId: String) =
        withContext(Dispatchers.IO) { api.declineRequest(rideId, requestId) }

    suspend fun cancelRide(rideId: String) =
        withContext(Dispatchers.IO) { api.cancelRide(rideId) }

    suspend fun cancelSeatRequest(rideId: String, requestId: String) =
        withContext(Dispatchers.IO) { api.cancelSeatRequest(rideId, requestId) }


    // LOST & FOUND

    suspend fun createLostItem(body: Map<String, Any>) =
        withContext(Dispatchers.IO) { api.createLostItem(body) }

    suspend fun getLostItems() =
        withContext(Dispatchers.IO) { api.getLostItems() }

    suspend fun getLostItemDetails(id: String) =
        withContext(Dispatchers.IO) { api.getLostItemDetails(id) }

    suspend fun claimLostItem(id: String) =
        withContext(Dispatchers.IO) { api.claimLostItem(id) }

    suspend fun resolveLostItem(id: String) =
        withContext(Dispatchers.IO) { api.resolveLostItem(id) }



    // TIMETABLE
    suspend fun createEvent(body: Map<String, Any>) =
        withContext(Dispatchers.IO) {
            val uid = uid() ?: return@withContext null
            api.createEvent(uid, body)
        }

    suspend fun getEvents() =
        withContext(Dispatchers.IO) {
            val uid = uid() ?: return@withContext null
            api.getEvents(uid)
        }



    // MAP

    suspend fun getLocations() =
        withContext(Dispatchers.IO) { api.getLocations() }

    suspend fun addLocation(body: Map<String, Any>) =
        withContext(Dispatchers.IO) { api.addLocation(body) }



    // FCM TOKENS


    suspend fun registerFcmToken(token: String) = withContext(Dispatchers.IO) {
        val uid = Firebase.auth.currentUser?.uid ?: return@withContext null
        api.registerFcmToken(uid, mapOf("token" to token))
    }

    suspend fun removeFcmToken(token: String) =
        withContext(Dispatchers.IO) {
            val user = uid() ?: return@withContext null
            api.removeFcmToken(user, mapOf("token" to token))
        }
}
