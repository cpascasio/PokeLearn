package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s13.grp7.pokelearn.databinding.LoginPageBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()

            if(checkAllField()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        firebaseAuth.signOut()
                        Toast.makeText(this, "Successfully signed in!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }
    }


    private fun checkAllField(): Boolean {
        val email = binding.emailEt.text.toString()
        val password = binding.passET.text.toString()
        if(email == ""){
            binding.emailLayout.error = "This is a required field."
            return false
        }
        if(password == ""){
            binding.passwordLayout.error = "This is a required field."
            return false
        }
        return true
    }
}