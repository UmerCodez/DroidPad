package com.github.umer0586.droidpad.data.util.vibrator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class VibratorUtilImp(context: Context) : VibratorUtil {

    @SuppressLint("ServiceCast")
    // For Android 12+
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {  // Fallback for older versions
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun vibrate() {

        if (vibrator.hasVibrator()) {
            // Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        SHORT_VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                // Pre-Android 8.0
                @Suppress("DEPRECATION")
                vibrator.vibrate(SHORT_VIBRATION_DURATION)
            }
        }

    }

    companion object {
        private const val SHORT_VIBRATION_DURATION = 10L // milliseconds
    }
}