package com.mobdeve.s13.grp7.pokelearn.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import org.json.JSONObject

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val context: Context? = null

    companion object {
        private const val DATABASE_NAME = "PokemonDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Pokemon"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_TYPE = "type"
        private const val COL_SPECIES = "species"
        private const val COL_DESCRIPTION = "description"
        private const val COL_ABILITY = "ability"
        private const val COL_HEIGHT = "height"
        private const val COL_WEIGHT = "weight"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableStatement = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY," +
                "$COL_NAME TEXT," +
                "$COL_TYPE TEXT," +
                "$COL_SPECIES TEXT," +
                "$COL_DESCRIPTION TEXT," +
                "$COL_ABILITY TEXT," +
                "$COL_HEIGHT TEXT," +
                "$COL_WEIGHT TEXT)"

        db?.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addPokemon(pokemon: JSONObject): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_ID, pokemon.getInt("id"))
        cv.put(COL_NAME, pokemon.getString("name"))
        cv.put(COL_TYPE, pokemon.getJSONArray("type").toString())
        cv.put(COL_SPECIES, pokemon.getString("species"))
        cv.put(COL_DESCRIPTION, pokemon.getString("description"))
        cv.put(COL_ABILITY, pokemon.getJSONArray("ability").toString())
        cv.put(COL_HEIGHT, pokemon.getString("height"))
        cv.put(COL_WEIGHT, pokemon.getString("weight"))

        val result = db.insert(TABLE_NAME, null, cv)

        return result != -1L
    }

    fun updatePokemon(pokemon: JSONObject): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_NAME, pokemon.getString("name"))
        cv.put(COL_TYPE, pokemon.getJSONArray("type").toString())
        cv.put(COL_SPECIES, pokemon.getString("species"))
        cv.put(COL_DESCRIPTION, pokemon.getString("description"))
        cv.put(COL_ABILITY, pokemon.getJSONArray("ability").toString())
        cv.put(COL_HEIGHT, pokemon.getString("height"))
        cv.put(COL_WEIGHT, pokemon.getString("weight"))

        val result = db.update(TABLE_NAME, cv, "$COL_ID=?", arrayOf(pokemon.getString("id")))

        return result > 0
    }

    fun deletePokemon(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))

        return result > 0
    }

    fun getPokemon(id: Int): JSONObject? {
        val db = this.readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_ID=?", arrayOf(id.toString()))
        if (queryResult.moveToFirst()) {
            val pokemon = JSONObject()

            try {
                pokemon.put("id", queryResult.getInt(queryResult.getColumnIndexOrThrow(COL_ID)))
                pokemon.put("name", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_NAME)))
                pokemon.put("type", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_TYPE)))
                pokemon.put("species", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_SPECIES)))
                pokemon.put("description", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_DESCRIPTION)))
                pokemon.put("ability", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_ABILITY)))
                pokemon.put("height", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_HEIGHT)))
                pokemon.put("weight", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_WEIGHT)))
            } catch (e: IllegalArgumentException) {
                // Log the error
                Log.e("MyDatabaseHelper", "Column not found", e)
                // Provide a suitable error message
                Toast.makeText(this.context, "An error occurred while retrieving the Pokémon data.", Toast.LENGTH_SHORT).show()
                return null
            }

            queryResult.close()

            return pokemon
        }

        queryResult.close()

        return null
    }

    fun getAllPokemon(): List<JSONObject> {
        val db = this.readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        val pokemonList = mutableListOf<JSONObject>()

        if (queryResult.moveToFirst()) {
            do {
                val pokemon = JSONObject()

                pokemon.put("id", queryResult.getInt(queryResult.getColumnIndexOrThrow(COL_ID)))
                pokemon.put("name", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_NAME)))
                pokemon.put("type", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_TYPE)))
                pokemon.put("species", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_SPECIES)))
                pokemon.put("description", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_DESCRIPTION)))
                pokemon.put("ability", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_ABILITY)))
                pokemon.put("height", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_HEIGHT)))
                pokemon.put("weight", queryResult.getString(queryResult.getColumnIndexOrThrow(COL_WEIGHT)))

                pokemonList.add(pokemon)
            } while (queryResult.moveToNext())
        }

        queryResult.close()

        return pokemonList
    }



}