package com.example.labyrinthe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameOn : AppCompatActivity(){
    private lateinit var labyrinth: Labyrinth

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        labyrinth = Labyrinth(this)

//        setContentView(alienSolarSystemTouch)
        setContentView(labyrinth)
    }
}