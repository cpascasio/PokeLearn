package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding

class WelcomePageActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_page)

        firebaseAuth = Firebase.auth

        Handler(Looper.getMainLooper()).postDelayed({
            val user = firebaseAuth.currentUser

            if(user != null) {
                // change go to home or main timer page
                val intent = Intent(this, WelcomePageActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Currently logged in", Toast.LENGTH_SHORT).show()
                finish()
            }else{
//                val loginButton: Button = findViewById(R.id.btnWP_Login)
//                val signupButton: Button = findViewById(R.id.btnWP_SignUp)
//
//                loginButton.setOnClickListener {
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
//                    Toast.makeText(this, "sign in", Toast.LENGTH_SHORT).show()
//                }
//
//                signupButton.setOnClickListener {
//                    val intent = Intent(this, SignUpActivity::class.java)
//                    startActivity(intent)
//                    Toast.makeText(this, "sign up", Toast.LENGTH_SHORT).show()
//                }
                val intent = Intent(this, WelcomePageActivity::class.java)
                startActivity(intent)
                finish() // finish MainActivity
            }
        }, 3000)
    }
}