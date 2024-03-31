package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.mobdeve.s13.grp7.pokelearn.databinding.RewardsPageBinding

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: RewardsPageBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var shareDialog: ShareDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RewardsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Facebook SDK
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()

// Initialize CallbackManager and ShareDialog
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)

        binding.btnRPOK.setOnClickListener {
            // Show the rating dialog when OK button is clicked
            showRatingActivity()
        }

        binding.shareBtn.setOnClickListener {
            shareToFacebook()
            Toast.makeText(this, "Clicked shared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRatingActivity() {
        val ratingDialog = RatingActivity(this) { rating ->
            // Handle the rating here
            Toast.makeText(this, "Rating submitted: $rating", Toast.LENGTH_SHORT).show()
        }
        ratingDialog.show()
    }

    private fun shareToFacebook() {
        // Assuming you have a Bitmap named 'image' containing the image you want to share
        val image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.poke_1)

        val photo = SharePhoto.Builder()
            .setBitmap(image)
            .build()

        val content = SharePhotoContent.Builder()
            .addPhoto(photo)
            .build()

        if (ShareDialog.canShow(SharePhotoContent::class.java)) {
            shareDialog.show(content)
            Toast.makeText(this, "shared", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cannot show share dialog", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}