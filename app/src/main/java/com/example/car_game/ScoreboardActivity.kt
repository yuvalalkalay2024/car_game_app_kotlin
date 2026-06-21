package com.example.car_game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ScoreboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        // ה-XML עצמו טוען את הפרגמנטים בצורה סטטית, ולכן אין צורך בקוד מורכב כאן!
    }
}