package com.varsitycollege.st10303285.colligoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.varsitycollege.st10303285.colligoapp.R

class RideListAdapter(
    private val rides: List<Map<String, Any>>,
    private val onClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<RideListAdapter.RideVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ride, parent, false)
        return RideVH(v)
    }

    override fun onBindViewHolder(holder: RideVH, position: Int) {
        val ride = rides[position]
        holder.start.text = ride["start"].toString()
        holder.dest.text = ride["destination"].toString()
        holder.btnRequest.setOnClickListener { onClick(ride) }
    }

    override fun getItemCount() = rides.size

    class RideVH(v: View) : RecyclerView.ViewHolder(v) {
        val start: TextView = v.findViewById(R.id.tvStart)
        val dest: TextView = v.findViewById(R.id.tvDestination)
        val btnRequest: Button = v.findViewById(R.id.btnRequestRide)
    }
}
