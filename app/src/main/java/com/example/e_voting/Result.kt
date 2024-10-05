package com.example.e_voting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Result : AppCompatActivity() {

    private lateinit var spinnerLocations: Spinner
    private lateinit var textViewResults: TextView

    //Firebase firestore instance
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerLocations = findViewById(R.id.spinner)


        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.locations_array,
            android.R.layout.simple_spinner_item
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinnerLocations.adapter = adapter

        // Set a listener for item selection
        spinnerLocations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLocation = parent.getItemAtPosition(position).toString()
                Toast.makeText(this@Result, "Result in $selectedLocation", Toast.LENGTH_SHORT).show()
                displayVotingResults(selectedLocation)
            }



            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }

        }
    }
    private fun displayVotingResults(selectedLocation: String) {
        // Fetch candidate name
        val docRefCan = db.collection("Regions").document(selectedLocation)
        docRefCan.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val candidatesList = document.get("candidates") as? List<Map<String, Any>> ?: emptyList()
                    val formattedCandidates = candidatesList.map { candidate ->
                        // Fetch the vote as a Long (or use a default value of 0 if null)
                        val voteCount = (candidate["vote"] as? Long) ?: 0L
                        // Return a Triple containing name, party, and vote count
                        Triple(candidate["name"] as String, candidate["party"] as String, voteCount)
                    }


                    // Call a method to update your RecyclerView with the list of formatted candidates
                    updateRecyclerView(formattedCandidates)
                } else {
                    Log.d("Firestore", "No such document for region: $selectedLocation")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching region document", exception)
            }
    }

    private fun updateRecyclerView(candidates: List<Triple<String, String, Long>>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewResult)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ResultAdapter(candidates)
        recyclerView.adapter = adapter

    }



}