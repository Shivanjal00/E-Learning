package com.app.tensquare.ui.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.tensquare.activity.ThankYouActivity
import com.app.tensquare.databinding.*
import com.app.tensquare.utils.AppUtills

class WalletOptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppUtills.disableSsAndRecording(this@WalletOptionActivity)

        binding.apply {
            imgBack.setOnClickListener { finish() }

            llAmazonWallet.setOnClickListener {
                Intent(this@WalletOptionActivity, ThankYouActivity::class.java).also {
                    it.putExtra("ACTION", "PAYMENT")
                    startActivity(it)
                }
            }

            llPaytmWallet.setOnClickListener {
                Intent(this@WalletOptionActivity, ThankYouActivity::class.java).also {
                    it.putExtra("ACTION", "PAYMENT")
                    startActivity(it)
                }
            }

        }
    }

}