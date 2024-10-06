package com.example.e_voting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class MainMenu : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Reference the start voting button from the layout
        val openSecondActivityButton: Button = findViewById(R.id.startVotingButton)

        // Set an OnClickListener to the button to open the second activity
        openSecondActivityButton.setOnClickListener {
            // Create an intent to open the SecondActivity
            val intent = Intent(this, UserInfo::class.java)
            // Start the activity
            startActivity(intent)
        }

        //Reference the see result button from the layout
        val openResultActivityButton: Button = findViewById(R.id.resultButton)

        // Set an OnClickListener to the button to open the second activity
        openResultActivityButton.setOnClickListener {
            // Create an intent to open the SecondActivity
            val intent = Intent(this, Result::class.java)
            // Start the activity
            startActivity(intent)
        }

    }
}