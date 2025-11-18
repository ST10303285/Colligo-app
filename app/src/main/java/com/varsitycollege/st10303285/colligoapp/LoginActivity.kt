package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogleSignIn: MaterialButton
    private lateinit var btnBiometricLogin: MaterialButton

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: java.util.concurrent.Executor

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            // User canceled or error
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // If already signed in → go home
        if (auth.currentUser != null) {
            goToHome()
            return
        }

        initViews()
        setupGoogleSignIn()
        setupBiometric()
        updateBiometricButtonState()

        btnLogin.setOnClickListener { performEmailLogin() }
        btnGoogleSignIn.setOnClickListener { launchGoogleSignIn() }
        btnBiometricLogin.setOnClickListener { showBiometricPrompt() }

        findViewById<android.widget.TextView>(R.id.registerRedirect)?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.passwordEditText)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        btnBiometricLogin = findViewById(R.id.btnBiometricLogin)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)
    }

    private fun launchGoogleSignIn() {
        googleClient.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(googleClient.signInIntent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                markUserHasLoggedInOnce()
                goToHome()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Google login failed", Toast.LENGTH_LONG).show()
            }
    }

    private fun performEmailLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                markUserHasLoggedInOnce()
                goToHome()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupBiometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@LoginActivity, "Welcome back!", Toast.LENGTH_SHORT).show()
                goToHome()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@LoginActivity, "Not recognized", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(this@LoginActivity, "Biometric error: $errString", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateBiometricButtonState() {
        val hasLoggedInOnce = getSharedPreferences("colligo_prefs", MODE_PRIVATE)
            .getBoolean("has_logged_in_once", false)

        val canUseBiometric = BiometricManager.from(this)
            .canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

        val shouldEnable = hasLoggedInOnce && canUseBiometric

        btnBiometricLogin.isEnabled = shouldEnable
        btnBiometricLogin.alpha = if (shouldEnable) 1.0f else 0.6f
        btnBiometricLogin.text = if (shouldEnable)
            "Login with Fingerprint / Face"
        else
            "Log in once to enable biometric"
        btnBiometricLogin.setIconResource(if (shouldEnable) R.drawable.ic_fingerprint else 0)
    }

    private fun showBiometricPrompt() {
        if (!btnBiometricLogin.isEnabled) {
            Toast.makeText(this, "Please log in once with email or Google first", Toast.LENGTH_LONG).show()
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Quick Login")
            .setSubtitle("Use fingerprint or face")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun markUserHasLoggedInOnce() {
        getSharedPreferences("colligo_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("has_logged_in_once", true)
            .apply()
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}