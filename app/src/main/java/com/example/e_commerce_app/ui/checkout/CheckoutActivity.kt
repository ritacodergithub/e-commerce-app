package com.example.e_commerce_app.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.e_commerce_app.R
import com.example.e_commerce_app.ui.success.OrderSuccessActivity
import com.example.e_commerce_app.util.PriceFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {

    private val viewModel: CheckoutViewModel by viewModels()

    private enum class Payment(val label: String) {
        CARD("Credit / debit card"),
        UPI("UPI / wallet"),
        COD("Cash on delivery")
    }

    private var paymentMethod: Payment = Payment.CARD

    private lateinit var payCard: LinearLayout
    private lateinit var payUpi: LinearLayout
    private lateinit var payCod: LinearLayout
    private lateinit var payCardCheck: ImageView
    private lateinit var payUpiCheck: ImageView
    private lateinit var payCodCheck: ImageView
    private lateinit var cardFields: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)
        applyEdgeInsets(R.id.checkoutRoot)

        findViewById<ImageButton>(R.id.checkoutBack).setOnClickListener { finish() }

        payCard = findViewById(R.id.payCard)
        payUpi = findViewById(R.id.payUpi)
        payCod = findViewById(R.id.payCod)
        payCardCheck = findViewById(R.id.payCardCheck)
        payUpiCheck = findViewById(R.id.payUpiCheck)
        payCodCheck = findViewById(R.id.payCodCheck)
        cardFields = findViewById(R.id.cardFields)

        payCard.setOnClickListener { selectPayment(Payment.CARD) }
        payUpi.setOnClickListener { selectPayment(Payment.UPI) }
        payCod.setOnClickListener { selectPayment(Payment.COD) }
        selectPayment(Payment.CARD)

        findViewById<Button>(R.id.checkoutPlaceOrder).setOnClickListener { placeOrder() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.totals.collect { totals ->
                        findViewById<TextView>(R.id.checkoutSubtotal).text =
                            PriceFormat.format(totals.subtotal)
                        findViewById<TextView>(R.id.checkoutShipping).text =
                            if (totals.shipping == 0.0) getString(R.string.shipping_free)
                            else PriceFormat.format(totals.shipping)
                        findViewById<TextView>(R.id.checkoutTax).text =
                            PriceFormat.format(totals.tax)
                        findViewById<TextView>(R.id.checkoutTotal).text =
                            PriceFormat.format(totals.total)
                    }
                }
                launch {
                    viewModel.placement.collect { state ->
                        when (state) {
                            is CheckoutViewModel.Placement.Success -> {
                                startActivity(
                                    Intent(this@CheckoutActivity, OrderSuccessActivity::class.java)
                                        .putExtra(OrderSuccessActivity.EXTRA_ORDER_ID, state.orderId)
                                        .putExtra(OrderSuccessActivity.EXTRA_ORDER_TOTAL, state.total)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                viewModel.reset()
                                finish()
                            }
                            is CheckoutViewModel.Placement.Error ->
                                Toast.makeText(this@CheckoutActivity, state.message, Toast.LENGTH_SHORT).show()
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun selectPayment(method: Payment) {
        paymentMethod = method
        payCard.isSelected = method == Payment.CARD
        payUpi.isSelected = method == Payment.UPI
        payCod.isSelected = method == Payment.COD
        payCardCheck.visibility = if (method == Payment.CARD) View.VISIBLE else View.INVISIBLE
        payUpiCheck.visibility = if (method == Payment.UPI) View.VISIBLE else View.INVISIBLE
        payCodCheck.visibility = if (method == Payment.COD) View.VISIBLE else View.INVISIBLE
        cardFields.visibility = if (method == Payment.CARD) View.VISIBLE else View.GONE
    }

    private fun placeOrder() {
        val address = findViewById<EditText>(R.id.checkoutAddress).text.toString().trim()
        val city = findViewById<EditText>(R.id.checkoutCity).text.toString().trim()
        val postal = findViewById<EditText>(R.id.checkoutPostal).text.toString().trim()
        val phone = findViewById<EditText>(R.id.checkoutPhone).text.toString().trim()

        if (address.isEmpty() || city.isEmpty() || postal.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, R.string.error_address_required, Toast.LENGTH_SHORT).show()
            return
        }

        if (paymentMethod == Payment.CARD) {
            val number = findViewById<EditText>(R.id.checkoutCardNumber).text.toString().trim()
            val expiry = findViewById<EditText>(R.id.checkoutCardExpiry).text.toString().trim()
            val cvv = findViewById<EditText>(R.id.checkoutCardCvv).text.toString().trim()
            if (number.length < 12 || expiry.length < 4 || cvv.length < 3) {
                Toast.makeText(this, R.string.error_card_required, Toast.LENGTH_SHORT).show()
                return
            }
        }

        val shippingAddress = "$address, $city - $postal · $phone"
        viewModel.placeOrder(
            paymentMethod = paymentMethod.label,
            shippingAddress = shippingAddress
        )
    }

    private fun applyEdgeInsets(rootId: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootId)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}