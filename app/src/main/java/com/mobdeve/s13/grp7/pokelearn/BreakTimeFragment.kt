import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val progressBar = binding.breakProgressBar

        // Get break time duration from arguments
        val breakDurationInMillis = arguments?.getLong(BREAK_DURATION_KEY) ?: 0
        setTime(breakDurationInMillis)

        // Start the break timer
        startTimer()

        return view
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
                updateProgressBar()
            }

            override fun onFinish() {
                // Handle what to do when the break time is over
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

    private fun updateProgressBar() {
        val progress = (((startTimeInMillis - timeLeftInMillis) * 100) / startTimeInMillis).toInt()
        binding.breakProgressBar.progress = progress
    }

    companion object {
        const val BREAK_DURATION_KEY = "break_duration"

        fun newInstance(breakDurationInMillis: Long): BreakTimeFragment {
            val fragment = BreakTimeFragment()
            val args = Bundle()
            args.putLong(BREAK_DURATION_KEY, breakDurationInMillis)
            fragment.arguments = args
            return fragment
        }
    }
}
