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
import com.example.e_commerce_app.databinding.ActivityCheckoutBinding
import com.example.e_commerce_app.ui.success.OrderSuccessActivity
import com.example.e_commerce_app.util.PriceFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {

    private val viewModel: CheckoutViewModel by viewModels()
    private lateinit var binding: ActivityCheckoutBinding

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
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        applyEdgeInsets(R.id.checkoutRoot)

        findViewById<ImageButton>(R.id.checkoutBack).setOnClickListener { finish() }

        payCard = binding.payCard
        payUpi = binding.payUpi
        payCod = binding.payCod
        payCardCheck = binding.payCardCheck
        payUpiCheck = binding.payUpiCheck
        payCodCheck = binding.payCodCheck
        cardFields = binding.cardFields
        payCard.setOnClickListener { selectPayment(Payment.CARD) }
        payUpi.setOnClickListener { selectPayment(Payment.UPI) }
        payCod.setOnClickListener { selectPayment(Payment.COD) }
        selectPayment(Payment.CARD)

        binding.checkoutPlaceOrder.setOnClickListener { placeOrder() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.totals.collect { totals ->
                        binding.checkoutSubtotal.text =
                            PriceFormat.format(totals.subtotal)
                        binding.checkoutShipping.text =
                            if (totals.shipping == 0.0) getString(R.string.shipping_free)
                            else PriceFormat.format(totals.shipping)
                        binding.checkoutTax.text =
                            PriceFormat.format(totals.tax)
                        binding.checkoutTotal.text =
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
        val address = binding.checkoutAddress.text.toString().trim()
        val city = binding.checkoutCity.text.toString().trim()
        val postal = binding.checkoutPostal.text.toString().trim()
        val phone = binding.checkoutPhone.text.toString().trim()

        if (address.isEmpty() || city.isEmpty() || postal.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, R.string.error_address_required, Toast.LENGTH_SHORT).show()
            return
        }

        if (paymentMethod == Payment.CARD) {
            val number = binding.checkoutCardNumber.text.toString().trim()
            val expiry = binding.checkoutCardExpiry.text.toString().trim()
            val cvv = binding.checkoutCardCvv.text.toString().trim()
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