package com.mobdeve.s13.grp7.pokelearn

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.mobdeve.s13.grp7.pokelearn.adapter.PokemonListAdapter
import com.mobdeve.s13.grp7.pokelearn.common.ItemOffsetDecoration
import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.database.UserProfileDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.model.PokemonNew
import kotlin.random.Random

class PokemonList : Fragment() {

    //internal var compositeDisposable = CompositeDisposable()
    //internal var iPokemonList:IPokemonList

    internal lateinit var rvw_pokemon : RecyclerView
    private lateinit var dbHelper: MyDatabaseHelper

    private val dummyData: ArrayList<String> = ArrayList()

    private lateinit var userPokemonList: ArrayList<PokemonNew>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

    dummyData.addAll(createDummyData())

        return inflater.inflate(R.layout.fragment_pokemon_list, container, false)


    // Inflate the layout for this fragment

//        rvw_pokemon = itemView!!.findViewById(R.id.rvw_pokemon) as RecyclerView
//        rvw_pokemon.setHasFixedSize(true)
//        rvw_pokemon.layoutManager = GridLayoutManager(activity, 2)
//
//        val itemDecoration = ItemOffsetDecoration(requireActivity(), R.dimen.spacing)
//        rvw_pokemon.addItemDecoration(itemDecoration)
//
//        fetchData()
//
//        return itemView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize EditText view for search input
        val edtSearch = view.findViewById<EditText>(R.id.edtSearch)

        rvw_pokemon = view.findViewById(R.id.rvw_pokemon) as RecyclerView
        rvw_pokemon.setHasFixedSize(true)
        rvw_pokemon.layoutManager = GridLayoutManager(requireContext(), 2)

        val itemDecoration = ItemOffsetDecoration(requireActivity(), R.dimen.spacing)
        rvw_pokemon.addItemDecoration(itemDecoration)


        dbHelper = MyDatabaseHelper(requireContext())
        fetchData()



        // Add a TextWatcher to edtSearch
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // No action needed here
            }

            override fun afterTextChanged(s: Editable) {
                // Call findPokemonByName every time the text changes
                val searchResults = findPokemonByName(userPokemonList, s.toString())
                val adapter = PokemonListAdapter(searchResults, requireActivity())
                rvw_pokemon.adapter = adapter
            }
        })
    }

    private fun fetchUserData(data: ArrayList<String>): ArrayList<PokemonNew> {
        // load each index of the arrayList
        var finalPokemonList = ArrayList<PokemonNew>()

        for (i in 0 until data.size) {
            var tempPoke = dbHelper.getPokemon(data[i].trim().toInt())

            if (tempPoke != null) {
                finalPokemonList.add(tempPoke)
            }
        }

        // Sort finalPokemonList from smallest to biggest
        finalPokemonList.sortWith(compareBy { it.id })

        return finalPokemonList
    }

    private fun findPokemonByName(pokemonList: ArrayList<PokemonNew>, name: String): ArrayList<PokemonNew> {
        val matchingPokemon = ArrayList<PokemonNew>()
        for (pokemon in pokemonList) {
            if (pokemon.name.contains(name, ignoreCase = true)) {
                matchingPokemon.add(pokemon)
            }
        }
        return matchingPokemon
    }

    fun createDummyData(): ArrayList<String> {
        // Generate random Pokedex numbers from 1 to 50
        val randomNumbers = HashSet<String>()
        while (randomNumbers.size < 10) {
            val randomPokedexNumber = Random.nextInt(1, 51).toString()
            randomNumbers.add(randomPokedexNumber)
        }

        val tempdata: ArrayList<String> = ArrayList()

        tempdata.addAll(randomNumbers)

        return tempdata
    }

    private fun fetchData() {
        val sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", null) // Get UID from SharedPreferences

        if (uid != null) {
            val userProfileDatabaseHelper = UserProfileDatabaseHelper(requireContext())
            val pokedex = userProfileDatabaseHelper.getPokedex(uid) // Get pokedex from SQLite database

            if (pokedex != null) {
                userPokemonList = fetchUserData(pokedex)
            } else {
                userPokemonList = fetchUserData(dummyData)
            }
        } else {
            userPokemonList = fetchUserData(dummyData)
        }

        val adapter = PokemonListAdapter(userPokemonList, requireActivity())
        rvw_pokemon.adapter = adapter
    }

}