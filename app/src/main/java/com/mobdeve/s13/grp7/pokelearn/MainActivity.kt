package com.mobdeve.s13.grp7.pokelearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mobdeve.s13.grp7.pokelearn.databinding.ActivityMainBinding
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable

class MainActivity : ComponentActivity() {
    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var setTimerButton: Button
    private lateinit var cancelButton: Button
    private lateinit var hoursEditText: EditText
    private lateinit var minutesEditText: EditText
    private lateinit var secondsEditText: EditText
    private lateinit var shakingPokeballImageView: ImageView

    private var countDownTimer: CountDownTimer? = null
    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0
    private var isTimerSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        timerText = findViewById(R.id.tvwMP_Timer)
        progressBar = findViewById(R.id.MP_ProgressBar)
        setTimerButton = findViewById(R.id.btnMP_SetTimer)
        cancelButton = findViewById(R.id.btnMP_Cancel)
        shakingPokeballImageView = findViewById(R.id.ivwMPShakingPokeball)

        setTimerButton.setOnClickListener { showTimerSettingsDialog() }
        cancelButton.setOnClickListener { cancelTimer() }

        // Load the static Pokeball image initially
        Glide.with(this)
            .load(R.drawable.pokeball_static)
            .into(shakingPokeballImageView)
    }

    private fun showTimerSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_timer_settings, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        hoursEditText = dialogView.findViewById(R.id.hoursEditText)
        minutesEditText = dialogView.findViewById(R.id.minutesEditText)
        secondsEditText = dialogView.findViewById(R.id.secondsEditText)
        val startTimerButton = dialogView.findViewById<Button>(R.id.startTimerButton)

        startTimerButton.setOnClickListener {
            dialog.dismiss()
            startTimer()
        }

        dialog.show()
    }

    private fun startTimer() {
        val hours = hoursEditText.text.toString().toIntOrNull() ?: 0
        val minutes = minutesEditText.text.toString().toIntOrNull() ?: 0
        val seconds = secondsEditText.text.toString().toIntOrNull() ?: 0

        val totalMillis = (hours.toLong() * 3600L + minutes.toLong() * 60L + seconds.toLong()) * 1000L
        if (totalMillis <= 0) return

        setTime(totalMillis)

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
                timerText.text = "00:00:00"
                progressBar.progress = 0
                isTimerSet = false
                // Switch back to the static Pokeball image
                Glide.with(this@MainActivity)
                    .load(R.drawable.pokeball_static)
                    .into(shakingPokeballImageView)
                // Ensure the progress bar is visible
                progressBar.visibility = View.VISIBLE
            }
        }.start()

        isTimerSet = true
        // Load the shaking Pokeball image when the timer is set
        Glide.with(this)
            .asGif()
            .load(R.drawable.pokeball_shaking)
            .into(shakingPokeballImageView)
    }

    private fun cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
            // Reset the timer text without changing the progress bar
            timerText.text = "00:00:00"
            isTimerSet = false
            // Switch back to the static Pokeball image
            Glide.with(this)
                .load(R.drawable.pokeball_static)
                .into(shakingPokeballImageView)
            // Ensure the progress bar is visible
            progressBar.visibility = View.VISIBLE
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
