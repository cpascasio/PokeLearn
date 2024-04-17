package com.mobdeve.s13.grp7.pokelearn

import SharedViewModel
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.mobdeve.s13.grp7.pokelearn.database.UserProfileDatabaseHelper
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

            cancelTimer() // Cancel the timer when the user leaves the app
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
        cancelButton.setOnClickListener { cancelTimer()  }

        //log shared preferences
        // Log shared preferences
        val sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", "No UID found")
        val email = sharedPreferences.getString("email", "No email found")

        Log.d("SharedPreferences", "UID: $uid")
        Log.d("SharedPreferences", "Email: $email")


        // Load the static Pokeball image initially
        Glide.with(this)
            .load(R.drawable.pokeball_static)
            .into(shakingPokeballImageView)


        // Get productivity time and break duration from arguments
        val productivityTimeInMillis = arguments?.getLong(PRODUCTIVITY_TIME_KEY)
        val breakDurationInMillis = arguments?.getLong(BREAK_DURATION_KEY)
        val longbreakDurationInMillis = arguments?.getLong(LONGBREAK_DURATION_KEY)

        // Only call setProductivityTime and setupStartButton if productivityTimeInMillis and breakDurationInMillis are not null
        if (productivityTimeInMillis != null && breakDurationInMillis != null && longbreakDurationInMillis != null) {
            setProductivityTime(productivityTimeInMillis)
            setupStartButton(productivityTimeInMillis, breakDurationInMillis, longbreakDurationInMillis)
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

        // Other view initialization code...

        hoursEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("h")) {
                    hoursEditText.setText("${s}h")
                    hoursEditText.setSelection(hoursEditText.text.length - 1) // position cursor before 'h'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        minutesEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("m")) {
                    minutesEditText.setText("${s}m")
                    minutesEditText.setSelection(minutesEditText.text.length - 1) // position cursor before 'm'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        secondsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("s")) {
                    secondsEditText.setText("${s}s")
                    secondsEditText.setSelection(secondsEditText.text.length - 1) // position cursor before 's'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        val breakHoursEditText = dialogView.findViewById<EditText>(R.id.breakHoursEditText)
        val breakMinutesEditText = dialogView.findViewById<EditText>(R.id.breakMinutesEditText)
        val breakSecondsEditText = dialogView.findViewById<EditText>(R.id.breakSecondsEditText)

        val longbreakHoursEditText = dialogView.findViewById<EditText>(R.id.longbreakHoursEditText)
        val longbreakMinutesEditText = dialogView.findViewById<EditText>(R.id.longbreakMinutesEditText)
        val longbreakSecondsEditText = dialogView.findViewById<EditText>(R.id.longbreakSecondsEditText)

        breakHoursEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("h")) {
                    breakHoursEditText.setText("${s}h")
                    breakHoursEditText.setSelection(breakHoursEditText.text.length - 1) // position cursor before 'h'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        breakMinutesEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("m")) {
                    breakMinutesEditText.setText("${s}m")
                    breakMinutesEditText.setSelection(breakMinutesEditText.text.length - 1) // position cursor before 'm'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        breakSecondsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("s")) {
                    breakSecondsEditText.setText("${s}s")
                    breakSecondsEditText.setSelection(breakSecondsEditText.text.length - 1) // position cursor before 's'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        longbreakHoursEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("h")) {
                    longbreakHoursEditText.setText("${s}h")
                    longbreakHoursEditText.setSelection(longbreakHoursEditText.text.length - 1) // position cursor before 'h'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        longbreakMinutesEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("m")) {
                    longbreakMinutesEditText.setText("${s}m")
                    longbreakMinutesEditText.setSelection(longbreakMinutesEditText.text.length - 1) // position cursor before 'm'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        longbreakSecondsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty() && !s.toString().endsWith("s")) {
                    longbreakSecondsEditText.setText("${s}s")
                    longbreakSecondsEditText.setSelection(longbreakSecondsEditText.text.length - 1) // position cursor before 's'
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })



        // Set default values for productivity duration, short break, and long break
        val defaultProductivityTime = 25 // Default productivity time in minutes
        val defaultShortBreak = 5 // Default short break time in minutes
        val defaultLongBreak = 10 // Default long break time in minutes

        hoursEditText.setText("")
        minutesEditText.setText(defaultProductivityTime.toString()) // Set default productivity duration
        secondsEditText.setText("")

        // Set default short break and long break durations in the dialog
        dialogView.findViewById<EditText>(R.id.breakMinutesEditText).setText(defaultShortBreak.toString())
        dialogView.findViewById<EditText>(R.id.breakSecondsEditText).setText("")
        dialogView.findViewById<EditText>(R.id.breakHoursEditText).setText("")
        dialogView.findViewById<EditText>(R.id.longbreakMinutesEditText).setText(defaultLongBreak.toString())
        dialogView.findViewById<EditText>(R.id.longbreakSecondsEditText).setText("")
        dialogView.findViewById<EditText>(R.id.longbreakHoursEditText).setText("")

        val clearDefaultButton = dialogView.findViewById<Button>(R.id.btn_ClearDefault)
        clearDefaultButton.setOnClickListener {
            // Clear default values
            hoursEditText.setText("")
            minutesEditText.setText("")
            secondsEditText.setText("")
            dialogView.findViewById<EditText>(R.id.breakHoursEditText).setText("")
            dialogView.findViewById<EditText>(R.id.breakMinutesEditText).setText("")
            dialogView.findViewById<EditText>(R.id.breakSecondsEditText).setText("")
            dialogView.findViewById<EditText>(R.id.longbreakHoursEditText).setText("")
            dialogView.findViewById<EditText>(R.id.longbreakMinutesEditText).setText("")
            dialogView.findViewById<EditText>(R.id.longbreakSecondsEditText).setText("")
        }

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

            // Get long break time duration
            val longbreakHours = dialogView.findViewById<EditText>(R.id.longbreakHoursEditText).text.toString().toIntOrNull() ?: 0
            val longbreakMinutes = dialogView.findViewById<EditText>(R.id.longbreakMinutesEditText).text.toString().toIntOrNull() ?: 0
            val longbreakSeconds = dialogView.findViewById<EditText>(R.id.longbreakSecondsEditText).text.toString().toIntOrNull() ?: 0

            // Validate user input and start the timer
            if (isValidInput(productivityHours, productivityMinutes, productivitySeconds) &&
                isValidInput(breakHours, breakMinutes, breakSeconds) &&
                isValidInput(longbreakHours, longbreakMinutes, longbreakSeconds)) {
                val productivityDurationInSeconds = productivityHours * 3600L + productivityMinutes * 60L + productivitySeconds
                val breakDurationInSeconds = breakHours * 3600L + breakMinutes * 60L + breakSeconds
                val longbreakDurationInSeconds = longbreakHours * 3600L + longbreakMinutes * 60L + longbreakSeconds

                if (productivityDurationInSeconds > 0 && breakDurationInSeconds > 0 && longbreakDurationInSeconds > 0) {
                    progressBar.visibility = View.VISIBLE
                    cancelButton.isEnabled = true
                    startTimerButton.isEnabled = true

                    val totalProductivityMillis = productivityDurationInSeconds * 1000L
                    setTime(totalProductivityMillis)

                    setupStartButton(totalProductivityMillis, breakDurationInSeconds * 1000L, longbreakDurationInSeconds * 1000L)
                }
            } else {
                Toast.makeText(requireContext(), "Invalid input. Please enter valid values.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }


    private fun startTimer(productivityDurationInSeconds: Long, breakDurationInSeconds: Long, longbreakDurationInSeconds: Long) {
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

                    // Update totalTimeSpent
                    val userProfileDbHelper = UserProfileDatabaseHelper(requireContext())
                    val sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)
                    val uid = sharedPreferences.getString("uid", null)
                    userProfileDbHelper.updateUserTotalTimeSpent(uid!!, productivityDurationInSeconds.toInt())

                    //update realtime firebase using updatefirebasedatabase

                    userProfileDbHelper.updateFirebaseDatabase(uid)


                    //start BreakTimeFragment and pass the break duration
                    startBreakTimer(breakDurationInSeconds, productivityDurationInSeconds, longbreakDurationInSeconds)

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

    private fun startBreakTimer(durationInSeconds: Long, productivityDurationInSeconds: Long, longbreakDurationInSeconds: Long) {
        val totalMillis = durationInSeconds * 1000L
        val totalProductivityMillis = productivityDurationInSeconds * 1000L
        val totalLongbreakMillis = longbreakDurationInSeconds * 1000L
        if (totalMillis <= 0) return

        setTime(totalMillis)

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        val delayMillis = 200L

        // Access the root layout of the activity and postDelayed on it
        view?.postDelayed({
            // Redirect to the BreakFragment
            val breakTimeFragment = BreakTimeFragment.newInstance(totalMillis, totalMillis, totalProductivityMillis, totalLongbreakMillis)
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, breakTimeFragment)
                commit()
            }

            // Start the break timer
            //breakTimeFragment.startTimer()

            // Update the timer text when starting break timer
            updateCountDownText() // Update the timer text

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
        updateCountDownText() // Update the timer text

        // Set startTimer button to not clickable
        binding.btnMPStart.isEnabled = false

        // set cancel button to not clickable
        cancelButton.isEnabled = false

        // SharedViewmodel cyclecounter reset to 0
        sharedViewModel.cycleCounter = 0

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

    fun setupStartButton(productivityTimeInMillis: Long, breakDurationInMillis: Long, longbreakDurationInMillis: Long) {
        val productivityTimeInSecondsfunc = productivityTimeInMillis / 1000L

        val breakDurationInMillisfunc = breakDurationInMillis / 1000L

        val longbreakDurationInMillisfunc = longbreakDurationInMillis / 1000L

        binding.btnMPStart.apply {
            isEnabled = true
            setOnClickListener {
                startTimer(productivityTimeInSecondsfunc, breakDurationInMillisfunc, longbreakDurationInMillisfunc)
                isEnabled = false
            }
        }
    }
    companion object {
        const val PRODUCTIVITY_TIME_KEY = "productivity_time"
        const val BREAK_DURATION_KEY = "break_duration"
        const val LONGBREAK_DURATION_KEY = "longbreak_duration"
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

    override fun onResume() {
        super.onResume()
        //startTimeInMillis = 0
        //timeLeftInMillis = 0
        updateCountDownText() // Update the timer text when the fragment resumes
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
