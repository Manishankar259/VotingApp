package com.example.e_voting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultAdapter(
    private val results: List<Triple<String, String, Long>> // Triple (Name, Party, VoteCount)
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    // ViewHolder class for RecyclerView
    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultTextView: TextView = itemView.findViewById(R.id.resultList) // TextView in recycler_view_result layout
        val partyNameTextView: TextView = itemView.findViewById(R.id.partyName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        // Display the formatted result for each candidate (name, party, vote)
        val (name, party, voteCount) = results[position]
        val partyName = "- $party"

        // Set candidate name and vote count
        holder.resultTextView.text = "$name: $voteCount vote(s)"
        // Set party name
        holder.partyNameTextView.text = partyName

    }

    override fun getItemCount(): Int {
        return results.size
    }
}


