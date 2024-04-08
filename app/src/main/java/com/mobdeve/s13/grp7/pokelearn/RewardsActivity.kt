package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.mobdeve.s13.grp7.pokelearn.databinding.RewardsPageBinding
import kotlin.random.Random

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: RewardsPageBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var shareDialog: ShareDialog

    // Move typeColorMapping here
    private val typeColorMapping = mapOf(
        "Grass" to R.color.type_grass,
        "Poison" to R.color.type_poison,
        "Fire" to R.color.type_fire,
        "Flying" to R.color.type_flying,
        "Water" to R.color.type_water,
        "Bug" to R.color.type_bug,
        "Normal" to R.color.type_normal,
        "Electric" to R.color.type_electric,
        "Ground" to R.color.type_ground,
        "Fairy" to R.color.type_fairy,
        "Fighting" to R.color.type_fighting,
        "Psychic" to R.color.type_psychic,
        "Rock" to R.color.type_rock,
        "Steel" to R.color.type_steel,
        "Ice" to R.color.type_ice,
        "Ghost" to R.color.type_ghost,
        "Dragon" to R.color.type_dragon,
        "Dark" to R.color.type_dark
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RewardsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rewardsSFX = MediaPlayer.create(this, R.raw.rewards)
        rewardsSFX.start()

        // Display a random Pokemon

        displayPokemon()

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


    fun rollPokemon(): Int {
        // Generate a random Pokedex number from 1 to 50
        val randomPokedexNumber = Random.nextInt(1, 51)
        return randomPokedexNumber
    }

    fun displayPokemon() {
        // Get the random Pokedex number
        val randomPokedexNumber = rollPokemon()

        val drawableName = "pokegif_${randomPokedexNumber}"
        val resId = this.resources.getIdentifier(drawableName, "drawable", this.packageName)

        // Use Glide to load the image resource into the ImageView
        Glide.with(this)
            .load(resId)
            .into(binding.ivPokeReward)
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