package com.mobdeve.s13.grp7.pokelearn.database

import android.util.Log
import com.google.firebase.database.*
import com.mobdeve.s13.grp7.pokelearn.model.UserProfile

class FirebaseDatabaseHelper {



    private val database = FirebaseDatabase.getInstance("https://pokelearn-aeb5e-default-rtdb.asia-southeast1.firebasedatabase.app/")

    fun writeUser(userProfile: UserProfile) {
        val myRef = database.getReference("userprofiles").child(userProfile.uid)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User does not exist, create the user
                    myRef.setValue(userProfile)
                }
            }



            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error here
            }
        })
    }
    fun readUser(userId: String, onDataReceived: (UserProfile?) -> Unit) {
        val myRef = database.getReference("userprofiles").child(userId)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                onDataReceived(userProfile)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here
            }
        })
    }

    fun readPokedex(userId: String, onDataReceived: (ArrayList<String>?) -> Unit) {
        val myRef = database.getReference("userprofiles").child(userId)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                onDataReceived(userProfile?.pokedex)

                // print if successful
                Log.d("pokedexinfirebase", "Pokedex: ${userProfile?.pokedex}))")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here
            }
        })
    }



}