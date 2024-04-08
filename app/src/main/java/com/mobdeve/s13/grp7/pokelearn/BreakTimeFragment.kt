import SharedViewModel
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mobdeve.s13.grp7.pokelearn.databinding.FragmentBreakTimeBinding
import com.mobdeve.s13.grp7.pokelearn.R
import com.mobdeve.s13.grp7.pokelearn.HomeFragment
import com.mobdeve.s13.grp7.pokelearn.RewardsActivity


class BreakTimeFragment : Fragment() {
    private lateinit var binding: FragmentBreakTimeBinding
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var startButton: Button
    private lateinit var cancelButton: Button
    private lateinit var webView: WebView
    private lateinit var sharedViewModel: SharedViewModel

    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0
    private var isTimerSet: Boolean = false

    private val videoUrls = arrayOf(
        "https://www.youtube.com/embed/Kk4WCJobi3I?si=l2dMynYd_dG-2zbP&loop=1",
        "https://www.youtube.com/embed/joEMMnOgdMs?si=hcF1vpSSHfJuRA4o&loop=1",
        "https://www.youtube.com/embed/DmojrzZib5g?si=WNK1RPiLY1ea4Aop&loop=1",
        "https://www.youtube.com/embed/h_V1WxifpjA?si=Ql502XLEnDxWxl9u&loop=1",
        "https://www.youtube.com/embed/SNuY9rbGUio?si=i9MZ3y-6KGOkY0Ig&loop=1",
        "https://www.youtube.com/embed/QGZuKIPffV8?si=XAuldG91DJMWM_HI&loop=1"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentBreakTimeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize UI elements
        startButton = view.findViewById(R.id.btnStart)
        cancelButton = view.findViewById(R.id.btnCancel)
        webView = view.findViewById(R.id.webView)
        val timerText = binding.tvwBreakTimer

        // Set click listener for the start button
        startButton.setOnClickListener {
            startTimer()
            startButton.isEnabled = false
            cancelButton.isEnabled = true
        }

        // Set click listener for the cancel button
        cancelButton.setOnClickListener {
            cancelTimer()
        }

        // Get sharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        Log.d("BreakTimeFragment", "STARTING Cycle Counter: ${sharedViewModel.cycleCounter}")

        // Get break time duration and remaining time from arguments
        var breakDurationInMillis: Long
        var remainingTimeInMillis: Long
        var longbreakTimeInMillis: Long
        if(sharedViewModel.cycleCounter == 3) {
            // set breakDurationInMillis to 10 seconds

            Log.d("BreakTimeFragment", "SET TO 30 MINUTES")
            breakDurationInMillis = arguments?.getLong(LONGBREAK_DURATION_KEY) ?: 0
            remainingTimeInMillis = breakDurationInMillis


            //breakDurationInMillis = 1800000
            //remainingTimeInMillis = breakDurationInMillis
        }else{
            breakDurationInMillis = arguments?.getLong(BREAK_DURATION_KEY) ?: 0
            remainingTimeInMillis = arguments?.getLong(REMAINING_TIME_KEY) ?: breakDurationInMillis
            longbreakTimeInMillis = arguments?.getLong(LONGBREAK_DURATION_KEY) ?: 0
        }

        setTime(remainingTimeInMillis)

        // Update the timer text
        updateCountDownText()

        // Randomly select a video URL
        val randomIndex = (0 until videoUrls.size).random()
        val selectedVideoUrl = videoUrls[randomIndex]

        // Load the selected YouTube video in the WebView
        val videoHtml = "<iframe width=\"100%\" height=\"100%\" src=\"$selectedVideoUrl\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
        webView.loadData(videoHtml, "text/html", "utf-8")
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()

        return view
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                if(sharedViewModel.cycleCounter == 3) {
                    redirectToRewardsPage()
                    sharedViewModel.cycleCounter = 0
                    Log.d("BreakTimeFragment", "Cycle Counter if 3: ${sharedViewModel.cycleCounter}")
                } else {
                    sharedViewModel.cycleCounter++
                    Log.d("BreakTimeFragment", "Cycle Counter: ${sharedViewModel.cycleCounter}")
                    redirectToHomeFragment()
                }
            }
        }.start()

        isTimerSet = true
    }

    private fun cancelTimer() {
        if(::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        isTimerSet = false
        redirectToHomeFragment()
    }


    private fun setTime(milliseconds: Long) {
        startTimeInMillis = milliseconds
        timeLeftInMillis = milliseconds
        updateCountDownText()
    }

    private fun updateCountDownText() {
        val seconds = (timeLeftInMillis / 1000) % 60
        val minutes = ((timeLeftInMillis / 1000) / 60) % 60
        val hours = ((timeLeftInMillis / 1000) / 3600)

        val timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        binding.tvwBreakTimer.text = timeLeftFormatted
    }

    private fun redirectToHomeFragment() {
        Log.d("BreakTimeFragment", "Redirecting to HomeFragment")
        try {
            val productivityTimeInMillis = arguments?.getLong(PRODUCTIVITY_TIME_KEY) ?: 0
            val breakDurationInMillis = arguments?.getLong(BREAK_DURATION_KEY) ?: 0
            val longbreakTimeInMillis = arguments?.getLong(LONGBREAK_DURATION_KEY) ?: 0
            val homeFragment = HomeFragment().apply {
                arguments = Bundle().apply {
                    putLong(PRODUCTIVITY_TIME_KEY, productivityTimeInMillis)
                    putLong(BREAK_DURATION_KEY, breakDurationInMillis)
                    putLong(LONGBREAK_DURATION_KEY, longbreakTimeInMillis)
                }
            }

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
        const val PRODUCTIVITY_TIME_KEY = "productivity_time" // New constant for productivity time key
        const val LONGBREAK_DURATION_KEY = "longbreak_duration"

        fun newInstance(breakDurationInMillis: Long, remainingTimeInMillis: Long, productivityTimeInMillis: Long, longbreakTimeInMillis: Long): BreakTimeFragment {
            val fragment = BreakTimeFragment()
            val args = Bundle()
            args.putLong(BREAK_DURATION_KEY, breakDurationInMillis)
            args.putLong(REMAINING_TIME_KEY, remainingTimeInMillis)
            args.putLong(PRODUCTIVITY_TIME_KEY, productivityTimeInMillis) // Add productivity time to the arguments
            args.putLong(LONGBREAK_DURATION_KEY, longbreakTimeInMillis)
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

//    companion object {
//        const val BREAK_DURATION_KEY = "break_duration"
//        const val REMAINING_TIME_KEY = "remaining_time"
//        const val PRODUCTIVITY_TIME_KEY = "productivity_time"
//
//        fun newInstance(breakDurationInMillis: Long, remainingTimeInMillis: Long, productivityTimeInMillis: Long): BreakTimeFragment {
//            val fragment = BreakTimeFragment()
//            val args = Bundle()
//            args.putLong(BREAK_DURATION_KEY, breakDurationInMillis)
//            args.putLong(REMAINING_TIME_KEY, remainingTimeInMillis)
//            args.putLong(PRODUCTIVITY_TIME_KEY, productivityTimeInMillis)
//            fragment.arguments = args
//            return fragment
//        }
//    }
}