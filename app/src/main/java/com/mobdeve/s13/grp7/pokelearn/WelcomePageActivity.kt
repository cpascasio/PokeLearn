package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s13.grp7.pokelearn.databinding.WelcomePageBinding

class WelcomePageActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val binding = WelcomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val user = firebaseAuth.currentUser

            if(user != null) {
                // change go to home or main timer page
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
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