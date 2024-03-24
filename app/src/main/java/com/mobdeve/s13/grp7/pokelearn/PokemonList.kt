package com.mobdeve.s13.grp7.pokelearn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s13.grp7.pokelearn.adapter.PokemonListAdapter
import com.mobdeve.s13.grp7.pokelearn.common.Common
import com.mobdeve.s13.grp7.pokelearn.common.ItemOffsetDecoration
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import com.mobdeve.s13.grp7.pokelearn.retrofit.IPokemonList
import com.mobdeve.s13.grp7.pokelearn.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PokemonList : Fragment() {

    internal var compositeDisposable = CompositeDisposable()
    internal var iPokemonList:IPokemonList

    internal lateinit var rvw_pokemon : RecyclerView

    init {
        val retrofit: Retrofit = RetrofitClient.instance
        iPokemonList = retrofit.create(IPokemonList::class.java)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView: View? = inflater.inflate(R.layout.fragment_pokemon_list, container, false)
        // Inflate the layout for this fragment

        rvw_pokemon = itemView!!.findViewById(R.id.rvw_pokemon) as RecyclerView
        rvw_pokemon.setHasFixedSize(true)
        rvw_pokemon.layoutManager = GridLayoutManager(activity, 2)

        val itemDecoration = ItemOffsetDecoration(requireActivity(), R.dimen.spacing)
        rvw_pokemon.addItemDecoration(itemDecoration)

        fetchData()

        return itemView
    }

    private fun fetchData() {
compositeDisposable.add(iPokemonList.listPokemon
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{pokemonDex ->
                Common.pokemonList = pokemonDex.pokemon!!
                val adapter = PokemonListAdapter(Common.pokemonList, requireActivity())

                rvw_pokemon.adapter = adapter
            })

    }

}