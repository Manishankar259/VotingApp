package com.example.e_voting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class VoteActivity : AppCompatActivity() {

    private lateinit var  editTextZipcode: EditText
    private  lateinit var buttonSearchRegion: Button
    private  lateinit var textViewRegion: TextView
    private lateinit var buttonSubmitVote: Button
    private  var regionName= ""
    private var selectedCandidate: String? = null // To store the selected candidate

    //Firebase firestore instance
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vote)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        editTextZipcode = findViewById(R.id.zipcodeText)
        buttonSearchRegion = findViewById(R.id.buttonSearch)
        textViewRegion = findViewById(R.id.textView3)
        buttonSubmitVote = findViewById(R.id.button)

        // Set button click listener
        buttonSearchRegion.setOnClickListener {
            val zipcodeInput = editTextZipcode.text.toString().trim()

            // Check if the input is valid
            if (zipcodeInput.length >= 3) {
                val firstThreeChars = zipcodeInput.substring(0, 3).uppercase()

                // Call the function to fetch the region from Firebase
                fetchRegionFromZipcode(firstThreeChars)
//                fetchCandidateName(regionName)
            } else {
                Toast.makeText(this@VoteActivity, "Please enter a valid zipcode", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSubmitVote.setOnClickListener {
            if (selectedCandidate == null) {
                Toast.makeText(this, "Please select a candidate to vote!", Toast.LENGTH_SHORT).show()
            } else {
                // Show confirmation dialog
                AlertDialog.Builder(this)
                    .setTitle("Confirm Vote")
                    .setMessage("Are you sure you want to vote for $selectedCandidate?")
                    .setPositiveButton("Yes") { _, _ ->
                        registerVote(regionName, selectedCandidate!!)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }


    }


    private fun fetchRegionFromZipcode(zipcodeRef: String) {
        // Reference to the "Zipcode" collection and the document with the first three letters of the zip code
        val docRef = db.collection("Zipcode").document(zipcodeRef)

        // Fetch the region name
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the "region" field from the document
                    val region = document.getString("region")
                    regionName = region.toString()
                    fetchCandidateName(region.toString())
                    if (region != null) {
                        // Set the region name to the TextView
                        textViewRegion.text = "Region: $region"
                    } else {
                        // In case the "region" field does not exist
                        textViewRegion.text = "Region not found"
                        Log.d("Firestore", "Region field missing in document")
                    }
                } else {
                    // Document does not exist
                    textViewRegion.text = "Region not found"
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                // Log error and show a toast message
                Log.w("Firestore", "Error fetching document", exception)
                Toast.makeText(this, "Error fetching region", Toast.LENGTH_SHORT).show()
            }


    }

    private fun fetchCandidateName(regionRef: String){
        // Fetch candidate name
        val docRefCan = db.collection("Regions").document(regionRef)
        docRefCan.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val candidatesList = document.get("candidates") as? List<Map<String, String>> ?: emptyList()
                    val formattedCandidates = candidatesList.map { candidate ->
                        "${candidate["name"]} (${candidate["party"]})"
                    }

                    // Call a method to update your RecyclerView with the list of formatted candidates
                    updateRecyclerView(formattedCandidates)
                } else {
                    Log.d("Firestore", "No such document for region: $regionName")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching region document", exception)
            }
    }

    // Register the vote in Firestore
    private fun registerVote(regionNameRef: String, candidateName: String) {
        val docRefCan = db.collection("Regions").document(regionNameRef)

        docRefCan.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val candidatesList = document.get("candidates") as? List<Map<String, Any>> ?: emptyList()

                // Find the candidate in the list and update their vote count
                val updatedCandidates = candidatesList.map { candidate ->
                    if (candidate["name"] == candidateName.split(" (")[0]) {
                        // Increment the vote count for the selected candidate
                        val newVoteCount = (candidate["vote"] as? Long ?: 0) + 1
                        candidate.toMutableMap().apply { put("vote", newVoteCount) }
                    } else {
                        candidate
                    }
                }

                // Update the candidates field in Firestore
                docRefCan.update("candidates", updatedCandidates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Vote registered successfully!", Toast.LENGTH_SHORT).show()
                        // Create an intent to navigate to the Main Menu activity
                        val intent = Intent(this, EndingScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Clears the activity stack
                        startActivity(intent)
                        finish() // Optional: Call finish to close the current activity
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error updating document", e)
                        Toast.makeText(this, "Election has ended, Cannot vote!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Log.d("Firestore", "No such document for region: $regionNameRef")
            }
        }.addOnFailureListener { exception ->
            Log.w("Firestore", "Error fetching region document", exception)
        }
    }

    private fun updateRecyclerView(candidates: List<String>) {
        val recyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CandidatesAdapter(candidates) { candidate ->
            selectedCandidate = candidate // Store the selected candidate when clicked
        }
        recyclerView.adapter = adapter

    }

}