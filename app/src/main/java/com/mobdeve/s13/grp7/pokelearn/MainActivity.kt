package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import android.widget.Button
import com.google.firebase.Firebase
import com.mobdeve.s13.grp7.pokelearn.databinding.WelcomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth



import android.content.BroadcastReceiver
import android.content.Context

import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mobdeve.s13.grp7.pokelearn.common.Common
import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
import com.mobdeve.s13.grp7.pokelearn.model.Pokemon
import com.mobdeve.s13.grp7.pokelearn.model.PokemonNew
import org.json.JSONArray
import java.io.IOException
import java.nio.charset.Charset
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)

    }
}


