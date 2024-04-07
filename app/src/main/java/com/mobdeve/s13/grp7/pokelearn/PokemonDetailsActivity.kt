package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import androidx.navigation.fragment.findNavController

class PokemonDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_pokemon_detail)


        val typeColorMapping = mapOf(
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

        // Get the intent that started this activity
        val intent = intent

        // Retrieve the PokemonNew attributes from the intent
        val pokemonName = intent.getStringExtra("Name")
        val pokemonId = intent.getIntExtra("Id", 0)
        val pokemonType = intent.getStringArrayListExtra("Type")
        val pokemonSpecies = intent.getStringExtra("Species")
        val pokemonDescription = intent.getStringExtra("Description")
        val pokemonAbility = intent.getStringExtra("Ability")
        val pokemonHeight = intent.getStringExtra("Height")
        val pokemonWeight = intent.getStringExtra("Weight")

        // Print the data to the Logcat console
        Log.d("PokemonDetailsActivity", "Name: $pokemonName, Id: $pokemonId, Type: $pokemonType, Species: $pokemonSpecies, Description: $pokemonDescription, Ability: $pokemonAbility, Height: $pokemonHeight, Weight: $pokemonWeight")

        // Display the data in the activity's layout
        val nameTextView = findViewById<TextView>(R.id.txtPokeName)
        val idTextView = findViewById<TextView>(R.id.txtPokeID)
        val elem1TextView = findViewById<TextView>(R.id.txtElem1)
        val elem2TextView = findViewById<TextView>(R.id.txtElem2)
        val speciesTextView = findViewById<TextView>(R.id.txtPokeCategory)
        val descriptionTextView = findViewById<TextView>(R.id.txtDescription)
        val abilityTextView = findViewById<TextView>(R.id.txtPokeAbilities)
        val height = findViewById<TextView>(R.id.txtHeight)
        val weight = findViewById<TextView>(R.id.txtWeight)
        val category = findViewById<TextView>(R.id.txtCategory)
        val abilities = findViewById<TextView>(R.id.txtAbilities)

        val heightTextView = findViewById<TextView>(R.id.txtPokeHeight)
        val weightTextView = findViewById<TextView>(R.id.txtPokeWeight)
        val PokeImageView = findViewById<ImageView>(R.id.ivPokeImage)
        val circleBackground = findViewById<ConstraintLayout>(R.id.circleBackground)

        val drawableName = "pokegif_${pokemonId}"
        val resId = this.resources.getIdentifier(drawableName, "drawable", this.packageName)



        Glide.with(this)
            .load(resId)
            .into(PokeImageView)





        val element1 = pokemonType?.get(0)!!.replace("[", "").replace("]", "").replace("\"", "")
        elem1TextView.text = element1
        val color1 = ContextCompat.getColor(this, typeColorMapping[element1] ?: R.color.black)
        elem1TextView.backgroundTintList = ColorStateList.valueOf(color1)

        if(pokemonType?.size == 1){
            elem2TextView.visibility = View.GONE
        } else {
            val element2 = pokemonType.get(1)!!.replace("[", "").replace("]", "").replace("\"", "").replace(" ", "")
            elem2TextView.text = element2
            val color2 = ContextCompat.getColor(this, typeColorMapping[element2] ?: R.color.purple_200)
            elem2TextView.backgroundTintList = ColorStateList.valueOf(color2)
            elem2TextView.visibility = View.VISIBLE
        }


        circleBackground.backgroundTintList = ColorStateList.valueOf(color1)
        height.setTextColor(color1)
        weight.setTextColor(color1)
        category.setTextColor(color1)
        abilities.setTextColor(color1)


        nameTextView.text = pokemonName
        if(pokemonId > 9){
            idTextView.text = "#0$pokemonId"
        } else {
            idTextView.text = "#00$pokemonId"
        }
        speciesTextView.text = pokemonSpecies
        descriptionTextView.text = pokemonDescription
        if (pokemonAbility != null) {
            abilityTextView.text = pokemonAbility.replace("[", "").replace("]", "").replace("\"", "")
        }
        heightTextView.text = pokemonHeight.toString()
        weightTextView.text = pokemonWeight.toString()

        val backButton: Button = findViewById(R.id.btnBack)

        backButton.setOnClickListener {
            Log.d("Back Button", "Clicked back button")
            onBackPressed()

        }

        // Now you can use these attributes in your activity
    }


}