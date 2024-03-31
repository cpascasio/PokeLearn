package com.mobdeve.s13.grp7.pokelearn

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobdeve.s13.grp7.pokelearn.R
import com.mobdeve.s13.grp7.pokelearn.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
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
    private var isBreakTime: Boolean = false // Track if the current timer is for break time

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        timerText = binding.tvwMPTimer
        progressBar = binding.MPProgressBar
        setTimerButton = binding.btnMPSetTimer
        cancelButton = binding.btnMPCancel
        shakingPokeballImageView = binding.ivwMPShakingPokeball

        setTimerButton.setOnClickListener { showTimerSettingsDialog() }
        cancelButton.setOnClickListener { cancelTimer() }

        // Load the static Pokeball image initially
        Glide.with(this)
            .load(R.drawable.pokeball_static)
            .into(shakingPokeballImageView)

        return view
    }

    private fun showTimerSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_timer_settings, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        hoursEditText = dialogView.findViewById(R.id.hoursEditText)
        minutesEditText = dialogView.findViewById(R.id.minutesEditText)
        secondsEditText = dialogView.findViewById(R.id.secondsEditText)
        val startTimerButton = dialogView.findViewById<Button>(R.id.btn_StartTimer)

        startTimerButton.setOnClickListener {
            dialog.dismiss()
            // Start the timer based on the input duration
            val productivityHours = hoursEditText.text.toString().toIntOrNull() ?: 0
            val productivityMinutes = minutesEditText.text.toString().toIntOrNull() ?: 0
            val productivitySeconds = secondsEditText.text.toString().toIntOrNull() ?: 0

            // Get break time duration
            val breakHours = dialogView.findViewById<EditText>(R.id.breakHoursEditText).text.toString().toIntOrNull() ?: 0
            val breakMinutes = dialogView.findViewById<EditText>(R.id.breakMinutesEditText).text.toString().toIntOrNull() ?: 0
            val breakSeconds = dialogView.findViewById<EditText>(R.id.breakSecondsEditText).text.toString().toIntOrNull() ?: 0

            val productivityDurationInSeconds = productivityHours * 3600L + productivityMinutes * 60L + productivitySeconds
            val breakDurationInSeconds = breakHours * 3600L + breakMinutes * 60L + breakSeconds

            if (productivityDurationInSeconds > 0 && breakDurationInSeconds > 0) {
                // Re-show the progress bar
                progressBar.visibility = View.VISIBLE
                startTimer(productivityDurationInSeconds, breakDurationInSeconds)
            }
        }

        dialog.show()
    }

    private fun startTimer(productivityDurationInSeconds: Long, breakDurationInSeconds: Long) {
        val totalProductivityMillis = productivityDurationInSeconds * 1000L

        if (totalProductivityMillis <= 0) return

        setTime(totalProductivityMillis)

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        val delayMillis = 200L

        // Access the root layout of the activity and postDelayed on it
        view?.postDelayed({
            countDownTimer = object : CountDownTimer(totalProductivityMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateCountDownText()
                    updateProgressBar()
                }

                override fun onFinish() {
                    // After productivity duration finishes, start the break timer
                    startBreakTimer(breakDurationInSeconds)
                }
            }.start()

            isBreakTime = false // Set isBreakTime flag to false since it's productivity time
            isTimerSet = true
            // Load the shaking Pokeball image when the timer is set
            Glide.with(this)
                .asGif()
                .load(R.drawable.pokeball_shaking)
                .into(shakingPokeballImageView)
        }, delayMillis)
    }

    private fun startBreakTimer(durationInSeconds: Long) {
        val totalMillis = durationInSeconds * 1000L
        if (totalMillis <= 0) return

        setTime(totalMillis)

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        val delayMillis = 200L

        // Access the root layout of the activity and postDelayed on it
        view?.postDelayed({
            // Redirect to the BreakFragment
            val breakTimeFragment = BreakTimeFragment.newInstance(totalMillis, totalMillis)
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, breakTimeFragment)
                commit()
            }

            // Start the break timer
            breakTimeFragment.startTimer()
        }, delayMillis)

        isBreakTime = true // Set isBreakTime flag to true since it's break time
        isTimerSet = true
        // Load the shaking Pokeball image when the timer is set
        Glide.with(this)
            .asGif()
            .load(R.drawable.pokeball_shaking)
            .into(shakingPokeballImageView)
    }


    private fun cancelTimer() {
        // Cancel the current timer and reset UI
        countDownTimer?.cancel()
        isTimerSet = false

        // Switch Pokeball image to static
        Glide.with(this)
            .load(R.drawable.pokeball_static)
            .into(shakingPokeballImageView)
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
        val elapsedTimeInMillis = startTimeInMillis - timeLeftInMillis
        val progress = ((elapsedTimeInMillis * 100) / startTimeInMillis).toInt()
        val filledProgress = 100 - progress  // Reverse progress to make it clockwise

        progressBar.progress = filledProgress

        // Ensure the progress bar reaches 100% when the timer finishes
        if (progress == 100) {
            progressBar.progress = 0 // Reset progress to 0 when the timer finishes
        }
    }
}

    /**
     * Starts the Pomodoro timer with a productivity duration of 25 minutes and a break duration of 5 minutes.
     * After 3 repetitions, the break duration is set to 30 minutes.
     */
//    private fun startPomodoroTimer() {
//        val productivityDurationInSeconds = 1 * 60L // 25 minutes
//        var breakDurationInSeconds = 2 * 60L // 5 minutes
//
//        // After 3 repetitions, set the break duration to 30 minutes
//        if (repetitionCount == 3) {
//            breakDurationInSeconds = 3 * 60L // 30 minutes
//            repetitionCount = 0 // Reset the counter
//        }
//
//        // Start the timer with the Pomodoro durations
//        startTimer(productivityDurationInSeconds, breakDurationInSeconds)
//
//        // Increment the counter
//        repetitionCount++
//    }
//}
