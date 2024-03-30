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
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

class HomeActivity : ComponentActivity() {
    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var setTimerButton: Button
    private lateinit var cancelButton: Button
    private lateinit var productivityHoursEditText: EditText
    private lateinit var productivityMinutesEditText: EditText
    private lateinit var productivitySecondsEditText: EditText
    private lateinit var breakHoursEditText: EditText
    private lateinit var breakMinutesEditText: EditText
    private lateinit var breakSecondsEditText: EditText
    private lateinit var repetitionsEditText: EditText
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

        productivityHoursEditText = dialogView.findViewById(R.id.productivity_hours)
        productivityMinutesEditText = dialogView.findViewById(R.id.productivity_minutes)
        productivitySecondsEditText = dialogView.findViewById(R.id.productivity_seconds)
        breakHoursEditText = dialogView.findViewById(R.id.break_hours)
        breakMinutesEditText = dialogView.findViewById(R.id.break_minutes)
        breakSecondsEditText = dialogView.findViewById(R.id.break_seconds)
        repetitionsEditText = dialogView.findViewById(R.id.repetitionsEditText)
        val startTimerButton = dialogView.findViewById<Button>(R.id.startTimerButton)

        startTimerButton.setOnClickListener {
            dialog.dismiss()
            val repetitions = repetitionsEditText.text.toString().toIntOrNull() ?: 1
            startTimer(repetitions)
        }

        dialog.show()
    }

    private fun startTimer(repetitions: Int) {
        val productivityHours = productivityHoursEditText.text.toString().toIntOrNull() ?: 0
        val productivityMinutes = productivityMinutesEditText.text.toString().toIntOrNull() ?: 0
        val productivitySeconds = productivitySecondsEditText.text.toString().toIntOrNull() ?: 0
        val breakHours = breakHoursEditText.text.toString().toIntOrNull() ?: 0
        val breakMinutes = breakMinutesEditText.text.toString().toIntOrNull() ?: 0
        val breakSeconds = breakSecondsEditText.text.toString().toIntOrNull() ?: 0

        val productivityMillis = (productivityHours.toLong() * 3600L + productivityMinutes.toLong() * 60L + productivitySeconds.toLong()) * 1000L
        val breakMillis = (breakHours.toLong() * 3600L + breakMinutes.toLong() * 60L + breakSeconds.toLong()) * 1000L

        if (productivityMillis <= 0 || breakMillis <= 0) return

        setTime(productivityMillis)

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        var repetitionsLeft = repetitions

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished

                // Check if it's productivity time or break time
                if (isTimerSet) {
                    updateCountDownText()
                    updateProgressBar()
                } else {
                    // Update break text
                    val breakHours = (timeLeftInMillis / 1000) / 3600
                    val breakMinutes = ((timeLeftInMillis / 1000) % 3600) / 60
                    val breakSeconds = (timeLeftInMillis / 1000) % 60

                    val breakTimeLeftFormatted = String.format("%02d:%02d:%02d", breakHours, breakMinutes, breakSeconds)
                    timerText.text = breakTimeLeftFormatted

                    // Update break progress bar
                    val breakProgress = ((timeLeftInMillis * 100) / breakMillis).toInt()
                    progressBar.progress = breakProgress
                }
            }

            override fun onFinish() {
                if (isTimerSet) {
                    // Start break timer
                    isTimerSet = false
                    setTime(breakMillis)
                } else {
                    // Reset timer and progress bar after break
                    timerText.text = "00:00:00"
                    progressBar.progress = 0
                    isTimerSet = true
                    repetitionsLeft--
                }

                if (repetitionsLeft > 0) {
                    // Start next productivity timer
                    startTimer(repetitionsLeft)
                } else {
                    // Stop the timer after all repetitions are done
                    countDownTimer?.cancel()
                    // Switch back to the static Pokeball image after all repetitions are done
                    Glide.with(this@HomeActivity)
                        .load(R.drawable.pokeball_static)
                        .into(shakingPokeballImageView)
                    // Ensure the progress bar is visible
                    progressBar.visibility = View.VISIBLE
                }
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
        Log.d("TimerDebug", "cancelTimer() called")
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
