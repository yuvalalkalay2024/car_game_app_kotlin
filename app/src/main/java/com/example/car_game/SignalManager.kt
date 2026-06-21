package com.example.car_game // שנה לשם ה-package האמיתי שלך

import android.content.Context
import android.media.MediaPlayer // ייבוא מחלקת המדיה שנוסף
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

class SignalManager(private val context: Context) {

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun vibrate() {
        val v: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // רטט למשך 500 מילישניות בעוצמה ברירת מחדל
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(500)
        }
    }

    // הפונקציה החדשה שמנגנת את הסאונד
    fun playSound() {
        // R.raw.crash - ודא שיש לך קובץ בשם crash.mp3 בתיקיית res/raw
        val mediaPlayer = MediaPlayer.create(context, R.raw.crash)
        mediaPlayer.start()

        // משחרר את הזיכרון כשהסאונד מסיים להתנגן
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
}