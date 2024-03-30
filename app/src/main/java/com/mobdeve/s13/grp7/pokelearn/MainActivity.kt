package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import android.widget.Button
import com.google.firebase.Firebase
import com.mobdeve.s13.grp7.pokelearn.databinding.WelcomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

//import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
//import com.mobdeve.s13.grp7.pokelearn.databinding.WelcomePageBinding

class MainActivity : ComponentActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = WelcomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = Firebase.auth

        Handler(Looper.getMainLooper()).postDelayed({
            val user = firebaseAuth.currentUser

            if(user != null) {
                // change go to home or main timer page
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
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



        }, 3000)


    }
}