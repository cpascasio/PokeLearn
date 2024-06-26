package com.mobdeve.s13.grp7.pokelearn

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.database.UserProfileDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.databinding.RewardsPageBinding
import kotlin.properties.Delegates
import kotlin.random.Random

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: RewardsPageBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var shareDialog: ShareDialog
    private var rolledNumber by Delegates.notNull<Int>()

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

        rolledNumber = rollPokemon()

        displayPokemon(rolledNumber)

        addPokemonToPokedex(rolledNumber)

        // update Firebase database


        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", null)
        if (uid != null) {

            val userProfileDbHelper = UserProfileDatabaseHelper(this)
            userProfileDbHelper.incrementPomodoroCyclesCompleted(uid)
            userProfileDbHelper.updateFirebaseDatabase(uid)
        }

        // increment pomodorocycles function



        // Initialize Facebook SDK
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()

// Initialize CallbackManager and ShareDialog
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)

        binding.btnRPOK.setOnClickListener {
            // Show the rating dialog when OK button is clicked
//            showRatingActivity()
            redirectToHomeFragment()
        }



        binding.shareBtn.setOnClickListener {
            shareToFacebook()

        }
    }


    fun rollPokemon(): Int {

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", null)
        val userProfileDbHelper = UserProfileDatabaseHelper(this)
        val userPokedex = userProfileDbHelper.getPokedex(uid!!)

        // Convert the user's Pokedex to a set for faster lookup
        val pokedexSet = userPokedex?.toSet()

        var randomPokedexNumber : Int

        do {
            // Generate a random Pokedex number from 1 to 50
            randomPokedexNumber = Random.nextInt(1, 51)
        } while (pokedexSet?.contains(randomPokedexNumber.toString()) == true)


        return randomPokedexNumber
    }

    fun displayPokemon(randomPokedexNumber:Int) {
        // Get the random Pokedex number
        //val randomPokedexNumber = rollPokemon()

        val drawableName = "pokegif_${randomPokedexNumber}"
        val resId = this.resources.getIdentifier(drawableName, "drawable", this.packageName)

        // Use Glide to load the image resource into the ImageView
        Glide.with(this)
            .load(resId)
            .into(binding.ivPokeReward)
    }

    //function to add the pokemon to the pokedex
    fun addPokemonToPokedex(pokemonId: Int) {
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", null)

        if (uid != null) {
            // Create an instance of UserProfileDatabaseHelper
            val userProfileDbHelper = UserProfileDatabaseHelper(this)

            // Add rolled pokemon to the pokedex given the uid
            val success = userProfileDbHelper.addPokemonToUser(uid, pokemonId.toString())

            if (success) {
                Toast.makeText(this, "Pokemon added to Pokedex", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to add Pokemon to Pokedex", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        //log the new pokedex of the user
        val userProfileDbHelper = UserProfileDatabaseHelper(this)
        val pokedex = userProfileDbHelper.getPokedex(uid!!)
        if (pokedex != null) {
            for (pokemon in pokedex) {
                Log.d("pokedex", pokemon)
            }
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
//        val image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.poke_1)

        // Generate the drawable name based on the rolled number
        val drawableName = "poke_$rolledNumber"

        // Get the resource ID of the image using the drawable name
        val resId = resources.getIdentifier(drawableName, "drawable", packageName)

        // Load the image as a Bitmap
//        val image: Bitmap = BitmapFactory.decodeResource(resources, resId)

        // Load the reward image as a Bitmap
        val rewardImage: Bitmap = BitmapFactory.decodeResource(resources, resId)

        // Load the background image as a Bitmap
        val backgroundImage: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_sharetofb)

        // Create a new Bitmap with the same dimensions as the background image
//        val compositeImage = Bitmap.createBitmap(backgroundImage.width, backgroundImage.height, backgroundImage.config)

        // Determine the scale factor to make the reward image larger
        val scaleFactor = 3.0f // Adjust this value as needed

        // Calculate the new dimensions of the scaled reward image
        val newWidth = (rewardImage.width * scaleFactor).toInt()
        val newHeight = (rewardImage.height * scaleFactor).toInt()


        // Create a scaled version of the reward image
        val scaledRewardImage = Bitmap.createScaledBitmap(rewardImage, newWidth, newHeight, true)

        // Create a new Bitmap with the same dimensions as the background image
        val compositeImage = Bitmap.createBitmap(backgroundImage.width, backgroundImage.height, backgroundImage.config)

        // Create a Canvas to draw on the composite image
//        val canvas = Canvas(compositeImage)

        // Create a Canvas to draw on the composite image
        val canvas = Canvas(compositeImage)

        // Draw the background image on the canvas
        canvas.drawBitmap(backgroundImage, 0f, 0f, null)

        // Calculate the position to draw the reward image at the center of the composite image
//        val x = (compositeImage.width - rewardImage.width) / 2f
//        val y = (compositeImage.height - rewardImage.height) / 2f

        // Calculate the position to draw the reward image at the center of the composite image
        val x = (compositeImage.width - scaledRewardImage.width) / 2f
        val y = (compositeImage.height - scaledRewardImage.height) / 2f

        // Draw the reward image on the canvas
        canvas.drawBitmap(scaledRewardImage, x, y, null)

        // Create a SharePhoto object with the composite image
        val photo = SharePhoto.Builder()
            .setBitmap(compositeImage)
            .build()

//        val photo = SharePhoto.Builder()
//            .setBitmap(image)
//            .build()

        val content = SharePhotoContent.Builder()
            .addPhoto(photo)
            .build()

        if (ShareDialog.canShow(SharePhotoContent::class.java)) {
            shareDialog.show(content)
            Toast.makeText(this, "Shared to Facebook", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cannot show share dialog", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    // create a function to redirect to home fragment
    private fun redirectToHomeFragment() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}