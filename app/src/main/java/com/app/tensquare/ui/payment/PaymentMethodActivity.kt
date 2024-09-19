package com.app.tensquare.ui.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.tensquare.activity.ThankYouActivity
import com.app.tensquare.databinding.*
import com.app.tensquare.utils.AppUtills

class PaymentMethodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppUtills.disableSsAndRecording(this@PaymentMethodActivity)

        binding.apply {
            imgBack.setOnClickListener { finish() }

            llCreditCard.setOnClickListener {
                Intent(this@PaymentMethodActivity, ThankYouActivity::class.java).also {
                    it.putExtra("ACTION", "PAYMENT")
                    startActivity(it)
                }
            }

            llUPI.setOnClickListener {
                Intent(this@PaymentMethodActivity, ThankYouActivity::class.java).also {
                    it.putExtra("ACTION", "PAYMENT")
                    startActivity(it)
                }
            }

            llWallet.setOnClickListener {
                Intent(this@PaymentMethodActivity, WalletOptionActivity::class.java).also {
                    startActivity(it)
                }
            }

            llPaytm.setOnClickListener {
                Intent(this@PaymentMethodActivity, ThankYouActivity::class.java).also {
                    it.putExtra("ACTION", "PAYMENT")
                    startActivity(it)
                }
            }
        }

    }


}