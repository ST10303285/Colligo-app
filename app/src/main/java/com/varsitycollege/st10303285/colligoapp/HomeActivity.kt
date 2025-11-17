package com.varsitycollege.st10303285.colligoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class HomeActivity : AppCompatActivity() {

    private lateinit var plannerCard: LinearLayout
    private lateinit var lostFoundCard: LinearLayout
    private lateinit var carpoolCard: LinearLayout
    private lateinit var mapCard: LinearLayout

    private lateinit var lectureRecycler: RecyclerView
    private val lecturesAdapter = LectureAdapter()

    // Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // nav views (nullable until found)
    private var navBar: LinearLayout? = null
    private var iconHome: ImageView? = null
    private var iconLocation: ImageView? = null
    private var iconCarpool: ImageView? = null
    private var iconCalendar: ImageView? = null
    private var iconLostFound: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val nav = findViewById<LinearLayout>(R.id.bottomNav)
        nav?.bringToFront()
        nav?.invalidate()


        // Enable offline persistence (simple)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings

        // view binding for cards
        plannerCard = findViewById(R.id.plannerCard)
        lostFoundCard = findViewById(R.id.lostFoundCard)
        carpoolCard = findViewById(R.id.carpoolCard)
        mapCard = findViewById(R.id.mapCard)
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Recycler
        lectureRecycler = findViewById(R.id.lectureRecycler)
        lectureRecycler.layoutManager = LinearLayoutManager(this)
        lectureRecycler.adapter = lecturesAdapter

        // click listeners - adjust Target activities
        plannerCard.setOnClickListener {
            startActivity(Intent(this, PlannerActivity::class.java))
        }

        lostFoundCard.setOnClickListener {
            startActivity(Intent(this, LostFoundActivity::class.java))
        }

        carpoolCard.setOnClickListener {
            startActivity(Intent(this, CarpoolActivity::class.java))
        }

        mapCard.setOnClickListener {
            startActivity(Intent(this, CampusMapActivity::class.java))
        }



        loadSchedule()
        setupRealtimeScheduleListener()
        setupNavBarAndInsets()
        setupNavClicks()
    }

    fun goToHome(view: View) {
        // We're already on HomeActivity; optionally scroll to top
        val scroll = findViewById<View?>(R.id.scrollContent)
        scroll?.let {
            it.scrollTo(0, 0)
        }
    }

    private fun loadSchedule() {
        // For UI demo, try fetch cached lecture list from Firestore collection "schedule"
        firestore.collection("schedule")
            .limit(10)
            .get()
            .addOnSuccessListener { snap ->
                val items = snap.documents.mapNotNull { doc ->
                    LectureItem(
                        title = doc.getString("title") ?: "Lecture",
                        time = doc.getString("time") ?: "09:00"
                    )
                }
                lecturesAdapter.setItems(items)
            }
            .addOnFailureListener {
                // fallback: show sample entries
                lecturesAdapter.setItems(
                    listOf(
                        LectureItem("Programming 3D", "08:00"),
                        LectureItem("Database Systems", "10:00")
                    )
                )
            }
    }

    private fun setupRealtimeScheduleListener() {
        // demonstrates real-time updates — useful for the schedule card
        firestore.collection("schedule")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("HomeActivity", "schedule listener error", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        LectureItem(doc.getString("title") ?: "Lecture", doc.getString("time") ?: "")
                    }
                    lecturesAdapter.setItems(items)
                }
            }
    }

    private fun setupNavBarAndInsets() {
        // get nav bar and icons (guard for null if include id differs)
        navBar = findViewById(R.id.bottomNav)
        if (navBar == null) {
            Log.w("HomeActivity", "bottomNav view not found. Ensure include id '@+id/bottomNav' exists.")
            return
        }

        // apply system bar insets so nav won't overlap navigation gestures/buttons
        ViewCompat.setOnApplyWindowInsetsListener(navBar!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // keep top/left/right padding, only adjust bottom
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            // return insets so other listeners can use them
            insets
        }

        // find icons inside the nav bar
        iconHome = navBar!!.findViewById(R.id.iconHome)
        iconLocation = navBar!!.findViewById(R.id.iconLocation)
        iconCarpool = navBar!!.findViewById(R.id.iconCarpool)
        iconCalendar = navBar!!.findViewById(R.id.iconCalendar)
        iconLostFound = navBar!!.findViewById(R.id.iconLostFound)
    }

    private fun setupNavClicks() {
        // navBar might be null (guard)
        if (navBar == null) return

        // Home -> no-op (we're already here). We still handle click to scroll top.
        iconHome?.setOnClickListener {
            // optional: scroll to top
            val scroll = findViewById<View?>(R.id.scrollContent)
            scroll?.scrollTo(0, 0)
            // small feedback so user knows we're already on home
            Toast.makeText(this, getString(R.string.home_icon_desc), Toast.LENGTH_SHORT).show()
        }

        iconLocation?.setOnClickListener {
            // launch CampusMapActivity (or Location feature)
            startActivity(Intent(this, CampusMapActivity::class.java))
        }

        iconCarpool?.setOnClickListener {
            startActivity(Intent(this, CarpoolActivity::class.java))
        }

        iconCalendar?.setOnClickListener {
            startActivity(Intent(this, PlannerActivity::class.java))
        }

        iconLostFound?.setOnClickListener {
            startActivity(Intent(this, LostFoundActivity::class.java))
        }
    }
}

/* ----- Lecture data + Adapter (kept in same file for convenience) ----- */

data class LectureItem(val title: String, val time: String)

class LectureAdapter : RecyclerView.Adapter<LectureAdapter.LecVH>() {
    private val items = mutableListOf<LectureItem>()
    fun setItems(new: List<LectureItem>) {
        items.clear()
        items.addAll(new)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): LecVH {
        val v = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return LecVH(v)
    }

    override fun onBindViewHolder(holder: LecVH, position: Int) {
        val it = items[position]
        holder.title.text = it.title
        holder.sub.text = it.time
    }

    override fun getItemCount(): Int = items.size

    class LecVH(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val title: android.widget.TextView = view.findViewById(android.R.id.text1)
        val sub: android.widget.TextView = view.findViewById(android.R.id.text2)
    }
}