package com.mobdeve.s13.grp7.pokelearn.retrofit

import com.mobdeve.s13.grp7.pokelearn.model.PokeListItemModel
import com.mobdeve.s13.grp7.pokelearn.model.Pokedex
import com.mobdeve.s13.grp7.pokelearn.model.Pokemon
import retrofit2.http.GET
import io.reactivex.Observable



interface IPokemonList {
    @get:GET("pokedex.json")
    val listPokemon:Observable<Pokedex>
}