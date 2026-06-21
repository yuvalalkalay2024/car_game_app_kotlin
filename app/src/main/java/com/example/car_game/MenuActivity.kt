package com.example.obstacle_game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.car_game.MainActivity

import com.example.car_game.R;
import com.example.car_game.ScoreboardActivity


class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val rgControls = findViewById<RadioGroup>(R.id.rg_controls)
        val rgSpeed = findViewById<RadioGroup>(R.id.rg_speed)
        val btnStart = findViewById<Button>(R.id.btn_start_game)

        // הכפתור החדש שיצרנו
        val btnHighScores = findViewById<Button>(R.id.btn_high_scores)

        // לחיצה על התחלת משחק
        btnStart.setOnClickListener {
            val isSensorMode = rgControls.checkedRadioButtonId == R.id.rb_sensor
            val isFastMode = rgSpeed.checkedRadioButtonId == R.id.rb_fast

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SENSOR_MODE", isSensorMode)
            intent.putExtra("FAST_MODE", isFastMode)
            startActivity(intent)
            finish()
        }

        // לחיצה על טבלת שיאים (פותח את מסך השיאים בלי לסגור את התפריט)
        btnHighScores.setOnClickListener {
            val intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }
    }
}