package com.mobdeve.s13.grp7.pokelearn.model

class UserProfile(
    var uid: String = "",
    var username: String = "",
    var pokedex: ArrayList<String> = arrayListOf("1"),
    var fullPomodoroCyclesCompleted: Int = 0,
    var totalTimeSpent: Int = 0


)