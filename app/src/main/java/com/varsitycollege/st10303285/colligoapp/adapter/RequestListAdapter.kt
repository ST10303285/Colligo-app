package com.varsitycollege.st10303285.colligoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.varsitycollege.st10303285.colligoapp.R

class RequestListAdapter(
    private val requests: List<Map<String, Any>>,
    private val onAccept: (String) -> Unit,
    private val onDecline: (String) -> Unit
) : RecyclerView.Adapter<RequestListAdapter.ReqVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReqVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return ReqVH(v)
    }

    override fun onBindViewHolder(holder: ReqVH, position: Int) {
        val req = requests[position]
        holder.riderId.text = req["riderId"].toString()
        val requestId = req["id"].toString()
        holder.accept.setOnClickListener { onAccept(requestId) }
        holder.decline.setOnClickListener { onDecline(requestId) }
    }

    override fun getItemCount() = requests.size

    class ReqVH(v: View) : RecyclerView.ViewHolder(v) {
        val riderId: TextView = v.findViewById(R.id.tvRiderName)
        val accept: Button = v.findViewById(R.id.btnAccept)
        val decline: Button = v.findViewById(R.id.btnDecline)
    }
}
