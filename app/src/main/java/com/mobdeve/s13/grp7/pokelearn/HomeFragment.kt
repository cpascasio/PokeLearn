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

    private var timerSettingsDialog: BottomSheetDialog? = null

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

        // Find the clear default button within the dialog's view hierarchy
        val clearDefaultButton = timerSettingsDialog?.findViewById<Button>(R.id.btn_ClearDefault)
        clearDefaultButton?.setOnClickListener {
            clearDefaultValues()
        }
    }



    private fun clearDefaultValues() {
        // Ensure EditText objects are initialized
        if (::hoursEditText.isInitialized && ::minutesEditText.isInitialized && ::secondsEditText.isInitialized) {
            // Clear default values for productivity duration
            hoursEditText.setText("")
            minutesEditText.setText("")
            secondsEditText.setText("")

            // Clear default values for short break duration
            val dialogView = timerSettingsDialog?.findViewById<View>(R.id.dialog_timer_settings)
            val breakHoursEditText = dialogView?.findViewById<EditText>(R.id.breakHoursEditText)
            val breakMinutesEditText = dialogView?.findViewById<EditText>(R.id.breakMinutesEditText)
            val breakSecondsEditText = dialogView?.findViewById<EditText>(R.id.breakSecondsEditText)
            breakHoursEditText?.setText("")
            breakMinutesEditText?.setText("")
            breakSecondsEditText?.setText("")

            // Clear default values for long break duration
            val longbreakHoursEditText = dialogView?.findViewById<EditText>(R.id.longbreakHoursEditText)
            val longbreakMinutesEditText = dialogView?.findViewById<EditText>(R.id.longbreakMinutesEditText)
            val longbreakSecondsEditText = dialogView?.findViewById<EditText>(R.id.longbreakSecondsEditText)
            longbreakHoursEditText?.setText("")
            longbreakMinutesEditText?.setText("")
            longbreakSecondsEditText?.setText("")
        } else {
            // EditText objects are not initialized, handle the case gracefully
            Log.e("HomeFragment", "EditText objects are not initialized")
            // You can show a toast or log an error message to indicate the issue
        }
    }




    private fun showTimerSettingsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_timer_settings, null)
        timerSettingsDialog = BottomSheetDialog(requireContext())
        timerSettingsDialog?.setContentView(dialogView)

        // Find the clear default button within the dialog's view hierarchy
        val clearDefaultButton = dialogView.findViewById<Button>(R.id.btn_ClearDefault)
        clearDefaultButton?.setOnClickListener {
            clearDefaultValues()
        }

        // Initialize EditText views
        hoursEditText = dialogView.findViewById(R.id.hoursEditText) ?: return
        minutesEditText = dialogView.findViewById(R.id.minutesEditText) ?: return
        secondsEditText = dialogView.findViewById(R.id.secondsEditText) ?: return

        // Set default values for productivity duration, short break, and long break
        val defaultProductivityTime = 25 // Default productivity time in minutes
        val defaultShortBreak = 5 // Default short break time in minutes
        val defaultLongBreak = 10 // Default long break time in minutes

        hoursEditText.setText("0")
        minutesEditText.setText(defaultProductivityTime.toString()) // Set default productivity duration
        secondsEditText.setText("0")

        // Set default short break and long break durations in the dialog
        dialogView.findViewById<EditText>(R.id.breakMinutesEditText)?.setText(defaultShortBreak.toString())
        dialogView.findViewById<EditText>(R.id.breakSecondsEditText)?.setText("0")
        dialogView.findViewById<EditText>(R.id.breakHoursEditText)?.setText("0")
        dialogView.findViewById<EditText>(R.id.longbreakMinutesEditText)?.setText(defaultLongBreak.toString())
        dialogView.findViewById<EditText>(R.id.longbreakSecondsEditText)?.setText("0")
        dialogView.findViewById<EditText>(R.id.longbreakHoursEditText)?.setText("0")

        val startTimerButton = dialogView.findViewById<Button>(R.id.btn_StartTimer)

        startTimerButton.setOnClickListener {
            timerSettingsDialog?.dismiss()
            // Start the timer based on the input duration
            val productivityHours = hoursEditText.text.toString().toIntOrNull() ?: 0
            val productivityMinutes = minutesEditText.text.toString().toIntOrNull() ?: 0
            val productivitySeconds = secondsEditText.text.toString().toIntOrNull() ?: 0

            // Get break time duration
            val breakHours = dialogView.findViewById<EditText>(R.id.breakHoursEditText)?.text.toString().toIntOrNull() ?: 0
            val breakMinutes = dialogView.findViewById<EditText>(R.id.breakMinutesEditText)?.text.toString().toIntOrNull() ?: 0
            val breakSeconds = dialogView.findViewById<EditText>(R.id.breakSecondsEditText)?.text.toString().toIntOrNull() ?: 0

            val longbreakHours = dialogView.findViewById<EditText>(R.id.longbreakHoursEditText)?.text.toString().toIntOrNull() ?: 0
            val longbreakMinutes = dialogView.findViewById<EditText>(R.id.longbreakMinutesEditText)?.text.toString().toIntOrNull() ?: 0
            val longbreakSeconds = dialogView.findViewById<EditText>(R.id.longbreakSecondsEditText)?.text.toString().toIntOrNull() ?: 0

            val productivityDurationInSeconds = productivityHours * 3600L + productivityMinutes * 60L + productivitySeconds
            val breakDurationInSeconds = breakHours * 3600L + breakMinutes * 60L + breakSeconds
            val longbreakDurationInSeconds = longbreakHours * 3600L + longbreakMinutes * 60L + longbreakSeconds

            // Validate user input
            if (isValidInput(productivityHours, productivityMinutes, productivitySeconds) &&
                isValidInput(breakHours, breakMinutes, breakSeconds)) {

                // Start the timer only if both productivity and break durations are greater than 0
                if (productivityDurationInSeconds > 0 && breakDurationInSeconds > 0 && longbreakDurationInSeconds > 0) {
                    // Re-show the progress bar
                    progressBar.visibility = View.VISIBLE
                    // set cancel button to clickable
                    cancelButton.isEnabled = true
                    startTimerButton.isEnabled = true

                    val totalProductivityMillis = productivityDurationInSeconds * 1000L
                    setTime(totalProductivityMillis)

                    //call setupstartbutton
                    setupStartButton(productivityDurationInSeconds * 1000L, breakDurationInSeconds * 1000L, longbreakDurationInSeconds * 1000L)
                }
            } else {
                // Show a toast message for invalid input
                Toast.makeText(requireContext(), "Invalid input. Please enter valid values.", Toast.LENGTH_SHORT).show()
            }
        }

        timerSettingsDialog?.show()
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
            breakTimeFragment.startTimer()

        }, delayMillis)
    }

    private fun updateProgressBar() {
        val progress = (timeLeftInMillis * 100 / startTimeInMillis).toInt()
        progressBar.progress = progress
    }

    private fun updateCountDownText() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        timerText.text = timeLeftFormatted
    }

    private fun setTime(milliseconds: Long) {
        startTimeInMillis = milliseconds
        timeLeftInMillis = milliseconds
        updateCountDownText()
    }

    private fun cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        isTimerSet = false
        progressBar.visibility = View.INVISIBLE

        // Load the static Pokeball image when the timer is canceled
        Glide.with(this)
            .load(R.drawable.pokeball_static)
            .into(shakingPokeballImageView)

        timerText.text = getString(R.string.default_timer_text)
        progressBar.progress = 0
    }

    private fun setProductivityTime(milliseconds: Long) {
        // Initialize the productivity time
        setTime(milliseconds)
    }

    private fun setupStartButton(productivityDurationInMillis: Long, breakDurationInMillis: Long, longbreakDurationInMillis: Long) {
        binding.btnMPStart.setOnClickListener {
            startTimer(productivityDurationInMillis / 1000, breakDurationInMillis / 1000, longbreakDurationInMillis / 1000)
        }
    }

    private fun isValidInput(hours: Int, minutes: Int, seconds: Int): Boolean {
        return hours >= 0 && minutes >= 0 && seconds >= 0
    }

    companion object {
        private const val PRODUCTIVITY_TIME_KEY = "PRODUCTIVITY_TIME"
        private const val BREAK_DURATION_KEY = "BREAK_DURATION"
        private const val LONGBREAK_DURATION_KEY = "LONGBREAK_DURATION"

        fun newInstance(productivityTimeInMillis: Long, breakDurationInMillis: Long, longbreakDurationInMillis: Long): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putLong(PRODUCTIVITY_TIME_KEY, productivityTimeInMillis)
            args.putLong(BREAK_DURATION_KEY, breakDurationInMillis)
            args.putLong(LONGBREAK_DURATION_KEY, longbreakDurationInMillis)
            fragment.arguments = args
            return fragment
        }
    }
}
