package com.example.car_game

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.obstacle_game.GameManager
import com.example.obstacle_game.MenuActivity
import kotlin.collections.get

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gameManager: GameManager
    private lateinit var signalManager: SignalManager

    private lateinit var obstacleViews: Array<Array<ImageView>>
    private lateinit var carViews: Array<ImageView>
    private lateinit var heartViews: Array<ImageView>
    private lateinit var tvCoins: TextView // משתנה למד המטבעות

    private lateinit var tvDistance: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var delayMillis = 1000L

    private var isSensorMode = false
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastSensorMoveTime = 0L

    private val gameLoopRunnable = object : Runnable {
        override fun run() {
            gameManager.advanceObstacles()
            checkCrashAndUpdateUI()
            handler.postDelayed(this, delayMillis)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvDistance = findViewById(R.id.tv_distance)
        isSensorMode = intent.getBooleanExtra("SENSOR_MODE", false)
        val isFastMode = intent.getBooleanExtra("FAST_MODE", false)

        delayMillis = if (isFastMode) 500L else 1000L

        gameManager = GameManager(rows = 16, cols = 5)
        signalManager = SignalManager(this)

        tvCoins = findViewById(R.id.tv_coins) // קישור מד המטבעות מה-XML

        initViews()
        setupControls()

        updateUI()
        handler.postDelayed(gameLoopRunnable, delayMillis)
    }

    private fun initViews() {
        // טעינת רשת ה-ImageViews (ללא קביעת תמונה קבועה מראש)
        obstacleViews = Array(16) { i ->
            Array(5) { j ->
                val resID = resources.getIdentifier("obstacle_${i}_${j}", "id", packageName)
                findViewById<ImageView>(resID)
            }
        }

        carViews = Array(5) { j ->
            val resID = resources.getIdentifier("car_$j", "id", packageName)
            val imageView = findViewById<ImageView>(resID)
            Glide.with(this).load(R.drawable.car).into(imageView)
            imageView
        }

        heartViews = arrayOf(
            findViewById(R.id.heart_1),
            findViewById(R.id.heart_2),
            findViewById(R.id.heart_3)
        )
        for (heart in heartViews) {
            Glide.with(this).load(R.drawable.heart).into(heart)
        }
    }

    private fun setupControls() {
        val btnLeft = findViewById<ImageButton>(R.id.btn_left)
        val btnRight = findViewById<ImageButton>(R.id.btn_right)

        if (isSensorMode) {
            btnLeft.visibility = View.INVISIBLE
            btnRight.visibility = View.INVISIBLE
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        } else {
            btnLeft.setOnClickListener { gameManager.moveCarLeft(); checkCrashAndUpdateUI() }
            btnRight.setOnClickListener { gameManager.moveCarRight(); checkCrashAndUpdateUI() }
        }
    }

    // --- אירועי חיישן התאוצה (מעודכן עם שליטת מהירות) ---
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && isSensorMode) {
            val currentTime = System.currentTimeMillis()

            // 1. שליטה ימינה/שמאלה (ציר X) - הקוד הקיים שלך
            if (currentTime - lastSensorMoveTime > 300) {
                val x = event.values[0]
                if (x > 2.5) { // הטיה שמאלה
                    gameManager.moveCarLeft()
                    checkCrashAndUpdateUI()
                    lastSensorMoveTime = currentTime
                } else if (x < -2.5) { // הטיה ימינה
                    gameManager.moveCarRight()
                    checkCrashAndUpdateUI()
                    lastSensorMoveTime = currentTime
                }
            }

            // 2. שליטה במהירות (ציר Y) - התוספת החדשה!
            val y = event.values[1]

            if (y < 3.5) {
                // הטיה קדימה (Tilt Forth) -> המכשולים יטוסו מהר! (השהייה של 350 מילישניות)
                delayMillis = 350L
            } else if (y > 7.5) {
                // הטיה אחורה אליך (Tilt Back) -> המשחק יאט (השהייה של 1200 מילישניות)
                delayMillis = 1200L
            } else {
                // מצב אחיזה רגיל ונוח -> מהירות בינונית מאוזנת (השהייה של 750 מילישניות)
                delayMillis = 750L
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        if (isSensorMode) {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isSensorMode) {
            sensorManager.unregisterListener(this)
        }
    }

    private fun checkCrashAndUpdateUI() {
        val collisionResult = gameManager.checkCollision()

        if (collisionResult == 1) { // התנגשות במכשול
            signalManager.toast("Crash!")
            signalManager.vibrate()
            signalManager.playSound()

            if (gameManager.isGameOver()) {
                signalManager.toast("Game Over!")

                // --- תחילת הקוד החדש ששומר שיא ---
                // נחשב את הניקוד (למשל, כל מטבע שווה 10 נקודות)
                val finalScore = gameManager.coinsCollected + gameManager.distanceTraveled

                // משיכת התאריך של היום
                val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val currentDate = sdf.format(java.util.Date())

                // הגרלת קואורדינטות בתוך גבולות ישראל בשביל המפה
                val randomLat = 29.5 + Math.random() * (33.0 - 29.5)
                val randomLon = 34.3 + Math.random() * (35.8 - 34.3)

                // יצירת אובייקט השיא ושמירתו
                val newScore = HighScore(finalScore, currentDate, randomLat, randomLon)
                ScoreManager.saveScore(this, newScore)
                // --- סוף הקוד החדש ---

                gameManager.resetGame()
                handler.removeCallbacks(gameLoopRunnable)

                val intent = android.content.Intent(this, ScoreboardActivity::class.java)
                startActivity(intent)
                finish()
                return
            }
        } else if (collisionResult == 2) { // איסוף מטבע בהצלחה
            // כאן אפשר להוסיף בעתיד סאונד קצר של מטבע דרך ה-signalManager
        }
        updateUI()
    }

    private fun updateUI() {
        // עדכון רשת המשחק לפי סוג הרכיב (ריק, מכשול או מטבע)
        for (i in 0 until 16) {
            for (j in 0 until 5) {
                val itemType = gameManager.obstacleMatrix[i][j]
                when (itemType) {
                    1 -> { // מכשול
                        obstacleViews[i][j].setImageResource(R.drawable.obstacle)
                        obstacleViews[i][j].visibility = View.VISIBLE
                    }
                    2 -> { // מטבע
                        obstacleViews[i][j].setImageResource(R.drawable.coin)
                        obstacleViews[i][j].visibility = View.VISIBLE
                    }
                    else -> { // ריק
                        obstacleViews[i][j].visibility = View.INVISIBLE
                    }
                }
            }
        }

        for (j in 0 until 5) {
            carViews[j].visibility = if (j == gameManager.carLane) View.VISIBLE else View.INVISIBLE
        }

        for (i in 0 until 3) {
            heartViews[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }

        // עדכון טקסט כמות המטבעות על המסך
        tvCoins.text = "Coins: ${gameManager.coinsCollected}"
        tvDistance.text = "Dist: ${gameManager.distanceTraveled}m"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(gameLoopRunnable)
    }
}