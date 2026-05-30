package com.example.obstacle_game // שנה לשם ה-package האמיתי שלך

class GameManager(private val rows: Int = 6, private val cols: Int = 3) {

    var lives: Int = 3
        private set // קריאה חופשית מבחוץ, שינוי רק מתוך המחלקה

    var carLane: Int = 1 // 0 = שמאל, 1 = אמצע, 2 = ימין
        private set

    // מטריצה בוליאנית: true מייצג מכשול, false מייצג שטח ריק
    val obstacleMatrix: Array<BooleanArray> = Array(rows) { BooleanArray(cols) { false } }

    fun moveCarLeft() {
        if (carLane > 0) carLane--
    }

    fun moveCarRight() {
        if (carLane < cols - 1) carLane++
    }

    fun advanceObstacles() {
        // 1. קידום המכשולים הקיימים למטה
        for (i in rows - 1 downTo 1) {
            for (j in 0 until cols) {
                obstacleMatrix[i][j] = obstacleMatrix[i - 1][j]
            }
        }

        // 2. ניקוי השורה העליונה
        for (j in 0 until cols) {
            obstacleMatrix[0][j] = false
        }

        // 3. בדיקה אם השורה שמתחתיה (שורה 1) מכילה מכשול
        var isRow1Empty = true
        for (j in 0 until cols) {
            if (obstacleMatrix[1][j]) {
                isRow1Empty = false
                break
            }
        }

        // 4. ייצור מכשול חדש: רק אם השורה הקודמת ריקה (כדי ליצור מרווח נשימה), ובסיכוי של 50%
        if (isRow1Empty && Math.random() > 0.5) {
            val randomCol = (0 until cols).random()
            obstacleMatrix[0][randomCol] = true
        }
    }

    fun checkCollision(): Boolean {
        // התנגשות קורית אם יש מכשול בשורה התחתונה ביותר באותו נתיב של המכונית
        if (obstacleMatrix[rows - 1][carLane]) {
            lives--
            obstacleMatrix[rows - 1][carLane] = false // מעלימים את המכשול שלא ניפסל שוב ושוב
            return true
        }
        return false
    }

    fun isGameOver(): Boolean {
        return lives <= 0
    }

    fun resetGame() {
        lives = 3
        carLane = 1
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                obstacleMatrix[i][j] = false
            }
        }
    }
}