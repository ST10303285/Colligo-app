package com.varsitycollege.st10303285.colligoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.varsitycollege.st10303285.colligoapp.adapter.RideListAdapter
import com.varsitycollege.st10303285.colligoapp.repository.ApiRepository
import kotlinx.coroutines.launch

class RequestedRidesActivity : AppCompatActivity() {

    private val repo = ApiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_rides) // reuse layout

        val rv = findViewById<RecyclerView?>(R.id.rvRides) ?: run {
            Toast.makeText(this, "Ensure activity_my_rides.xml has rvRides", Toast.LENGTH_LONG).show()
            return
        }
        rv.layoutManager = LinearLayoutManager(this)
        loadRequested()
    }

    private fun loadRequested() {
        lifecycleScope.launch {
            val resp = repo.getRequestedRides()
            if (resp.isSuccessful) {
                val results = resp.body() ?: emptyList()
                // convert to simple ride list for adapter (each element has ride/request info)
                val mapped = results.map { item ->
                    // item: { rideId, requestId, ride, request }
                    val ride = (item["ride"] as? Map<*, *>) ?: mapOf<String, Any>()
                    val rideId = ride["id"] ?: item["rideId"]
                    val start = ride["start"] ?: ""
                    val destination = ride["destination"] ?: ""
                    mapOf("id" to rideId.toString(), "start" to start.toString(), "destination" to destination.toString())
                }
                findViewById<RecyclerView>(R.id.rvRides).adapter = RideListAdapter(mapped) { _ -> /* open if needed */ }
            } else {
                Toast.makeText(this@RequestedRidesActivity, "Failed to load requested rides", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
