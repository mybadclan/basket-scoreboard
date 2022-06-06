package com.example.placardoracha

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import com.example.placardoracha.models.Game
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat

class ScoreBoardActivity : AppCompatActivity() {
    private var isPlaying = false
    private var pauseOffset: Long = 0
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_board)

        val scoreOne: TextView = findViewById(R.id.team_one_points)
        val scoreTwo: TextView = findViewById(R.id.team_two_points)
        val teamOne: TextView = findViewById(R.id.team_one)
        val teamTwo: TextView = findViewById(R.id.team_two)
        val gameTime: TextView = findViewById(R.id.game_time)
        val chronometer: Chronometer = findViewById(R.id.chronometer)
        val resetButton: Button = findViewById(R.id.stop_button)
        val playPauseButton: Button = findViewById(R.id.play_pause_button)
        val randomButton: FloatingActionButton = findViewById(R.id.random_score_button)

        game = intent.extras?.getSerializable("game") as Game

        savedInstanceState?.run {
            game.copy(
                teamOne = getString(TEAM_ONE).toString(),
                teamTwo = getString(TEAM_TWO).toString(),
                teamOneScore = getInt(SCORE_ONE),
                teamTwoScore = getInt(SCORE_TWO)
            ).also { game = it }
        }

        setTeams(teamOne, teamTwo, scoreOne, scoreTwo)

        val gameTimeLabels = mapOf(1 to R.string.game_time_one,
            2 to R.string.game_time_two,
            3 to R.string.game_time_three,
            4 to R.string.game_time_four)

        handleScoreClick(scoreOne) { game.copy(teamOneScore = it).also { it1 -> game = it1 } }
        handleScoreClick(scoreTwo) { game.copy(teamTwoScore = it).also { it1 -> game = it1 } }
        handlePLayChronometer(chronometer, playPauseButton)
        handleResetChronometer(chronometer, resetButton, playPauseButton)

        handleGameTime(chronometer) { ch, min, sec ->
            if (min == 40 && sec == 0) {
                ch.base = SystemClock.elapsedRealtime()
                pauseOffset = 0

                playPauseButton.apply {
                    text = getString(R.string.button_play)
                    isClickable = false
                }

                saveGame()
            } else {
                val gameTimeLabel = gameTimeLabels[min!!.div(10)]
                gameTime.text = gameTimeLabel?.let { it -> getString(it) }
                playPauseButton.text = getString(R.string.button_play)
                pauseOffset = SystemClock.elapsedRealtime() - ch.base
            }
        }

        randomButton.setOnClickListener {
            val scoreOneValue = (60..120).random()
            val scoreTwoValue = (60..120).random()

            scoreOne.text = scoreOneValue.toString()
            scoreTwo.text = scoreTwoValue.toString()

            game.copy(
                teamOneScore = scoreOneValue,
                teamTwoScore = scoreTwoValue,
                gameDate = game.gameDate
            ).also { it1 -> game = it1 }

            saveGame()

            val intent = Intent(this, MatchHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString(TEAM_ONE, game.teamOne)
            putString(TEAM_TWO, game.teamTwo)
            putInt(SCORE_ONE, game.teamOneScore)
            putInt(SCORE_TWO, game.teamTwoScore)
        }

        super.onSaveInstanceState(outState)
    }

    private fun setTeams(
        teamOne: TextView,
        teamTwo: TextView,
        scoreOne: TextView,
        scoreTwo: TextView,
    ) {
        val formatter = DecimalFormat("#00")

        teamOne.text = game.teamOne
        teamTwo.text = game.teamTwo
        scoreOne.text = formatter.format(game.teamOneScore)
        scoreTwo.text = formatter.format(game.teamTwoScore)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleScoreClick(view: TextView, fn: (score: Int) -> Unit) {
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val score = view.text.toString().toInt()
                val formatter = DecimalFormat("#00")
                val rootXCenter = v.rootView.width / 2
                val viewX = event.x

                val newScore = if (viewX >= rootXCenter) score + 1 else score - 1
                val fixedScore = if (newScore <= 0) 0 else newScore

                fn(fixedScore)
                view.text = formatter.format(fixedScore)
            }

            true
        }
    }

    private fun handlePLayChronometer(chronometer: Chronometer, playButton: Button) {
        playButton.setOnClickListener {
            isPlaying = if (!isPlaying) {
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
                true
            } else {
                chronometer.stop()
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
                false
            }

            playButton.text =
                if (!isPlaying) getString(R.string.button_play) else getString(R.string.button_pause)
        }
    }

    private fun handleResetChronometer(
        chronometer: Chronometer,
        resetButton: Button,
        playButton: Button,
    ) {
        resetButton.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.stop()
            pauseOffset = 0
            isPlaying = false

            playButton.text = getString(R.string.button_play)
        }
    }

    private fun handleGameTime(
        chronometer: Chronometer,
        fn: (it: Chronometer, minutes: Int?, seconds: Int?) -> Unit,
    ) {
        chronometer.setOnChronometerTickListener {
            val (minutes, seconds) = getTime(it.base)

            isPlaying = if (minutes != 0 && minutes % 10 == 0 && seconds == 0) {
                it.stop()
                fn(it, minutes, seconds)
                false
            } else {
                true
            }
        }
    }

    private fun saveGame() {
        val sharedFilename = "games"
        val sharedMode = Context.MODE_PRIVATE
        val sharedPreferences = getSharedPreferences(sharedFilename, sharedMode)
        val sharedPreferencesEdition = sharedPreferences.edit()

        sharedPreferencesEdition.run {
            val count = sharedPreferences.getInt("size", 0)
                .let {
                    putInt("size", it + 1)
                    it
                }

            val dt = ByteArrayOutputStream().apply {
                ObjectOutputStream(this).writeObject(game)
            }

            putString("match_$count", dt.toString(StandardCharsets.ISO_8859_1.name()))
            commit()
        }
    }

    private fun getTime(base: Long): Pair<Int, Int> {
        val milliseconds = SystemClock.elapsedRealtime() - base
        val minutes = (milliseconds / 1000 / 60).toInt()
        val seconds = ((milliseconds / 1000) % 60).toInt()

        return Pair(minutes, seconds)
    }

    companion object {
        const val TEAM_ONE = "team_one"
        const val TEAM_TWO = "team_two"
        const val SCORE_ONE = "score_one"
        const val SCORE_TWO = "score_two"
    }
}