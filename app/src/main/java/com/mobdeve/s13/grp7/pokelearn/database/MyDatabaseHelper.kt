package com.mobdeve.s13.grp7.pokelearn.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s13.grp7.pokelearn.model.PokemonNew
import com.mobdeve.s13.grp7.pokelearn.model.UserProfile
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

    fun addPokemon(pokemon: PokemonNew): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_ID, pokemon.id)
        cv.put(COL_NAME, pokemon.name)
        cv.put(COL_TYPE, pokemon.type.joinToString())
        cv.put(COL_SPECIES, pokemon.species)
        cv.put(COL_DESCRIPTION, pokemon.description)
        cv.put(COL_ABILITY, pokemon.ability.joinToString())
        cv.put(COL_HEIGHT, pokemon.height)
        cv.put(COL_WEIGHT, pokemon.weight)

        val result = db.insert(TABLE_NAME, null, cv)

        return result != -1L
    }

    fun updatePokemon(pokemon: PokemonNew): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_NAME, pokemon.name)
        cv.put(COL_TYPE, pokemon.type.joinToString())
        cv.put(COL_SPECIES, pokemon.species)
        cv.put(COL_DESCRIPTION, pokemon.description)
        cv.put(COL_ABILITY, pokemon.ability.joinToString())
        cv.put(COL_HEIGHT, pokemon.height)
        cv.put(COL_WEIGHT, pokemon.weight)

        val result = db.update(TABLE_NAME, cv, "$COL_ID=?", arrayOf(pokemon.id.toString()))

        return result > 0
    }

    fun deletePokemon(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))

        return result > 0
    }

    fun getPokemon(id: Int): PokemonNew? {
        val db = this.readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_ID=?", arrayOf(id.toString()))
        var pokemon: PokemonNew? = null
        if (queryResult.moveToFirst()) {
            pokemon = PokemonNew().apply {
                this.id = queryResult.getInt(queryResult.getColumnIndexOrThrow(COL_ID))
                this.name = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_NAME))
                this.type = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_TYPE)).split(",")
                this.species = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_SPECIES))
                this.description = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_DESCRIPTION))
                this.ability = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_ABILITY)).split(",")
                this.height = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_HEIGHT))
                this.weight = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_WEIGHT))
            }
            queryResult.close()
        }
        return pokemon
    }

    fun getAllPokemon(): List<PokemonNew> {
        val db = this.readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        val pokemonList = mutableListOf<PokemonNew>()

        if (queryResult.moveToFirst()) {
            do {
                val id = queryResult.getInt(queryResult.getColumnIndexOrThrow(COL_ID))
                val name = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_NAME))
                val type = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_TYPE)).split(",")
                val species = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_SPECIES))
                val description = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_DESCRIPTION))
                val ability = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_ABILITY)).split(",")
                val height = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_HEIGHT))
                val weight = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_WEIGHT))

                val pokemon = PokemonNew().apply {
                    this.id = id
                    this.name = name
                    this.type = type
                    this.species = species
                    this.description = description
                    this.ability = ability
                    this.height = height
                    this.weight = weight
                }

                pokemonList.add(pokemon)
            } while (queryResult.moveToNext())
        }

        queryResult.close()

        return pokemonList
    }



}


class UserProfileDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, USER_DATABASE_NAME, null, DATABASE_VERSION) {


    // Firebase reference
    private val database = FirebaseDatabase.getInstance("https://pokelearn-aeb5e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("userprofiles")

    companion object {
        private const val USER_DATABASE_NAME = "UserDatabase"
        private const val USER_TABLE_NAME = "UserProfile"
        private const val DATABASE_VERSION = 1
        private const val COL_UID = "uid"
        private const val COL_USERNAME = "username"
        private const val COL_POKEDEX = "pokedex"
        private const val COL_POMODORO_CYCLES = "fullPomodoroCyclesCompleted"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableStatement = "CREATE TABLE $USER_TABLE_NAME (" +
                "$COL_UID TEXT PRIMARY KEY," +
                "$COL_USERNAME TEXT," +
                "$COL_POKEDEX TEXT," +
                "$COL_POMODORO_CYCLES INTEGER)"

        db?.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USER_TABLE_NAME")
        onCreate(db)
    }

    // Add methods for adding, updating, and deleting user profiles as needed

    fun addUserProfile(userProfile: UserProfile): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_UID, userProfile.uid)
        cv.put(COL_USERNAME, userProfile.username)
        cv.put(COL_POKEDEX, userProfile.pokedex.joinToString())
        cv.put(COL_POMODORO_CYCLES, userProfile.fullPomodoroCyclesCompleted)

        val result = db.insert(USER_TABLE_NAME, null, cv)

        return result != -1L
    }

    fun updateUserProfile(userProfile: UserProfile): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_USERNAME, userProfile.username)
        cv.put(COL_POKEDEX, userProfile.pokedex.joinToString())
        cv.put(COL_POMODORO_CYCLES, userProfile.fullPomodoroCyclesCompleted)

        val result = db.update(USER_TABLE_NAME, cv, "$COL_UID=?", arrayOf(userProfile.uid))

        return result > 0
    }

    // Call this function whenever you change the SQLite database
    fun updateFirebaseDatabase(uid: String) {
        val userProfile = getUserProfile(uid)
        if (userProfile != null) {
            // Convert the UserProfile object to a format suitable for Firebase
            val dataForFirebase = mapOf(
                "uid" to userProfile.uid,
                "username" to userProfile.username,
                "pokedex" to userProfile.pokedex.joinToString(),
                "fullPomodoroCyclesCompleted" to userProfile.fullPomodoroCyclesCompleted
            )

            // Update Firebase
            database.child(userProfile.uid).setValue(dataForFirebase)
            Log.d("Firebase", "Firebase database updated");
        }
    }



    fun deleteUserProfile(uid: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(USER_TABLE_NAME, "$COL_UID=?", arrayOf(uid))

        return result > 0
    }

    //function to add pokemon to pokedex
    fun addPokemonToUser(uid: String, pokemonId: String): Boolean {
        val db = this.writableDatabase
        val userProfile = getUserProfile(uid)
        if (userProfile != null) {
            userProfile.pokedex.add(pokemonId)
            return updateUserProfile(userProfile)
        }
        return false
    }

    // function for getting the pokedex list given uid
    fun getPokedex(uid: String): ArrayList<String>? {
        val userProfile = getUserProfile(uid)
        return userProfile?.pokedex
    }



    fun getUserProfile(uid: String): UserProfile? {
        val db = this.readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $USER_TABLE_NAME WHERE $COL_UID=?", arrayOf(uid))
        var userProfile: UserProfile? = null
        if (queryResult.moveToFirst()) {
            userProfile = UserProfile().apply {
                this.uid = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_UID))
                this.username = queryResult.getString(queryResult.getColumnIndexOrThrow(COL_USERNAME))
                this.pokedex =
                    queryResult.getString(queryResult.getColumnIndexOrThrow(COL_POKEDEX)).split(",").toMutableList() as ArrayList<String>
                this.fullPomodoroCyclesCompleted = queryResult.getInt(queryResult.getColumnIndexOrThrow(COL_POMODORO_CYCLES))
            }
            queryResult.close()
        }
        return userProfile
    }
}

