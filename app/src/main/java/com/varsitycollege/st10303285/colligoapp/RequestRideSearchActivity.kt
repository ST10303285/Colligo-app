package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.varsitycollege.st10303285.colligoapp.SearchRideViewModel

class RequestRideSearchActivity : AppCompatActivity() {

    private lateinit var vm: SearchRideViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_ride_search)

        vm = ViewModelProvider(this)[SearchRideViewModel::class.java]

        val etStart = findViewById<EditText>(R.id.etPickup)
        val etDest = findViewById<EditText>(R.id.etDestination)
        val btnSearch = findViewById<Button>(R.id.btnSearchRides)

        btnSearch.setOnClickListener {
            val i = Intent(this, RequestRideResultsActivity::class.java)
            i.putExtra("pickup", etStart.text.toString())
            i.putExtra("destination", etDest.text.toString())
            startActivity(i)
        }
    }
}
