package com.example.obstacle_game.com.example.car_game.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.car_game.MainActivity
import com.example.car_game.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val rgControls = findViewById<RadioGroup>(R.id.rg_controls)
        val rgSpeed = findViewById<RadioGroup>(R.id.rg_speed)
        val btnStart = findViewById<Button>(R.id.btn_start_game)

        btnStart.setOnClickListener {
            // בודק מה סומן בתפריט
            val isSensorMode = rgControls.checkedRadioButtonId == R.id.rb_sensor
            val isFastMode = rgSpeed.checkedRadioButtonId == R.id.rb_fast

            // מעביר את הנתונים ל-MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SENSOR_MODE", isSensorMode)
            intent.putExtra("FAST_MODE", isFastMode)
            startActivity(intent)

            // סוגר את התפריט כדי שלחיצה על כפתור 'חזור' בטלפון לא תחזיר אותנו לפה
            finish()
        }
    }
}