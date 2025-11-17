package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var accountSection: LinearLayout
    private lateinit var languageOption: LinearLayout
    private lateinit var notificationsOption: LinearLayout
    private lateinit var helpOption: LinearLayout
    private lateinit var backButton: ImageView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        accountSection = findViewById(R.id.accountSection)
        languageOption = findViewById(R.id.languageOption)
        notificationsOption = findViewById(R.id.notificationsOption)
        helpOption = findViewById(R.id.helpOption)
        backButton = findViewById(R.id.backButton)
        btnLogout = findViewById(R.id.logoutButton)

        // clicks
        accountSection.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        languageOption.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        notificationsOption.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        helpOption.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            // clear backstack and return to LoginActivity
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }
}