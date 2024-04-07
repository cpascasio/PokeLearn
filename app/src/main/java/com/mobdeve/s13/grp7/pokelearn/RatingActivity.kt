package com.mobdeve.s13.grp7.pokelearn

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import androidx.core.content.ContextCompat.startActivity

class RatingActivity (context: Context, private val callback: (Float) -> Unit) : Dialog(context) {

    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmitRating: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rating) // Assuming your XML layout file is named "rating.xml"

        ratingBar = findViewById(R.id.ratingBar)
        btnSubmitRating = findViewById(R.id.btnSubmitRating)

        btnSubmitRating.setOnClickListener {
            val rating = ratingBar.rating
            callback(rating)

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

            dismiss()
        }
    }
}