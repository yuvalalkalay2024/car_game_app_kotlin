package com.example.car_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScoresListFragment : Fragment() {

    private val viewModel: ScoreViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scores_list, container, false)

        val rvScores = view.findViewById<RecyclerView>(R.id.rv_scores)
        rvScores.layoutManager = LinearLayoutManager(context)

        // שולף את רשימת השיאים האמיתית מה-SharedPreferences
        val savedScores = ScoreManager.getScores(requireContext())

        // מזין אותם לתוך האדפטר (הטבלה)
        rvScores.adapter = ScoresAdapter(savedScores) { highScore ->
            viewModel.selectLocation(highScore.latitude, highScore.longitude)
        }

        return view
    }

    // אדפטר פנימי עבור ה-RecyclerView
    class ScoresAdapter(private val items: List<HighScore>, private val onItemClick: (HighScore) -> Unit) :
        RecyclerView.Adapter<ScoresAdapter.ViewHolder>() {

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvRank: TextView = v.findViewById(R.id.tv_rank)
            val tvScore: TextView = v.findViewById(R.id.tv_score_value)
            val tvDate: TextView = v.findViewById(R.id.tv_date)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvRank.text = "${position + 1}."
            holder.tvScore.text = "${item.score} Pts"
            holder.tvDate.text = item.date
            holder.itemView.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount() = items.size
    }
}