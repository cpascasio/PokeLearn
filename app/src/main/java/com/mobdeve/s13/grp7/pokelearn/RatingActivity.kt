package com.mobdeve.s13.grp7.pokelearn

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar

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
            dismiss()
        }
    }
}