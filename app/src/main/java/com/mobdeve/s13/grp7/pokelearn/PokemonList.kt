package com.mobdeve.s13.grp7.pokelearn

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s13.grp7.pokelearn.adapter.PokemonListAdapter
import com.mobdeve.s13.grp7.pokelearn.common.Common
import com.mobdeve.s13.grp7.pokelearn.common.ItemOffsetDecoration
import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.model.PokemonNew
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import com.mobdeve.s13.grp7.pokelearn.retrofit.IPokemonList
import com.mobdeve.s13.grp7.pokelearn.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
            var tempPoke = dbHelper.getPokemon(data[i].toInt())

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
//compositeDisposable.add(iPokemonList.listPokemon
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe{pokemonDex ->
//                Common.pokemonList = pokemonDex.pokemon!!
//                val adapter = PokemonListAdapter(Common.pokemonList, requireActivity())
//
//                rvw_pokemon.adapter = adapter
//            })

        userPokemonList = fetchUserData(dummyData)

        val adapter = PokemonListAdapter(userPokemonList, requireActivity())
        rvw_pokemon.adapter = adapter

    }

}