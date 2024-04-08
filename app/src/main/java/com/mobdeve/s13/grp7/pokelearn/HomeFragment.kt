package com.mobdeve.s13.grp7.pokelearn

import SharedViewModel
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobdeve.s13.grp7.pokelearn.R
import com.mobdeve.s13.grp7.pokelearn.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
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

    private var cycleCounter = 0

    private var isUserInApp: Boolean = false
    private var appInBackground: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    //    private val inactivityThreshold: Long = 2 * 60 * 1000 // 2 minutes in milliseconds
    private val inactivityThreshold: Long = 5 * 1000 // 5 secs for trial

    private val logRunnable = Runnable {
        if (!isUserInApp && appInBackground && isTimerSet) {
            // User has left the app for over 2 minutes
            sendNotification()
            Log.d("MainActivity", "User has left the app for over 2 minutes")
        }
    }

    private fun sendNotification() {
        Log.d("MainActivity", "Notification sent")
        val channelId = "default_channel_id"
        val channelName = "Default"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
            Log.d("MainActivity", "version is good")
        }
        Log.d("MainActivity", "before notif builder")
        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.pokelearn_logo)
            .setContentTitle("Wild Distraction Appeared!")
            .setContentText("It seems like you’ve been distracted. Your Pokemon might escape!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        Log.d("MainActivity", "Notif middle")
        val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity", "returned")
            return
        }
        notificationManager.notify(1, notificationBuilder.build())
        Log.d("MainActivity", "Done Sending")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize the SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

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


        // Get productivity time and break duration from arguments
        val productivityTimeInMillis = arguments?.getLong(PRODUCTIVITY_TIME_KEY)
        val breakDurationInMillis = arguments?.getLong(BREAK_DURATION_KEY)

        // Only call setProductivityTime and setupStartButton if productivityTimeInMillis and breakDurationInMillis are not null
        if (productivityTimeInMillis != null && breakDurationInMillis != null) {
            setProductivityTime(productivityTimeInMillis)
            setupStartButton(productivityTimeInMillis, breakDurationInMillis)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    private fun showTimerSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_timer_settings, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        hoursEditText = dialogView.findViewById(R.id.hoursEditText)
        minutesEditText = dialogView.findViewById(R.id.minutesEditText)
        secondsEditText = dialogView.findViewById(R.id.secondsEditText)
        val startTimerButton = dialogView.findViewById<Button>(R.id.btn_StartTimer)

        // Set default values for productivity duration, short break, and long break
        val defaultProductivityTime = 25 // Default productivity time in minutes
        val defaultShortBreak = 5 // Default short break time in minutes
        val defaultLongBreak = 10 // Default long break time in minutes

        hoursEditText.setText("0")
        minutesEditText.setText(defaultProductivityTime.toString()) // Set default productivity duration
        secondsEditText.setText("0")

        // Set default short break and long break durations in the dialog
        dialogView.findViewById<EditText>(R.id.breakMinutesEditText).setText(defaultShortBreak.toString())
        dialogView.findViewById<EditText>(R.id.breakSecondsEditText).setText("0")
        dialogView.findViewById<EditText>(R.id.breakHoursEditText).setText("0")
        dialogView.findViewById<EditText>(R.id.longbreakMinutesEditText).setText(defaultLongBreak.toString())
        dialogView.findViewById<EditText>(R.id.longbreakSecondsEditText).setText("0")
        dialogView.findViewById<EditText>(R.id.longbreakHoursEditText).setText("0")


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

//            val productivityDurationInSeconds = productivityHours * 3600L + productivityMinutes * 60L + productivitySeconds
//            val breakDurationInSeconds = breakHours * 3600L + breakMinutes * 60L + breakSeconds

//            if (productivityDurationInSeconds > 0 && breakDurationInSeconds > 0) {
//                // Re-show the progress bar
//                progressBar.visibility = View.VISIBLE
//                // set cancel button to clickable
//                cancelButton.isEnabled = true
//                startTimerButton.isEnabled = false
//                startTimer(productivityDurationInSeconds, breakDurationInSeconds)
//            }

            // Validate user input
            if (isValidInput(productivityHours, productivityMinutes, productivitySeconds) &&
                isValidInput(breakHours, breakMinutes, breakSeconds)) {
                val productivityDurationInSeconds = productivityHours * 3600L + productivityMinutes * 60L + productivitySeconds
                val breakDurationInSeconds = breakHours * 3600L + breakMinutes * 60L + breakSeconds

                // Start the timer only if both productivity and break durations are greater than 0
                if (productivityDurationInSeconds > 0 && breakDurationInSeconds > 0) {
                    // Re-show the progress bar
                    progressBar.visibility = View.VISIBLE
                    // set cancel button to clickable
                    cancelButton.isEnabled = true
                    startTimerButton.isEnabled = true

                    val totalProductivityMillis = productivityDurationInSeconds * 1000L
                    setTime(totalProductivityMillis)

                    //call setupstartbutton
                    setupStartButton(productivityDurationInSeconds * 1000L, breakDurationInSeconds * 1000L)


                    //startTimer(productivityDurationInSeconds, breakDurationInSeconds)
                }
            }else {
                // Show a toast message for invalid input
                Toast.makeText(requireContext(), "Invalid input. Please enter valid values.", Toast.LENGTH_SHORT).show()
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
                    //start BreakTimeFragment and pass the break duration
                    startBreakTimer(breakDurationInSeconds, productivityDurationInSeconds)
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

    private fun startBreakTimer(durationInSeconds: Long, productivityDurationInSeconds: Long) {
        val totalMillis = durationInSeconds * 1000L
        val totalProductivityMillis = productivityDurationInSeconds * 1000L
        if (totalMillis <= 0) return

        setTime(totalMillis)

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        val delayMillis = 200L

        // Access the root layout of the activity and postDelayed on it
        view?.postDelayed({
            // Redirect to the BreakFragment
            val breakTimeFragment = BreakTimeFragment.newInstance(totalMillis, totalMillis, totalProductivityMillis)
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, breakTimeFragment)
                commit()
            }

            // Start the break timer
            //breakTimeFragment.startTimer()
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
        countDownTimer = null
        startTimeInMillis = 0
        timeLeftInMillis = 0
        isBreakTime = false
        updateCountDownText()

        // Set startTimer button to not clickable
        binding.btnMPStart.isEnabled = false

        // set cancel button to not clickable
        cancelButton.isEnabled = false

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

    fun setProductivityTime(productivityTimeInMillis: Long) {
        setTime(productivityTimeInMillis)
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

    fun setupStartButton(productivityTimeInMillis: Long, breakDurationInMillis: Long) {
        val productivityTimeInSecondsfunc = productivityTimeInMillis / 1000L

        val breakDurationInMillisfunc = breakDurationInMillis / 1000L

        binding.btnMPStart.apply {
            isEnabled = true
            setOnClickListener {
                startTimer(productivityTimeInSecondsfunc, breakDurationInMillisfunc)
                isEnabled = false
            }
        }
    }
    companion object {
        const val PRODUCTIVITY_TIME_KEY = "productivity_time"
        const val BREAK_DURATION_KEY = "break_duration"
    }

    override fun onStart() {
        super.onStart()
        // User is in the app
        isUserInApp = true
        Log.d("MainActivity", "onStart() called")
        cancelLogTask() // Cancel any previously scheduled log task
        appInBackground = false
    }

    override fun onStop() {
        super.onStop()
        // User has left the app
        isUserInApp = false
        Log.d("MainActivity", "onStop() called")
        scheduleLogTask() // Schedule a log task if the app is going into the background
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelLogTask() // Cancel any scheduled log task when the activity is destroyed
    }

    private fun scheduleLogTask() {
        appInBackground = true
        handler.postDelayed(logRunnable, inactivityThreshold)
    }

    private fun cancelLogTask() {
        handler.removeCallbacks(logRunnable)
    }

    private fun isValidInput(hours: Int, minutes: Int, seconds: Int): Boolean {
        return hours in 0..24 && minutes in 0..59 && seconds in 0..59
    }




}