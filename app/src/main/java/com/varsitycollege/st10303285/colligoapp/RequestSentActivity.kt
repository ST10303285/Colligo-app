package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RequestSentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_sent)

        findViewById<Button>(R.id.btnSearchMore).setOnClickListener {
            startActivity(Intent(this, RequestRideSearchActivity::class.java))
        }
    }
}
