package com.mobdeve.s13.grp7.pokelearn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mobdeve.s13.grp7.pokelearn.common.Common
import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
import com.mobdeve.s13.grp7.pokelearn.model.Pokemon
import org.json.JSONArray
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbar: Toolbar

    private val showDetail = object:BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if(intent!!.action!!.toString() == Common.KEY_ENABLE_HOME) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowHomeEnabled(true)

                val detailFragment : PokemonDetail = PokemonDetail().getInstance()
                val position: Int = intent.getIntExtra("position", -1)
                val bundle = Bundle()

                bundle.putInt("position", position)
                detailFragment.arguments = bundle

//                val fragmentTransaction = supportFragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.list_pokemon_fragment, detailFragment)
//                fragmentTransaction.addToBackStack("detail")
//                fragmentTransaction.commit()

                val pokemon:Pokemon = Common.pokemonList[position]
                supportActionBar!!.title = pokemon.name
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(PokemonList())

        //registerReceiver(showDetail, IntentFilter(Common.KEY_ENABLE_HOME))


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

        // Create an instance of MyDatabaseHelper
        val dbHelper = MyDatabaseHelper(this)

        // Iterate over the JSON array
        for (i in 0 until jsonArray.length()) {
            // Get each JSONObject
            val jsonObject = jsonArray.getJSONObject(i)

            // Insert the JSONObject into the database
            dbHelper.addPokemon(jsonObject)
        }

        val pokemonList = dbHelper.getAllPokemon()

        for (pokemon in pokemonList) {
            Log.d("MainActivity", pokemon.toString())
        }


        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId) {
                R.id.home -> replaceFragment(Home())
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
        unregisterReceiver(showDetail)

    }




}