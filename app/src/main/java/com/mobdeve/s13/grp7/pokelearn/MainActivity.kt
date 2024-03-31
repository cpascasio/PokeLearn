package com.mobdeve.s13.grp7.pokelearn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import com.google.firebase.Firebase
import com.mobdeve.s13.grp7.pokelearn.databinding.WelcomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

import android.content.Intent

import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
import com.mobdeve.s13.grp7.pokelearn.model.Pokemon
import com.mobdeve.s13.grp7.pokelearn.model.PokemonNew
import org.json.JSONArray
import java.io.IOException
import java.nio.charset.Charset
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbar: Toolbar

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.selectedItemId = R.id.home
        replaceFragment(HomeFragment())

        //registerReceiver(showDetail, IntentFilter(Common.KEY_ENABLE_HOME))


        // Create an instance of MyDatabaseHelper
        val dbHelper = MyDatabaseHelper(this)

        // Check if the database is empty
        if (dbHelper.getAllPokemon().isEmpty()) {
            // Load the JSON data from the assets directory
            val jsonString: String
            try {
                val inputStream = assets.open("new-pokedex.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                jsonString = String(buffer, Charset.forName("UTF-8"))
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }

            val jsonArray = JSONArray(jsonString)

            // Iterate over the JSON array
            for (i in 0 until jsonArray.length()) {
                // Get each JSONObject
                val jsonObject = jsonArray.getJSONObject(i)

                // Create a PokemonNew object
                val pokemon = PokemonNew().apply {
                    this.id = jsonObject.getInt("id")
                    this.name = jsonObject.getString("name")
                    this.type = jsonObject.getString("type").split(",")
                    this.species = jsonObject.getString("species")
                    this.description = jsonObject.getString("description")
                    this.ability = jsonObject.getString("ability").split(",")
                    this.height = jsonObject.getString("height")
                    this.weight = jsonObject.getString("weight")
                }

                // Insert the PokemonNew object into the database
                dbHelper.addPokemon(pokemon)
            }
        }

        val pokemonList = dbHelper.getAllPokemon()

        for (pokemon in pokemonList) {
            Log.d("MainActivity123", pokemon.type[0].toString())
        }


        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(Profile())
                R.id.pokedex -> replaceFragment(PokemonList())

                else -> {

                }

            }
            true

        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId)
        {
            android.R.id.home -> {
                toolbar.title = "POKEMON LIST"

                supportFragmentManager.popBackStack("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE)

                supportActionBar!!.setDisplayShowHomeEnabled(false)
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            }
        }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()

    }




}

