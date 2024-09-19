package com.app.tensquare.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.tensquare.R
import com.app.tensquare.databinding.ActivityThankYouBinding
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.utils.*

class ThankYouActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThankYouBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivityThankYouBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppUtills.disableSsAndRecording(this@ThankYouActivity)

        binding.txtMessage.text = when {
            intent.getStringExtra(ACTION) == SIGN_UP -> getString(R.string.thank_for_registration)
            //intent.getStringExtra(ACTION) == FORGOT_PASSWORD -> getString(R.string.password_is_reset)
            else -> getString(R.string.enrolment_completion)
        }

        if (intent.getStringExtra(ACTION) == SIGN_UP) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    val broadcast = Intent(JUMP_TO_HOME_BROADCAST)
                    sendBroadcast(broadcast)
                    Intent(this@ThankYouActivity, HomeActivity::class.java).also {
                        it.putExtra(ACTION, intent.getStringExtra(ACTION))
                        startActivity(it)
                    }
                    finish()
                }, 1000L
            )
        } else {
            binding.txtContinue.visibility = View.VISIBLE
        }
        binding.txtContinue.setOnClickListener {
            Intent(this@ThankYouActivity, HomeActivity::class.java).also {
                it.putExtra(ACTION, intent.getStringExtra(ACTION))
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
            }
            finish()
        }


    }
}