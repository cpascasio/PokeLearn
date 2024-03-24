package com.mobdeve.s13.grp7.pokelearn.model

class PokeListItemModel(val id: Int, val name: String, val type: String, val image: Int){
    companion object {
        private const val DEFAULT_ID = -1
    }

    constructor(name: String, type: String, image: Int) : this(DEFAULT_ID, name, type, image)
    constructor() : this(DEFAULT_ID, "Blank", "Type", 0)
}