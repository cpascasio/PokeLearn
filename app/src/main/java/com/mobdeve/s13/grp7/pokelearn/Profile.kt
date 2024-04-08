package com.mobdeve.s13.grp7.pokelearn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Get user email from Firebase Auth
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        // Display user email
        val tvwPPEmail = view.findViewById<TextView>(R.id.tvwPP_Email)
        userEmail?.let {
            tvwPPEmail.text = it
        }

        // Set OnClickListener for the logout button
        val logoutButton = view.findViewById<Button>(R.id.logoutBtn)
        logoutButton.setOnClickListener {
            // Sign out the user
            auth.signOut()
            
            //clear shared preferences
            val sharedPreferences = activity?.getSharedPreferences("User", Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.clear()?.apply()

            // Navigate to the login screen or perform any other action after logout
            LoginManager.getInstance().logOut()
            // For example, navigate back to the LoginActivity
            val intent = Intent(activity, WelcomePageActivity::class.java)
            startActivity(intent)
            activity?.finish() // Finish the current activity to prevent navigating back to it with the back button
        }

        return view
    }
}
