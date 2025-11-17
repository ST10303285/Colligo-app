package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help) // create layout if missing

        val tvFaq = findViewById<TextView>(R.id.tvFaq)
        val btnContact = findViewById<Button>(R.id.btnContact)

        tvFaq.text = """
            FAQs
            1. How do I reset my password?
               - Use the Forgot Password option on the login screen.
            2. How do I report a bug?
               - Use Contact Support below and describe the steps.
            3. How do I change my language?
               - Settings > Language
        """.trimIndent()

        btnContact.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("support@colligo.example"))
                putExtra(Intent.EXTRA_SUBJECT, "Colligo App Support")
            }
            startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_support)))
        }
    }
}