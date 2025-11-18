package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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

        accountSection.setOnClickListener { startActivity(Intent(this, AccountActivity::class.java)) }
        languageOption.setOnClickListener { startActivity(Intent(this, LanguageActivity::class.java)) }
        notificationsOption.setOnClickListener { startActivity(Intent(this, NotificationSettingsActivity::class.java)) }
        helpOption.setOnClickListener { startActivity(Intent(this, HelpActivity::class.java)) }
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        btnLogout.setOnClickListener {
            // 1. Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // 2. Sign out from Google (prevents silent re-login)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            GoogleSignIn.getClient(this, gso).signOut()

            // 3. Go back to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}