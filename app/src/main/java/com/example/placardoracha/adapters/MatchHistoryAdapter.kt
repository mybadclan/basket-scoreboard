package com.example.placardoracha.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placardoracha.R
import com.example.placardoracha.models.Game
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MatchHistoryAdapter(private val games: List<Game>) :
    RecyclerView.Adapter<MatchHistoryAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val teamOne: TextView = ItemView.findViewById(R.id.team_one)
        val teamTwo: TextView = ItemView.findViewById(R.id.team_two)
        val scoreTeamOne: TextView = ItemView.findViewById(R.id.score_team_one)
        val scoreTeamTwo: TextView = ItemView.findViewById(R.id.score_team_two)
        val gameDate: TextView = ItemView.findViewById(R.id.date_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_history_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentGame = games[position]

        val formatter = DecimalFormat("#00")
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())

        holder.apply {
            teamOne.text = currentGame.teamOne
            teamTwo.text = currentGame.teamTwo
            scoreTeamOne.text = formatter.format(currentGame.teamOneScore)
            scoreTeamTwo.text = formatter.format(currentGame.teamTwoScore)
            gameDate.text = dateFormatter.format(currentGame.gameDate.time)
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }
}