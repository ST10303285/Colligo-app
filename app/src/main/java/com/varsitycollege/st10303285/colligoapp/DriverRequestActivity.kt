package com.varsitycollege.st10303285.colligoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.varsitycollege.st10303285.colligoapp.adapter.RequestListAdapter
import com.varsitycollege.st10303285.colligoapp.repository.ApiRepository
import kotlinx.coroutines.launch

class DriverRequestsActivity : AppCompatActivity() {

    private val repo = ApiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_request)

        val rv = findViewById<RecyclerView>(R.id.rvRideRequests)
        rv.layoutManager = LinearLayoutManager(this)

        val rideId = intent.getStringExtra("rideId") ?: run {
            Toast.makeText(this, "Missing rideId", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadRequests(rv, rideId)
    }

    private fun loadRequests(rv: RecyclerView, rideId: String) {
        lifecycleScope.launch {
            val resp = repo.getRideRequests(rideId)
            if (resp.isSuccessful) {
                val list = resp.body() ?: emptyList()
                rv.adapter = RequestListAdapter(list,
                    onAccept = { requestId -> acceptRequest(rideId, requestId) },
                    onDecline = { requestId -> declineRequest(rideId, requestId) })
            } else {
                Toast.makeText(this@DriverRequestsActivity, "Failed to load requests", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun acceptRequest(rideId: String, requestId: String) {
        lifecycleScope.launch {
            val resp = repo.acceptRequest(rideId, requestId)
            if (resp.isSuccessful) {
                Toast.makeText(this@DriverRequestsActivity, "Accepted", Toast.LENGTH_SHORT).show()
                loadRequests(findViewById(R.id.rvRideRequests), rideId)
            } else {
                Toast.makeText(this@DriverRequestsActivity, "Accept failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun declineRequest(rideId: String, requestId: String) {
        lifecycleScope.launch {
            val resp = repo.declineRequest(rideId, requestId)
            if (resp.isSuccessful) {
                Toast.makeText(this@DriverRequestsActivity, "Declined", Toast.LENGTH_SHORT).show()
                loadRequests(findViewById(R.id.rvRideRequests), rideId)
            } else {
                Toast.makeText(this@DriverRequestsActivity, "Decline failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
