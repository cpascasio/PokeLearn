package com.mobdeve.s13.grp7.pokelearn.adapter


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s13.grp7.pokelearn.Interface.IItemClickListener
import com.mobdeve.s13.grp7.pokelearn.PokemonDetailsActivity
import com.mobdeve.s13.grp7.pokelearn.common.Common
import com.mobdeve.s13.grp7.pokelearn.database.MyDatabaseHelper
import com.mobdeve.s13.grp7.pokelearn.databinding.PokemonListItemBinding
import com.mobdeve.s13.grp7.pokelearn.model.PokeListItemModel
import com.mobdeve.s13.grp7.pokelearn.model.Pokemon
import com.mobdeve.s13.grp7.pokelearn.model.PokemonNew

class PokemonListAdapter(private val pokemonList: List<PokemonNew>, private val context: Context) : RecyclerView.Adapter<PokemonListAdapter.PokemonListViewHolder>(){

    // Handles the logic needed for getItemCount().
    override fun getItemCount(): Int {
        return this.pokemonList.size
    }

    // Note: This method uses ViewBinding instead of inflating the ViewGroup.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonListAdapter.PokemonListViewHolder {
        val pokemonListItemBinding = PokemonListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonListViewHolder(pokemonListItemBinding)
    }


    // Handles binding a model to the ViewHolder.
    override fun onBindViewHolder(holder: PokemonListAdapter.PokemonListViewHolder, position: Int) {
        holder.bindPokeListItem(this.pokemonList[position], position)
    }

    inner class PokemonListViewHolder(private val pokemonListItemBinding: PokemonListItemBinding): RecyclerView.ViewHolder(pokemonListItemBinding.root), View.OnClickListener {

        private var myPosition: Int = -1

        private lateinit var item: PokemonNew

        internal var itemClickListener: IItemClickListener? = null

        fun setItemClickListener(iItemClickListener: IItemClickListener) {
            this.itemClickListener = iItemClickListener;
        }

        init {
            pokemonListItemBinding.root.setOnClickListener(this)
        }


        fun bindPokeListItem(pokeListItemModel: PokemonNew, position: Int) {
            this@PokemonListViewHolder.myPosition = position
            this@PokemonListViewHolder.item = pokeListItemModel

            with(pokemonListItemBinding){
                tvPokeName.text = pokeListItemModel.name

                if(pokeListItemModel.id > 9){
                    tvPokeId.text = "#0${pokeListItemModel.id}"
                } else {
                    tvPokeId.text = "#00${pokeListItemModel.id}"
                }



                val drawableName = "poke_${pokeListItemModel.id}"
                Log.d("PokemonListAdapter", "pokeName: $pokeListItemModel.name")
                Log.d("PokemonListAdapter", "Drawable Name: $drawableName")
                val resId = itemView.context.resources.getIdentifier(drawableName, "drawable", itemView.context.packageName)

                Glide.with(itemView.context)
                    .load(resId)
                    .centerCrop()
                    .into(ivPokeSprite)


//                Glide.with(itemView.context)
//                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${position + 1}.png")
//                    .centerCrop()
//                    .into(ivPokeSprite)

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
                Toast.makeText(context, "Clicked at Pokemon: " + pokemonList[position].name, Toast.LENGTH_SHORT).show()

                // Create a new intent to start the PokemonDetailsActivity
                val intent = Intent(context, PokemonDetailsActivity::class.java)

                // Add each attribute of the PokemonNew object at the clicked position as an extra to the intent
                intent.putExtra("Name", pokemonList[position].name)
                intent.putExtra("Id", pokemonList[position].id)
                intent.putExtra("Ability", ArrayList(pokemonList[position].ability.map { it.removeSurrounding("[", "]") }))
                intent.putExtra("Type", ArrayList(pokemonList[position].type.map { it.removeSurrounding("[", "]") }))
                intent.putExtra("Species", pokemonList[position].species)
                intent.putExtra("Description", pokemonList[position].description)
                intent.putExtra("Ability", ArrayList(pokemonList[position].ability).joinToString(", "))
                intent.putExtra("Height", pokemonList[position].height)
                intent.putExtra("Weight", pokemonList[position].weight)

                // Start the activity with the intent
                context.startActivity(intent)
//

//
            }
        }
    }




}