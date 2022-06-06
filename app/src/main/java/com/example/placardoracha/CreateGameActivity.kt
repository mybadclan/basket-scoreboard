package com.example.placardoracha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.example.placardoracha.models.Game
import java.time.LocalDateTime
import java.util.*

class CreateGameActivity : AppCompatActivity() {
    private var teamOneName = ""
    private var teamTwoName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        val teamOneInput: EditText = findViewById(R.id.team_one_input)
        val teamTwoInput: EditText = findViewById(R.id.team_two_input)
        val registerButton: Button = findViewById(R.id.button_register)

        teamOneInput.addTextChangedListener { teamOneName = it.toString() }
        teamTwoInput.addTextChangedListener { teamTwoName = it.toString() }

        handleRegister(registerButton) {
            teamOneInput.text.clear()
            teamTwoInput.text.clear()
            teamOneName.let { "" }
            teamTwoName.let { "" }
        }
    }

    private fun handleRegister(view: Button, fn: () -> Unit) {
        view.setOnClickListener {
            if (teamOneName.isNotBlank() && teamTwoName.isNotBlank()) {
                val intent = Intent(this, ScoreBoardActivity::class.java).apply {
                    putExtra("game", Game(teamOneName, teamTwoName))
                }
                fn()
                startActivity(intent)
            }
        }
    }

}