package com.mobdeve.s13.grp7.pokelearn.adapter


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s13.grp7.pokelearn.PokemonDetailsActivity
import com.mobdeve.s13.grp7.pokelearn.databinding.PokemonListItemBinding
import com.mobdeve.s13.grp7.pokelearn.model.PokeListItemModel
import com.mobdeve.s13.grp7.pokelearn.model.Pokemon

class PokemonListAdapter(var pokeListItemModels: ArrayList<Pokemon>, private var activity: Activity) : RecyclerView.Adapter<PokemonListAdapter.PokemonListViewHolder>(){

    // Handles the logic needed for getItemCount().
    override fun getItemCount(): Int {
        return this.pokeListItemModels.size
    }

    // Note: This method uses ViewBinding instead of inflating the ViewGroup.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonListAdapter.PokemonListViewHolder {
        val pokemonListItemBinding = PokemonListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonListViewHolder(pokemonListItemBinding)
    }


    // Handles binding a model to the ViewHolder.
    override fun onBindViewHolder(holder: PokemonListAdapter.PokemonListViewHolder, position: Int) {
        holder.bindPokeListItem(this.pokeListItemModels[position], position)
    }

    inner class PokemonListViewHolder(private val pokemonListItemBinding: PokemonListItemBinding): RecyclerView.ViewHolder(pokemonListItemBinding.root), View.OnClickListener {

        private var myPosition: Int = -1

        private lateinit var item: Pokemon

        // Allows for the itemView to trigger the logic in OnClick()
        init {
            this@PokemonListViewHolder.itemView.setOnClickListener(this)
        }

        fun bindPokeListItem(pokeListItemModel: Pokemon, position: Int) {
            this@PokemonListViewHolder.myPosition = position
            this@PokemonListViewHolder.item = pokeListItemModel

            with(pokemonListItemBinding){
                tvPokeName.text = pokeListItemModel.name
                tvPokeId.text = pokeListItemModel.num
                Glide.with(itemView.context)
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${position + 1}.png")
                    .centerCrop()
                    .into(ivPokeSprite)

            }
        }

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            this.pokemonListItemBinding.root.setOnClickListener(onClickListener)
        }

        fun getPokemonListItemBinding(): PokemonListItemBinding {
            return pokemonListItemBinding
        }

        override fun onClick(v: View?) {
            this.pokemonListItemBinding.root.setOnClickListener {
                val position: Int = this.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val poke: Pokemon = pokeListItemModels[position]
                    val intent = Intent(v?.context, PokemonDetailsActivity::class.java)
                    intent.putExtra("POKE_ID", poke.id)
                    v?.context?.startActivity(intent)
                }
            }
        }
    }




}