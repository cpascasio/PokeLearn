package com.mobdeve.s13.grp7.pokelearn

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.mobdeve.s13.grp7.pokelearn.database.FirebaseDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.database.UserProfileDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.databinding.LoginPageBinding
import com.mobdeve.s13.grp7.pokelearn.model.UserProfile


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseHelper: FirebaseDatabaseHelper
    var callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseHelper = FirebaseDatabaseHelper()

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()

            if(checkAllField()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        // store userID to shared preferences
                        editor.putString("uid", firebaseAuth.currentUser?.uid)
                        editor.putString("email", email)
                        editor.putString("username", firebaseAuth.currentUser?.displayName)

                        // Fetch pokedex from Firebase


                        editor.apply()
                        Toast.makeText(this, "Successfully signed in!", Toast.LENGTH_SHORT).show()

                        val uid = sharedPreferences.getString("uid", null)

                        // fetch userprofile in the firebase database given the UID

                        if (uid != null) {
                            firebaseHelper.readUser(uid) { userProfile ->
                                if (userProfile != null) {
                                    // Save UserProfile into SQLite database
                                    val userProfileDatabaseHelper = UserProfileDatabaseHelper(this)
                                    val isAdded = userProfileDatabaseHelper.addUserProfile(userProfile)

                                    if (isAdded) {
                                        Log.d(TAG, "User profile added to SQLite database")
                                    } else {
                                        Log.e(TAG, "Failed to add user profile to SQLite database")
                                    }
                                }
                            }
                        }


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

        binding.buttonFacebookLogin.setReadPermissions("email", "public_profile")
        binding.buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })
        // ...
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

    fun onSignUpClicked(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
//        updateUI(currentUser)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    // store userID to shared preferences
                    editor.putString("uid", firebaseAuth.currentUser?.uid)
                    editor.putString("username", firebaseAuth.currentUser?.displayName)


                    // add email to shared preferences
                    editor.putString("email", firebaseAuth.currentUser?.email)
                    // add password to shared preferences
                    // Fetch pokedex from Firebase

                    editor.apply()

                        Toast.makeText(this, "Successfully signed in!", Toast.LENGTH_SHORT).show()

                    val uid = firebaseAuth.currentUser?.uid

                    if (uid != null) {
                        firebaseHelper.readUser(uid) { userProfile ->
                            val userProfileDatabaseHelper = UserProfileDatabaseHelper(this)
                            if (userProfile != null) {
                                // User profile exists in Firebase, save it to SQLite
                                userProfileDatabaseHelper.addUserProfile(userProfile)
                            } else {
                                // User profile does not exist in Firebase, create a new one
                                val newUserProfile = UserProfile().apply {
                                    this.uid = uid
                                    this.username = firebaseAuth.currentUser?.displayName.toString()
                                    this.pokedex = arrayListOf("1")
                                    this.fullPomodoroCyclesCompleted = 0 // Initialize with 0
                                }
                                // Save new user profile to SQLite
                                userProfileDatabaseHelper.addUserProfile(newUserProfile)
                                // Save new user profile to Firebase
                                firebaseHelper.writeUser(newUserProfile)
                            }
                        }
                    }


                    val user = firebaseAuth.currentUser
//                    updateUI(user)
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }
}