package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RidesDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rides_dashboard)

        val btnOffer = findViewById<Button>(R.id.btnOfferRide)
        val btnRequest = findViewById<Button>(R.id.btnRequestRide)

        btnOffer.setOnClickListener {
            startActivity(Intent(this, OfferRideActivity::class.java))
        }

        btnRequest.setOnClickListener {
            startActivity(Intent(this, RequestRideSearchActivity::class.java))
        }
    }
}
