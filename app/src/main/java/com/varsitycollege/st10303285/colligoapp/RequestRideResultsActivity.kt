package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.varsitycollege.st10303285.colligoapp.adapter.RideListAdapter
import com.varsitycollege.st10303285.colligoapp.repository.ApiRepository
import kotlinx.coroutines.launch

class RequestRideResultsActivity : AppCompatActivity() {

    private val api = ApiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_ride_results)

        val rv = findViewById<RecyclerView>(R.id.rvRides)
        rv.layoutManager = LinearLayoutManager(this)

        val pickup = intent.getStringExtra("pickup") ?: ""
        val dest = intent.getStringExtra("destination") ?: ""

        lifecycleScope.launch {
            val response = api.getRides()
            if (response.isSuccessful) {
                val rides = response.body() ?: emptyList()
                val filtered = rides.filter {
                    it["start"] == pickup && it["destination"] == dest
                }
                rv.adapter = RideListAdapter(filtered) { ride ->
                    val rideId = ride["id"].toString()
                    sendRequest(rideId)
                }
            }
        }
    }

    private fun sendRequest(rideId: String) {
        lifecycleScope.launch {
            val response = api.requestSeat(rideId, mapOf("seatsRequested" to 1))
            if (response.isSuccessful) {
                val i = Intent(this@RequestRideResultsActivity, RequestSentActivity::class.java)
                startActivity(i)
            } else {
                Toast.makeText(this@RequestRideResultsActivity, "Failed to request", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
