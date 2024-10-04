package com.example.e_voting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class MainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Reference the button from the layout
        val openSecondActivityButton: Button = findViewById(R.id.startVotingButton)

        // Set an OnClickListener to the button to open the second activity
        openSecondActivityButton.setOnClickListener {
            // Create an intent to open the SecondActivity
            val intent = Intent(this, VoteActivity::class.java)
            // Start the activity
            startActivity(intent)
        }
    }
}