package com.example.e_voting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class CandidatesAdapter(
    private val candidates: List<String>,
    private val onCandidateSelected: (String) -> Unit // Pass a function to handle the selected candidate
) : RecyclerView.Adapter<CandidatesAdapter.CandidateViewHolder>() {

    // Variable to hold the currently selected candidate position
    var selectedPosition = -1

    class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val candidateCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false)
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidateName = candidates[position]

        // Bind the candidate's name to the CheckBox text
        holder.candidateCheckBox.text = candidateName

        // Set the checkbox checked state based on selectedPosition
        holder.candidateCheckBox.isChecked = (position == selectedPosition)

        // Handle checkbox click event
        holder.candidateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Use post to ensure the notifyItemChanged calls occur after layout computation
                holder.candidateCheckBox.post {
                    // Uncheck the previously selected checkbox
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(selectedPosition) // Uncheck previous checkbox
                    }
                    selectedPosition = holder.adapterPosition // Update selected position
                    // Pass the selected candidate name to the caller
                    onCandidateSelected(candidateName)
                    notifyItemChanged(selectedPosition) // Refresh current item
                }
            } else if (selectedPosition == holder.adapterPosition) {
                selectedPosition = RecyclerView.NO_POSITION // Reset if unchecked
                holder.candidateCheckBox.post {
                    notifyItemChanged(holder.adapterPosition) // Refresh current item
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return candidates.size
    }

}

