package com.mobdeve.s13.grp7.pokelearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
import com.mobdeve.s13.grp7.pokelearn.ui.theme.PokeLearnTheme
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.os.CountDownTimer

class MainActivity : ComponentActivity() {
    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var setTimerButton: Button
    private lateinit var cancelButton: Button
    private lateinit var customDurationEditText: EditText

    private var countDownTimer: CountDownTimer? = null
    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        timerText = findViewById(R.id.timerText)
        progressBar = findViewById(R.id.progressBar)
        setTimerButton = findViewById(R.id.setTimerButton)
        cancelButton = findViewById(R.id.cancelButton)

        setTimerButton.setOnClickListener { showTimerSettingsDialog() }
        cancelButton.setOnClickListener { cancelTimer() }
    }

    private fun showTimerSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_timer_settings, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        customDurationEditText = dialogView.findViewById(R.id.customDurationEditText)
        val startTimerButton = dialogView.findViewById<Button>(R.id.startTimerButton)

        startTimerButton.setOnClickListener {
            dialog.dismiss()
            startTimer()
        }

        dialog.show()
    }

    private fun startTimer() {
        val input = customDurationEditText.text.toString()
        if (input.isEmpty()) return

        val millisInput = input.toLong() * 1000 * 60 // Convert minutes to milliseconds
        setTime(millisInput)

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
                updateProgressBar()
            }

            override fun onFinish() {
                timerText.text = "00:00"
                progressBar.progress = 0
            }
        }.start()
    }

    private fun cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
            // Reset the timer text without changing the progress bar
            timerText.text = "00:00"
        }
    }

    private fun setTime(milliseconds: Long) {
        startTimeInMillis = milliseconds
        timeLeftInMillis = milliseconds
        updateCountDownText()
    }

    private fun updateCountDownText() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        timerText.text = timeLeftFormatted
    }

    private fun updateProgressBar() {
        val progress = ((timeLeftInMillis * 100) / startTimeInMillis).toInt()
        progressBar.progress = progress
    }
}
