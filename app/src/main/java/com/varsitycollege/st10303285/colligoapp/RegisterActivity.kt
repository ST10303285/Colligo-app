package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPrefs: android.content.SharedPreferences

    private lateinit var etFullName: EditText
    private lateinit var etUniversity: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoogleSignUp: Button

    private lateinit var googleClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            Toast.makeText(this, "Google Sign-Up failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        initializeViews()
        setupGoogleSignIn()

        btnRegister.setOnClickListener { performRegistration() }
        btnGoogleSignUp.setOnClickListener { googleSignInLauncher.launch(googleClient.signInIntent) }

        findViewById<TextView>(R.id.loginRedirect).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.fullNameEditText)
        etUniversity = findViewById(R.id.etUniversity)
        etEmail = findViewById(R.id.emailEditText)
        etPassword = findViewById(R.id.passwordEditText)
        etConfirmPassword = findViewById(R.id.confirmPasswordEditText)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)
    }

    private fun performRegistration() {
        val name = etFullName.text.toString().trim()
        val uni = etUniversity.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (name.isEmpty() || uni.isEmpty() || email.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                saveUserToFirestore(uid, email, name, uni)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveUserToFirestore(uid: String, email: String, name: String, university: String) {
        val userData = hashMapOf(
            "email" to email,
            "fullName" to name,
            "university" to university,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(uid).set(userData)
            .addOnSuccessListener {
                saveUserSession(email)
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                goToHome()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save profile", Toast.LENGTH_LONG).show()
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = auth.currentUser ?: return@addOnSuccessListener
                val uid = user.uid
                val email = user.email.orEmpty()
                val name = user.displayName.orEmpty()

                // Save minimal profile if using Google
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        if (!doc.exists()) {
                            saveUserToFirestore(uid, email, name, "Not specified")
                        } else {
                            saveUserSession(email)
                            goToHome()
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Google Sign-Up failed", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveUserSession(email: String) {
        sharedPrefs.edit()
            .putString("last_logged_in_email", email)
            .putBoolean("biometrics_enabled", true)
            .apply()
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}