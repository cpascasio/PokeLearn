package com.mobdeve.s13.grp7.pokelearn

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
import com.mobdeve.s13.grp7.pokelearn.model.PokemonNew
import org.json.JSONArray
import java.io.IOException
import java.nio.charset.Charset
import kotlin.random.Random
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.grpc.Context

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbar: Toolbar

    private lateinit var firebaseAuth: FirebaseAuth

//    private var isUserInApp: Boolean = false
//    private var appInBackground: Boolean = false
//    private val handler = Handler(Looper.getMainLooper())
////    private val inactivityThreshold: Long = 2 * 60 * 1000 // 2 minutes in milliseconds
//    private val inactivityThreshold: Long = 5 * 1000 // 5 secs for trial
//    private var timerStarted = false
//
//
//
//    private val logRunnable = Runnable {
//        if (!isUserInApp && appInBackground) {
//            // User has left the app for over 2 minutes
//            sendNotification()
//            Log.d("MainActivity", "User has left the app for over 2 minutes")
//        }
//    }
//
//    private fun sendNotification() {
//        Log.d("MainActivity", "Notification sent")
//        val channelId = "default_channel_id"
//        val channelName = "Default"
//        val importance = NotificationManager.IMPORTANCE_DEFAULT
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(channelId, channelName, importance)
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(notificationChannel)
//            Log.d("MainActivity", "version is good")
//        }
//        Log.d("MainActivity", "before notif builder")
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.logo_pokelearn)
//            .setContentTitle("Wild Distraction Appeared!")
//            .setContentText("It seems like you’ve been distracted. Your Pokemon might escape!")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        Log.d("MainActivity", "Notif middle")
//        val notificationManager = NotificationManagerCompat.from(this)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("MainActivity", "returned")
//            return
//        }
//        notificationManager.notify(1, notificationBuilder.build())
//        Log.d("MainActivity", "Done Sending")
//    }

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

//    override fun onStart() {
//        super.onStart()
//        // User is in the app
//        isUserInApp = true
//        Log.d("MainActivity", "onStart() called")
//        cancelLogTask() // Cancel any previously scheduled log task
//        appInBackground = false
//    }
//
//    override fun onStop() {
//        super.onStop()
//        // User has left the app
//        isUserInApp = false
//        Log.d("MainActivity", "onStop() called")
//        scheduleLogTask() // Schedule a log task if the app is going into the background
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cancelLogTask() // Cancel any scheduled log task when the activity is destroyed
//    }
//
//    private fun scheduleLogTask() {
//        appInBackground = true
//        handler.postDelayed(logRunnable, inactivityThreshold)
//    }
//
//    private fun cancelLogTask() {
//        handler.removeCallbacks(logRunnable)
//    }


}







