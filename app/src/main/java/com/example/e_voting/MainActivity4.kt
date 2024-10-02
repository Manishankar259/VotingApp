package com.example.e_voting

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity4 : AppCompatActivity() {

    private lateinit var  editTextZipcode: EditText
    private  lateinit var buttonSearchRegion: Button
    private  lateinit var textViewRegion: TextView

    //Firebase firestore instance
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        editTextZipcode = findViewById(R.id.zipcodeText)
        buttonSearchRegion = findViewById(R.id.button)
        textViewRegion = findViewById(R.id.textView3)

        // Set button click listener
        buttonSearchRegion.setOnClickListener {
            val zipcodeInput = editTextZipcode.text.toString().trim()

            // Check if the input is valid
            if (zipcodeInput.length >= 3) {
                val firstThreeChars = zipcodeInput.substring(0, 3).uppercase()

                // Call the function to fetch the region from Firebase
                fetchRegionFromZipcode(firstThreeChars)
            } else {
                Toast.makeText(this@MainActivity4, "Please enter a valid zipcode", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun fetchRegionFromZipcode(zipcodeRef: String) {
        // Reference to the "Zipcode" collection and the document with the first three letters of the zip code
        val docRef = db.collection("Zipcode").document(zipcodeRef)

        // Fetch the document
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the "region" field from the document
                    val region = document.getString("region")
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


}