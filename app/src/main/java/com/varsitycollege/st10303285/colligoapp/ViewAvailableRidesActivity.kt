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

class ViewAvailableRidesActivity : AppCompatActivity() {

    private val repo = ApiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_ride_results) // reuse layout

        val rv = findViewById<RecyclerView>(R.id.rvRides)
        rv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val resp = repo.getRides()
                if (resp.isSuccessful) {
                    val list = resp.body() ?: emptyList()
                    rv.adapter = RideListAdapter(list) { ride ->
                        val rideId = ride["id"]?.toString() ?: return@RideListAdapter
                        val i = Intent(this@ViewAvailableRidesActivity, RequestRideActivity::class.java)
                        i.putExtra("rideId", rideId)
                        startActivity(i)
                    }
                } else {
                    Toast.makeText(this@ViewAvailableRidesActivity, "Failed to load rides", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ViewAvailableRidesActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
