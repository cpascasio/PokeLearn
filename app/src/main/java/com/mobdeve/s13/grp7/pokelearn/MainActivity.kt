package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import com.mobdeve.s13.grp7.pokelearn.databinding.WelcomePageBinding
//import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
//import com.mobdeve.s13.grp7.pokelearn.databinding.WelcomePageBinding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = WelcomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val loginButton: Button = findViewById(R.id.btnWP_Login)
        val signupButton: Button = findViewById(R.id.btnWP_SignUp)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}