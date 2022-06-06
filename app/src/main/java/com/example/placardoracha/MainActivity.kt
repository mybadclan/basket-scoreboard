package com.example.placardoracha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val createGameButton: Button = findViewById(R.id.button_create_game)
        val historyButton: Button = findViewById(R.id.button_history)

        goToCreateActivity(createGameButton)
        goToMatchHistoryActivity(historyButton)
    }

    private fun goToCreateActivity(view: Button) {
        view.setOnClickListener {
            val intent = Intent(this, CreateGameActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToMatchHistoryActivity(view: Button) {
        view.setOnClickListener {
            val intent = Intent(this, MatchHistoryActivity::class.java)
            startActivity(intent)
        }
    }
}

