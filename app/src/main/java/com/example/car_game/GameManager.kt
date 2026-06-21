package com.example.obstacle_game // שנה לשם ה-package האמיתי שלך

class GameManager(private val rows: Int = 16, private val cols: Int = 5) {

    var lives: Int = 3
        private set // קריאה חופשית מבחוץ, שינוי רק מתוך המחלקה

    var carLane: Int = 2 // 0 = שמאל, 1 = אמצע, 2 = ימין
        private set

    var coinsCollected: Int = 0
        private set

    // מטריצה בוליאנית: true מייצג מכשול, false מייצג שטח ריק
    val obstacleMatrix: Array<IntArray> = Array(rows) { IntArray(cols) { 0 } }

    var distanceTraveled: Int = 0
        private set

    fun moveCarLeft() {
        if (carLane > 0) carLane--
    }

    fun moveCarRight() {
        if (carLane < cols - 1) carLane++
    }

    fun advanceObstacles() {
        distanceTraveled += 1
        // 1. קידום המכשולים הקיימים למטה
        for (i in rows - 1 downTo 1) {
            for (j in 0 until cols) {
                obstacleMatrix[i][j] = obstacleMatrix[i - 1][j]
            }
        }

        // 2. ניקוי השורה העליונה
        for (j in 0 until cols) {
            obstacleMatrix[0][j] = 0
        }

        // 3. בדיקה אם השורה שמתחתיה (שורה 1) מכילה מכשול
        var isRow1Empty = true
        for (j in 0 until cols) {
            if (obstacleMatrix[1][j] > 0) {
                isRow1Empty = false
                break
            }
        }

        // 4. ייצור מכשול חדש: רק אם השורה הקודמת ריקה (כדי ליצור מרווח נשימה), ובסיכוי של 50%
        if (isRow1Empty) {
            val rand = Math.random()
            val randomCol = (0 until cols).random()
            if (rand > 0.6) {
                obstacleMatrix[0][randomCol] = 1 // ייצור מכשול
            } else if (rand > 0.4) {
                obstacleMatrix[0][randomCol] = 2 // ייצור מטבע
            }
        }
    }

    fun checkCollision(): Int {
        val currentItem = obstacleMatrix[rows - 1][carLane]

        if (currentItem == 1) { // התנגשות במכשול
            lives--
            obstacleMatrix[rows - 1][carLane] = 0
            return 1
        } else if (currentItem == 2) { // איסוף מטבע
            coinsCollected++
            obstacleMatrix[rows - 1][carLane] = 0
            return 2
        }
        return 0
    }

    fun isGameOver(): Boolean {
        return lives <= 0
    }

    fun resetGame() {
        distanceTraveled = 0
        lives = 3
        carLane = 2
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                obstacleMatrix[i][j] = 0
            }
        }
    }
}