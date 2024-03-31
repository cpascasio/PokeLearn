package com.mobdeve.s13.grp7.pokelearn

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobdeve.s13.grp7.pokelearn.databinding.FragmentBreakTimeBinding

class BreakTimeFragment : Fragment() {
    private lateinit var binding: FragmentBreakTimeBinding
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var startButton: Button
    private lateinit var webView: WebView // Declaration of webView

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
        startButton = view.findViewById(R.id.btnStart) // Find the Start button
        webView = view.findViewById(R.id.webView) // Find the WebView

        // Get break time duration and remaining time from arguments
        val breakDurationInMillis = arguments?.getLong(BREAK_DURATION_KEY) ?: 0
        val remainingTimeInMillis = arguments?.getLong(REMAINING_TIME_KEY) ?: breakDurationInMillis
        setTime(remainingTimeInMillis)

        // Set a click listener for the Start button
        startButton.setOnClickListener {
            startTimer() // Start the timer when the Start button is clicked
            startButton.isEnabled = false // Disable the button after starting the timer
        }

        // Load the YouTube video in the WebView
        val videoUrl = "https://www.youtube.com/embed/V2KCAfHjySQ?si=bhUQ6aaMCT7FT7bA"
        val videoHtml = "<iframe width=\"100%\" height=\"100%\" src=\"$videoUrl\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
        webView.loadData(videoHtml, "text/html", "utf-8")
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()

        return view
    }

    internal fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                redirectToRewardsPage()
            }
        }.start()
    }

    private fun setTime(milliseconds: Long) {
        startTimeInMillis = milliseconds
        timeLeftInMillis = milliseconds
        updateCountDownText() // Update UI with initial time
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


    private fun redirectToRewardsPage() {
        try {
            val rewardsIntent = Intent(requireContext(), RewardsActivity::class.java)
            startActivity(rewardsIntent)
            requireActivity().finish() // Optional: Finish the current activity if needed
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

}



