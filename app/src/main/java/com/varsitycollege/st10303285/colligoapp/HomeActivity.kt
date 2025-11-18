package com.varsitycollege.st10303285.colligoapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    // Feature cards (MaterialCardView but same IDs)
    private lateinit var plannerCard: View
    private lateinit var lostFoundCard: View
    private lateinit var carpoolCard: View
    private lateinit var mapCard: View

    // Schedule
    private lateinit var lectureRecycler: RecyclerView
    private val lectureAdapter = LectureAdapter()

    // Bottom navigation (from your included nav_bar.xml)
    private var navBar: LinearLayout? = null
    private var iconHome: ImageView? = null
    private var iconLocation: ImageView? = null
    private var iconCarpool: ImageView? = null
    private var iconCalendar: ImageView? = null
    private var iconLostFound: ImageView? = null

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Bring bottom nav to front
        findViewById<LinearLayout>(R.id.bottomNav)?.apply {
            bringToFront()
            invalidate()
        }

        // Enable offline persistence
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        initializeViews()
        updateGreetingWithUserName()
        setupFeatureCardClicks()
        setupSchedule()
        setupBottomNavigation()
        ensureUserDocumentExists()
        registerFcmToken()
        requestNotificationPermission()
    }

    private fun initializeViews() {
        // Cards
        plannerCard = findViewById(R.id.plannerCard)
        lostFoundCard = findViewById(R.id.lostFoundCard)
        carpoolCard = findViewById(R.id.carpoolCard)
        mapCard = findViewById(R.id.mapCard)

        // RecyclerView
        lectureRecycler = findViewById(R.id.lectureRecycler)
        lectureRecycler.layoutManager = LinearLayoutManager(this)
        lectureRecycler.adapter = lectureAdapter

        // Top bar buttons
        findViewById<ImageView>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnSearch)?.setOnClickListener {
            Toast.makeText(this, "Search coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateGreetingWithUserName() {
        val user = Firebase.auth.currentUser
        val greetingView = findViewById<TextView>(R.id.tvGreeting)

        if (user != null) {
            val name = when {
                !user.displayName.isNullOrBlank() -> user.displayName!!.split(" ").first()
                !user.email.isNullOrBlank() -> {
                    val username = user.email!!.substringBefore("@")
                    username.replace(".", " ")
                        .split(" ")
                        .joinToString(" ") { it.capitalize() }
                }
                else -> "Student"
            }
            greetingView.text = "Hi, $name!"
        } else {
            greetingView.text = "Welcome!"
        }
    }

    private fun setupFeatureCardClicks() {
        plannerCard.setOnClickListener { startActivity(Intent(this, PlannerActivity::class.java)) }
        lostFoundCard.setOnClickListener { startActivity(Intent(this, LostFoundActivity::class.java)) }
        carpoolCard.setOnClickListener { startActivity(Intent(this, RidesDashboardActivity::class.java)) }
        mapCard.setOnClickListener { startActivity(Intent(this, CampusMapActivity::class.java)) }
    }

    private fun setupSchedule() {
        loadScheduleFromFirestore()

        firestore.collection("schedule")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lectures = snapshot?.documents?.mapNotNull {
                    LectureItem(
                        title = it.getString("title") ?: "Lecture",
                        time = it.getString("time") ?: ""
                    )
                } ?: emptyList()

                lectureAdapter.setItems(lectures)
            }
    }

    private fun loadScheduleFromFirestore() {
        firestore.collection("schedule")
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.mapNotNull {
                    LectureItem(
                        title = it.getString("title") ?: "Subject",
                        time = it.getString("time") ?: "--:--"
                    )
                }
                lectureAdapter.setItems(list)
            }
    }

    private fun setupBottomNavigation() {
        navBar = findViewById(R.id.bottomNav)
        if (navBar == null) {
            Log.w("HomeActivity", "bottomNav not found")
            return
        }

        // Handle notch & gesture navigation padding
        ViewCompat.setOnApplyWindowInsetsListener(navBar!!) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        // Find your icons from nav_bar.xml
        iconHome = navBar!!.findViewById(R.id.iconHome)
        iconLocation = navBar!!.findViewById(R.id.iconLocation)
        iconCarpool = navBar!!.findViewById(R.id.iconCarpool)
        iconCalendar = navBar!!.findViewById(R.id.iconCalendar)
        iconLostFound = navBar!!.findViewById(R.id.iconLostFound)

        // Click listeners – same as your original code
        iconHome?.setOnClickListener {
            findViewById<NestedScrollView>(R.id.scrollContent)?.smoothScrollTo(0, 0)
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
        }

        iconLocation?.setOnClickListener {
            startActivity(Intent(this, CampusMapActivity::class.java))
        }

        iconCarpool?.setOnClickListener {
            startActivity(Intent(this, RidesDashboardActivity::class.java))
        }

        iconCalendar?.setOnClickListener {
            startActivity(Intent(this, PlannerActivity::class.java))
        }

        iconLostFound?.setOnClickListener {
            startActivity(Intent(this, LostFoundActivity::class.java))
        }
    }

    private fun ensureUserDocumentExists() {
        val user = Firebase.auth.currentUser ?: return
        val ref = firestore.collection("users").document(user.uid)

        ref.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                val data = hashMapOf(
                    "email" to (user.email ?: ""),
                    "fullName" to (user.displayName ?: ""),
                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                ref.set(data, SetOptions.merge())
            }
        }
    }

    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            val token = task.result
            Log.d("FCM", "Token: $token")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repo = com.varsitycollege.st10303285.colligoapp.repository.ApiRepository()
                    repo.registerFcmToken(token)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed", e)
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }
}

// Data class & clean adapter
data class LectureItem(val title: String, val time: String)

class LectureAdapter : RecyclerView.Adapter<LectureAdapter.ViewHolder>() {
    private val items = mutableListOf<LectureItem>()

    fun setItems(newItems: List<LectureItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.time.text = items[position].time
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1)
        val time: TextView = view.findViewById(android.R.id.text2)
    }
}