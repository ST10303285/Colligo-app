package com.varsitycollege.st10303285.colligoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varsitycollege.st10303285.colligoapp.repository.ApiRepository
import kotlinx.coroutines.launch

class OfferRideViewModel : ViewModel() {

    private val repo = ApiRepository()

    fun postRide(body: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val response = repo.createRide(body)
            if (response.isSuccessful) {
                val id = response.body()?.get("id")?.toString()
                callback(true, id)
            } else {
                callback(false, null)
            }
        }
    }
}
