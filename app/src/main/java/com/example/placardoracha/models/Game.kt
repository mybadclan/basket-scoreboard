package com.example.placardoracha.models

import java.io.Serializable
import java.util.*


data class Game(
    val teamOne: String,
    val teamTwo: String,
    val teamOneScore: Int = 0,
    val teamTwoScore: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0,
    var gameDate: Calendar = Calendar.getInstance()
): Serializable