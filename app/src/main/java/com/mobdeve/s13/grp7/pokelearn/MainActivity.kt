package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to HomeActivity
        startActivity(Intent(this, HomeActivity::class.java))
        // Finish MainActivity to prevent returning to it when pressing back button
        finish()
    }
}
