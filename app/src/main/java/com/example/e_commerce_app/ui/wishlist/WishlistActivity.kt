package com.example.e_commerce_app.ui.wishlist

import android.os.Bundle
import android.view.View
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
import com.example.e_commerce_app.ui.detail.ProductDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishlistActivity : AppCompatActivity() {

    private val viewModel: WishlistViewModel by viewModels()
    private lateinit var adapter: WishlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wishlist)
        applyEdgeInsets(R.id.wishlistRoot)

        findViewById<ImageButton>(R.id.wishlistBack).setOnClickListener { finish() }

        adapter = WishlistAdapter(
            onClick = { ProductDetailActivity.start(this, it.id) },
            onRemove = { viewModel.remove(it.id) },
            onMoveToCart = { viewModel.moveToCart(it.id) }
        )
        val list = findViewById<RecyclerView>(R.id.wishlistList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        val empty = findViewById<View>(R.id.wishlistEmpty)
        val countLabel = findViewById<TextView>(R.id.wishlistCount)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { items ->
                    adapter.submitList(items)
                    val isEmpty = items.isEmpty()
                    empty.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    list.visibility = if (isEmpty) View.GONE else View.VISIBLE
                    countLabel.text = "${items.size} items"
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