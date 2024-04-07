package com.mobdeve.s13.grp7.pokelearn

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mobdeve.s13.grp7.pokelearn.databinding.SignupPageBinding
import com.mobdeve.s13.grp7.pokelearn.databinding.LoginPageBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: SignupPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth

        binding.signupBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmPassword = binding.confirmPassEt.text.toString()

            if(checkAllField()){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        firebaseAuth.signOut()
                        Toast.makeText(this, "User account is successfully created!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }
    }

    private fun checkAllField(): Boolean {
        val username = binding.usernameEt.text.toString()
        val email = binding.emailEt.text.toString()
        val password = binding.passET.text.toString()
        val confirmPassword = binding.confirmPassEt.text.toString()
        if(username == ""){
            binding.usernameLayout.error = "This is a required field."
            return false
        }
        if(email == ""){
            binding.emailLayout.error = "This is a required field."
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailLayout.error = "Check email format"
            return false
        }
        if(password == ""){
            binding.passwordLayout.error = "This is a required field."
            return false
        }
        if(password.length < 8){
            binding.passwordLayout.error = "Password must be at least 8 characters."
            return false
        }
        if(confirmPassword == ""){
            binding.confirmPasswordLayout.error = "This is a required field."
            return false
        }
        if(password != confirmPassword){
            binding.passwordLayout.error = "Password does not match."
            binding.confirmPasswordLayout.error = "Password does not match."
            return false
        }
        return true
    }

    fun onLoginClicked(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}