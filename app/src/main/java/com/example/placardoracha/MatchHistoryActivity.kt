package com.example.placardoracha

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.placardoracha.adapters.MatchHistoryAdapter
import com.example.placardoracha.models.Game
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class MatchHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_history)

        val recyclerView: RecyclerView = findViewById(R.id.rcv_history_cards)
        val removeButton: FloatingActionButton = findViewById(R.id.remove_button)

        recyclerView.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = MatchHistoryAdapter(getSavedGames())
        }

        removeButton.setOnClickListener {
            removeGames()
            recyclerView.adapter = MatchHistoryAdapter(getSavedGames())
        }
    }

    private fun getSavedGames(): List<Game> {
        val sharedFilename = "games"
        val sharedMode = Context.MODE_PRIVATE
        val sharedPreferences = getSharedPreferences(sharedFilename, sharedMode)

        val size = sharedPreferences.getInt("size", 0)
        val list = if (size - 5 < 0) 0 until size else (size - 5) until size

        return list.toList()
            .map { value -> "match_$value" }
            .filter { sharedPreferences.getString(it, "").toString().isNotEmpty() }
            .map {
                val gameStr = sharedPreferences.getString(it, "").toString()
                val dis = ByteArrayInputStream(gameStr.toByteArray(Charsets.ISO_8859_1))

                ObjectInputStream(dis).readObject() as Game
            }
            .asReversed()
    }

    private fun removeGames() {
        val sharedFilename = "games"
        val sharedMode = Context.MODE_PRIVATE
        val sharedPreferences = getSharedPreferences(sharedFilename, sharedMode)

        val editor = sharedPreferences.edit()
        editor.putInt("size", 0)
        editor.clear()
        editor.apply()
    }
}