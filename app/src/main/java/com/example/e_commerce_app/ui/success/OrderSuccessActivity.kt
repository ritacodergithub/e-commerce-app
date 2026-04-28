package com.example.e_commerce_app.ui.success

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce_app.R
import com.example.e_commerce_app.ui.home.HomeActivity
import com.example.e_commerce_app.util.PriceFormat

class OrderSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_success)
        applyEdgeInsets(R.id.successRoot)

        val orderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: "SHP-000000"
        val total = intent.getDoubleExtra(EXTRA_ORDER_TOTAL, 0.0)

        findViewById<TextView>(R.id.successOrderId).text = orderId
        findViewById<TextView>(R.id.successOrderTotal).text = PriceFormat.format(total)

        val goHome = {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        findViewById<Button>(R.id.successButton).setOnClickListener { goHome() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { goHome() }
        })
    }

    private fun applyEdgeInsets(rootId: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootId)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    companion object {
        const val EXTRA_ORDER_ID = "order_id"
        const val EXTRA_ORDER_TOTAL = "order_total"
    }
}