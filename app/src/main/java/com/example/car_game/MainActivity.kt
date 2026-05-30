package com.example.car_game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.obstacle_game.GameManager
import com.example.obstacle_game.SignalManager

class MainActivity : AppCompatActivity() {

    private lateinit var gameManager: GameManager
    private lateinit var signalManager: SignalManager

    private lateinit var obstacleViews: Array<Array<ImageView>>
    private lateinit var carViews: Array<ImageView> // חזרנו לשורה אחת של רכבים
    private lateinit var heartViews: Array<ImageView>

    private val handler = Handler(Looper.getMainLooper())
    private val DELAY_MILLIS = 1000L

    private val gameLoopRunnable = object : Runnable {
        override fun run() {
            gameManager.advanceObstacles()
            checkCrashAndUpdateUI()
            handler.postDelayed(this, DELAY_MILLIS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameManager = GameManager(rows = 6, cols = 3)
        signalManager = SignalManager(this)

        initViews()
        setupButtons()

        updateUI()
        handler.postDelayed(gameLoopRunnable, DELAY_MILLIS)
    }

    private fun initViews() {
        // טעינת מכשולים
        obstacleViews = Array(6) { i ->
            Array(3) { j ->
                val resID = resources.getIdentifier("obstacle_${i}_${j}", "id", packageName)
                val imageView = findViewById<ImageView>(resID)
                // שים לב: ודא שיש לך תמונת מכשול בשם 'obstacle' בתיקיית drawable
                Glide.with(this).load(R.drawable.obstacle).into(imageView)
                imageView
            }
        }

        // טעינת רכבים - מתאים לשמות שיש לך בתיקייה
        carViews = Array(3) { j ->
            val resID = resources.getIdentifier("car_$j", "id", packageName)
            val imageView = findViewById<ImageView>(resID)
            Glide.with(this).load(R.drawable.car).into(imageView) // שיניתי ל-car
            imageView
        }

        // טעינת לבבות - מתאים לשמות שיש לך בתיקייה
        heartViews = arrayOf(
            findViewById(R.id.heart_1),
            findViewById(R.id.heart_2),
            findViewById(R.id.heart_3)
        )
        for (heart in heartViews) {
            Glide.with(this).load(R.drawable.heart).into(heart) // שיניתי ל-heart
        }
    }

    private fun setupButtons() {
        findViewById<ImageButton>(R.id.btn_left).setOnClickListener { gameManager.moveCarLeft(); checkCrashAndUpdateUI() }
        findViewById<ImageButton>(R.id.btn_right).setOnClickListener { gameManager.moveCarRight(); checkCrashAndUpdateUI() }
    }

    private fun checkCrashAndUpdateUI() {
        if (gameManager.checkCollision()) {
            signalManager.toast("Crash!")
            signalManager.vibrate()

            if (gameManager.isGameOver()) {
                signalManager.toast("Game Over! Restarting...")
                gameManager.resetGame()
            }
        }
        updateUI()
    }

    private fun updateUI() {
        for (i in 0 until 6) {
            for (j in 0 until 3) {
                obstacleViews[i][j].visibility = if (gameManager.obstacleMatrix[i][j]) View.VISIBLE else View.INVISIBLE
            }
        }

        for (j in 0 until 3) {
            carViews[j].visibility = if (j == gameManager.carLane) View.VISIBLE else View.INVISIBLE
        }

        for (i in 0 until 3) {
            heartViews[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(gameLoopRunnable)
    }
}