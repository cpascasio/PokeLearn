package com.mobdeve.s13.grp7.pokelearn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PokemonDetail: Fragment(){

    internal var instance: PokemonDetail? = null

    fun getInstance(): PokemonDetail {
        if(instance == null)
            instance = PokemonDetail()
        return instance!!
    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val itemView: View = inflater.inflate(R.layout.fragment_pokemon_detail, container, false)
//
//        return itemView
//    }

}