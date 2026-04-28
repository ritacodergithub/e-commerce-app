package com.example.e_commerce_app.ui.detail

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_app.R
import com.example.e_commerce_app.data.repository.CartRepository
import com.example.e_commerce_app.domain.model.Product
import com.example.e_commerce_app.ui.cart.CartActivity
import com.example.e_commerce_app.ui.checkout.CheckoutActivity
import com.example.e_commerce_app.ui.common.UiState
import com.example.e_commerce_app.util.PriceFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    private val viewModel: ProductDetailViewModel by viewModels()

    @Inject lateinit var cartRepo: CartRepository

    private lateinit var pagerAdapter: ImagePagerAdapter
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var dotsContainer: LinearLayout
    private lateinit var wishlistBtn: ImageButton
    private lateinit var cartBadge: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        applyEdgeInsets(R.id.detailRoot)

        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId < 0) { finish(); return }

        pagerAdapter = ImagePagerAdapter()
        reviewAdapter = ReviewAdapter()
        dotsContainer = findViewById(R.id.detailDots)
        wishlistBtn = findViewById(R.id.detailWishlist)
        cartBadge = findViewById(R.id.detailCartBadge)

        val pager = findViewById<ViewPager2>(R.id.detailPager)
        pager.adapter = pagerAdapter
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                renderDots(pagerAdapter.itemCount, position)
            }
        })

        val reviewsList = findViewById<RecyclerView>(R.id.reviewsList)
        reviewsList.layoutManager = LinearLayoutManager(this)
        reviewsList.adapter = reviewAdapter

        findViewById<ImageButton>(R.id.detailBack).setOnClickListener { finish() }
        findViewById<FrameLayout>(R.id.detailCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        wishlistBtn.setOnClickListener { viewModel.toggleWishlist() }

        findViewById<Button>(R.id.detailAddToCart).setOnClickListener {
            viewModel.addToCart()
            Toast.makeText(this, R.string.msg_added_to_cart, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.detailBuyNow).setOnClickListener {
            viewModel.addToCart()
            startActivity(Intent(this, CheckoutActivity::class.java))
        }

        viewModel.load(productId)

        observe()
    }

    private fun observe() {
        val cartCount = cartRepo.observeCount()
            .stateIn(lifecycleScope, SharingStarted.Eagerly, 0)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        when (state) {
                            is UiState.Success -> bindDetail(state.data)
                            is UiState.Error ->
                                Toast.makeText(this@ProductDetailActivity, state.message, Toast.LENGTH_SHORT).show()
                            UiState.Loading -> Unit
                        }
                    }
                }
                launch {
                    viewModel.isInWishlist.collect { inList ->
                        wishlistBtn.setImageResource(
                            if (inList) R.drawable.ic_heart_filled else R.drawable.ic_heart
                        )
                    }
                }
                launch {
                    cartCount.collect { count ->
                        if (count > 0) {
                            cartBadge.text = if (count > 9) "9+" else count.toString()
                            cartBadge.visibility = View.VISIBLE
                        } else cartBadge.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun bindDetail(detail: ProductDetailViewModel.Detail) {
        val product = detail.product
        val images = if (product.images.isNotEmpty()) product.images
        else listOfNotNull(product.thumbnail.takeIf { it.isNotEmpty() })
        pagerAdapter.submit(images)
        renderDots(images.size, 0)

        findViewById<TextView>(R.id.detailBrand).text =
            product.brand.ifEmpty { product.displayCategory }
        findViewById<TextView>(R.id.detailName).text = product.title
        findViewById<TextView>(R.id.detailRating).text =
            "%.1f · %d in stock".format(product.rating, product.stock)
        findViewById<TextView>(R.id.detailPrice).text = PriceFormat.format(product.price)
        findViewById<TextView>(R.id.detailDescription).text = product.description

        val original = findViewById<TextView>(R.id.detailPriceOriginal)
        val discount = findViewById<TextView>(R.id.detailDiscount)
        if (product.discountPercentage > 0) {
            original.text = PriceFormat.format(product.originalPrice)
            original.paintFlags = original.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            original.visibility = View.VISIBLE
            discount.text = "-${product.discountPercentInt}%"
            discount.visibility = View.VISIBLE
        } else {
            original.visibility = View.GONE
            discount.visibility = View.GONE
        }

        if (detail.reviews.isEmpty()) {
            findViewById<View>(R.id.reviewsEmpty).visibility = View.VISIBLE
            findViewById<View>(R.id.reviewsList).visibility = View.GONE
        } else {
            findViewById<View>(R.id.reviewsEmpty).visibility = View.GONE
            findViewById<View>(R.id.reviewsList).visibility = View.VISIBLE
            reviewAdapter.submit(detail.reviews)
        }
    }

    private fun renderDots(count: Int, active: Int) {
        dotsContainer.removeAllViews()
        if (count <= 1) return
        val density = resources.displayMetrics.density
        val gap = (4 * density).toInt()
        repeat(count) { i ->
            val dot = View(this)
            val isActive = i == active
            val width = (if (isActive) 18 else 6) * density
            val height = 6 * density
            val params = LinearLayout.LayoutParams(width.toInt(), height.toInt()).apply {
                marginStart = gap; marginEnd = gap
            }
            dot.layoutParams = params
            dot.setBackgroundResource(
                if (isActive) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
            )
            dotsContainer.addView(dot)
        }
    }

    private fun applyEdgeInsets(rootId: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootId)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "product_id"

        fun start(context: Context, productId: Int) {
            context.startActivity(
                Intent(context, ProductDetailActivity::class.java)
                    .putExtra(EXTRA_PRODUCT_ID, productId)
            )
        }
    }
}