package com.mobdeve.s13.grp7.pokelearn.adapter


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s13.grp7.pokelearn.Interface.IItemClickListener
import com.mobdeve.s13.grp7.pokelearn.PokemonDetailsActivity
import com.mobdeve.s13.grp7.pokelearn.common.Common
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

        internal var itemClickListener: IItemClickListener? = null

        fun setItemClickListener(iItemClickListener: IItemClickListener) {
            this.itemClickListener = iItemClickListener;
        }

        init {
            pokemonListItemBinding.root.setOnClickListener(this)
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
                Toast.makeText(activity, "Clicked at Pokemon: " + pokeListItemModels[position].name, Toast.LENGTH_SHORT).show()
//                val poke: Pokemon = pokeListItemModels[position]
//                val intent = Intent(v?.context, PokemonDetailsActivity::class.java).apply {
//                    putExtra("POKE_ID", poke.id)
//                    putExtra("position", position) // Adding position as extra
//                    putExtra(Common.KEY_ENABLE_HOME, true) // Adding KEY_ENABLE_HOME
//                }
//                v?.context?.startActivity(intent)

//
            }
        }
    }




}