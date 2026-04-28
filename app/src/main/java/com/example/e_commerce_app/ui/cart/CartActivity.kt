package com.example.e_commerce_app.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.ui.checkout.CheckoutActivity
import com.example.e_commerce_app.util.PriceFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private val viewModel: CartViewModel by viewModels()

    private lateinit var adapter: CartAdapter
    private lateinit var emptyState: View
    private lateinit var summary: View
    private lateinit var list: RecyclerView
    private lateinit var subtotalText: TextView
    private lateinit var shippingText: TextView
    private lateinit var taxText: TextView
    private lateinit var totalText: TextView
    private lateinit var itemCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        applyEdgeInsets(R.id.cartRoot)

        list = findViewById(R.id.cartList)
        emptyState = findViewById(R.id.cartEmptyState)
        summary = findViewById(R.id.cartSummary)
        subtotalText = findViewById(R.id.cartSubtotal)
        shippingText = findViewById(R.id.cartShipping)
        taxText = findViewById(R.id.cartTax)
        totalText = findViewById(R.id.cartTotal)
        itemCountText = findViewById(R.id.cartItemCount)

        adapter = CartAdapter(
            onIncrement = { viewModel.increment(it) },
            onDecrement = { viewModel.decrement(it) },
            onRemove = { viewModel.remove(it) }
        )
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        findViewById<ImageButton>(R.id.cartBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.cartEmptyShopButton).setOnClickListener { finish() }
        findViewById<Button>(R.id.cartCheckoutButton).setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.items.collect { items ->
                        adapter.submitList(items)
                        val empty = items.isEmpty()
                        emptyState.visibility = if (empty) View.VISIBLE else View.GONE
                        list.visibility = if (empty) View.GONE else View.VISIBLE
                        summary.visibility = if (empty) View.GONE else View.VISIBLE
                    }
                }
                launch {
                    viewModel.totals.collect { totals ->
                        itemCountText.text = "${totals.itemCount} items"
                        subtotalText.text = PriceFormat.format(totals.subtotal)
                        shippingText.text =
                            if (totals.shipping == 0.0) getString(R.string.shipping_free)
                            else PriceFormat.format(totals.shipping)
                        taxText.text = PriceFormat.format(totals.tax)
                        totalText.text = PriceFormat.format(totals.total)
                    }
                }
            }
        }
    }

    private fun applyEdgeInsets(rootId: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootId)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}