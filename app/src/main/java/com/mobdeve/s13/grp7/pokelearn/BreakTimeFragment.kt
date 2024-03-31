package com.mobdeve.s13.grp7.pokelearn

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobdeve.s13.grp7.pokelearn.databinding.FragmentBreakTimeBinding

class BreakTimeFragment : Fragment() {
    private lateinit var binding: FragmentBreakTimeBinding
    private lateinit var countDownTimer: CountDownTimer

    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBreakTimeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize UI elements
        val timerText = binding.tvwBreakTimer

        // Get break time duration and remaining time from arguments
        val breakDurationInMillis = arguments?.getLong(BREAK_DURATION_KEY) ?: 0
        val remainingTimeInMillis = arguments?.getLong(REMAINING_TIME_KEY) ?: breakDurationInMillis
        setTime(remainingTimeInMillis)

        // Start the break timer with remaining time
        startTimer()

        return view
    }

    internal fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                redirectToHomeFragment()
            }
        }.start()
    }

    private fun setTime(milliseconds: Long) {
        startTimeInMillis = milliseconds
        timeLeftInMillis = milliseconds
    }

    private fun updateCountDownText() {
        val seconds = (timeLeftInMillis / 1000) % 60
        val minutes = ((timeLeftInMillis / 1000) / 60) % 60
        val hours = ((timeLeftInMillis / 1000) / 3600)

        val timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        binding.tvwBreakTimer.text = timeLeftFormatted
    }

    private fun redirectToHomeFragment() {
        try {
            val homeFragment = HomeFragment()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, homeFragment)
                commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val BREAK_DURATION_KEY = "break_duration"
        const val REMAINING_TIME_KEY = "remaining_time"

        fun newInstance(breakDurationInMillis: Long, remainingTimeInMillis: Long): BreakTimeFragment {
            val fragment = BreakTimeFragment()
            val args = Bundle()
            args.putLong(BREAK_DURATION_KEY, breakDurationInMillis)
            args.putLong(REMAINING_TIME_KEY, remainingTimeInMillis)
            fragment.arguments = args
            return fragment
        }
    }
}