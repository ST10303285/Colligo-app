package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OfferRideSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_ride_success)

        val rideId = intent.getStringExtra("rideId")
        val tv = findViewById<TextView>(R.id.tvRideId)
        tv.text = "Ride posted successfully!\nRide ID: $rideId"

        findViewById<Button>(R.id.btnViewMyRides).setOnClickListener {
            startActivity(Intent(this, MyRidesActivity::class.java))
        }

        findViewById<Button>(R.id.btnDashboard).setOnClickListener {
            startActivity(Intent(this, RidesDashboardActivity::class.java))
        }
    }
}
