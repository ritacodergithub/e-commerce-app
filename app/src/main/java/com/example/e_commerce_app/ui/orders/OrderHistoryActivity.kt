package com.example.e_commerce_app.ui.orders

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderHistoryActivity : AppCompatActivity() {

    private val viewModel: OrderHistoryViewModel by viewModels()
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_history)
        applyEdgeInsets(R.id.ordersRoot)

        findViewById<ImageButton>(R.id.ordersBack).setOnClickListener { finish() }

        adapter = OrderAdapter()
        val list = findViewById<RecyclerView>(R.id.ordersList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        val empty = findViewById<View>(R.id.ordersEmpty)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orders.collect { orders ->
                    adapter.submitList(orders)
                    val isEmpty = orders.isEmpty()
                    empty.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    list.visibility = if (isEmpty) View.GONE else View.VISIBLE
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