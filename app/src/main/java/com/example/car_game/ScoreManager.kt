package com.example.car_game

import android.content.Context

object ScoreManager {
    private const val PREFS_NAME = "GameScoresPrefs"
    private const val SCORES_KEY = "ScoresString"

    // שמירת שיא חדש
    fun saveScore(context: Context, newScore: HighScore) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentScoresStr = prefs.getString(SCORES_KEY, "") ?: ""

        val scoresList = parseScores(currentScoresStr).toMutableList()
        scoresList.add(newScore)

        // מיון מהגבוה לנמוך ושמירת עשרת הראשונים בלבד
        scoresList.sortByDescending { it.score }
        val top10 = scoresList.take(10)

        // המרה חזרה למחרוזת ושמירה
        val newScoresStr = top10.joinToString(";") { "${it.score},${it.date},${it.latitude},${it.longitude}" }
        prefs.edit().putString(SCORES_KEY, newScoresStr).apply()
    }

    // שליפת כל השיאים
    fun getScores(context: Context): List<HighScore> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val scoresStr = prefs.getString(SCORES_KEY, "") ?: ""
        return parseScores(scoresStr)
    }

    // פונקציית עזר לפענוח המחרוזת
    private fun parseScores(scoresStr: String): List<HighScore> {
        if (scoresStr.isEmpty()) return emptyList()
        return scoresStr.split(";").mapNotNull {
            val parts = it.split(",")
            if (parts.size == 4) {
                try {
                    HighScore(parts[0].toInt(), parts[1], parts[2].toDouble(), parts[3].toDouble())
                } catch (e: Exception) { null }
            } else null
        }
    }
}