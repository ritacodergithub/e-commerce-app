package com.example.e_commerce_app.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_app.R
import com.example.e_commerce_app.data.repository.AuthRepository
import com.example.e_commerce_app.ui.auth.LoginActivity
import com.example.e_commerce_app.ui.cart.CartActivity
import com.example.e_commerce_app.ui.common.UiState
import com.example.e_commerce_app.ui.detail.ProductDetailActivity
import com.example.e_commerce_app.ui.home.adapter.BannerAdapter
import com.example.e_commerce_app.ui.home.adapter.CategoryAdapter
import com.example.e_commerce_app.ui.home.adapter.ProductGridAdapter
import com.example.e_commerce_app.ui.orders.OrderHistoryActivity
import com.example.e_commerce_app.ui.wishlist.WishlistActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    @Inject lateinit var auth: AuthRepository

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductGridAdapter
    private lateinit var bannerPager: ViewPager2
    private lateinit var bannerDots: LinearLayout
    private lateinit var searchInput: EditText
    private lateinit var searchClear: ImageView
    private lateinit var resultsLabel: TextView
    private lateinit var resultsCount: TextView
    private lateinit var emptyState: TextView
    private lateinit var grid: RecyclerView
    private lateinit var cartBadge: TextView
    private lateinit var swipe: SwipeRefreshLayout

    private var ignoreNextSearchChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        applyEdgeInsets(R.id.homeRoot)

        findViewById<TextView>(R.id.homeGreeting).text =
            getString(R.string.home_greeting, auth.userName())

        searchInput = findViewById(R.id.searchInput)
        searchClear = findViewById(R.id.searchClear)
        resultsLabel = findViewById(R.id.resultsLabel)
        resultsCount = findViewById(R.id.resultsCount)
        emptyState = findViewById(R.id.emptyState)
        grid = findViewById(R.id.productGrid)
        cartBadge = findViewById(R.id.cartBadge)
        bannerPager = findViewById(R.id.bannerPager)
        bannerDots = findViewById(R.id.bannerDots)
        swipe = findViewById(R.id.homeSwipe)

        setUpBanners()
        setUpCategories()
        setUpGrid()
        setUpSearch()
        setUpToolbarActions()

        swipe.setColorSchemeResources(R.color.brand_primary)
        swipe.setOnRefreshListener { viewModel.refresh() }

        observe()
    }

    private fun setUpBanners() {
        val banners = listOf(
            BannerAdapter.Banner(R.string.banner_title_1, R.string.banner_subtitle_1, R.drawable.bg_banner_indigo),
            BannerAdapter.Banner(R.string.banner_title_2, R.string.banner_subtitle_2, R.drawable.bg_banner_coral),
            BannerAdapter.Banner(R.string.banner_title_3, R.string.banner_subtitle_3, R.drawable.bg_banner_mint)
        )
        bannerPager.adapter = BannerAdapter(banners) { /* navigate to a sale page later */ }
        renderBannerDots(banners.size, 0)
        bannerPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                renderBannerDots(banners.size, position)
            }
        })
    }

    private fun renderBannerDots(count: Int, active: Int) {
        bannerDots.removeAllViews()
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
            bannerDots.addView(dot)
        }
    }

    private fun setUpCategories() {
        val list = findViewById<RecyclerView>(R.id.categoryList)
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter { viewModel.setCategory(it) }
        list.adapter = categoryAdapter
    }

    private fun setUpGrid() {
        productAdapter = ProductGridAdapter(
            onClick = { product -> ProductDetailActivity.start(this, product.id) },
            onWishlistToggle = { product ->
                lifecycleScope.launch {
                    val ids = viewModel.wishlistIds.value
                    if (product.id in ids) {
                        // remove
                    }
                }
                // Use the detail VM-style toggle through wishlist repo via dedicated call:
                viewModel.toggleWishlist(product.id)
            },
            isWishlisted = { id -> id in viewModel.wishlistIds.value }
        )
        grid.layoutManager = GridLayoutManager(this, 2)
        grid.adapter = productAdapter
        val pad = (6 * resources.displayMetrics.density).toInt()
        grid.addItemDecoration(GridSpacingDecoration(pad))
    }

    private fun setUpSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (ignoreNextSearchChange) {
                    ignoreNextSearchChange = false
                    return
                }
                val q = s?.toString().orEmpty()
                searchClear.visibility = if (q.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.setQuery(q)
            }
        })
        searchClear.setOnClickListener { searchInput.setText("") }
    }

    private fun setUpToolbarActions() {
        findViewById<FrameLayout>(R.id.cartButton).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        findViewById<ImageButton>(R.id.homeWishlistButton).setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }
        findViewById<ImageButton>(R.id.homeOrdersButton).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }
        findViewById<ImageButton>(R.id.filterButton).setOnClickListener {
            FilterBottomSheet.newInstance().show(supportFragmentManager, "filters")
        }
        findViewById<ImageButton>(R.id.logoutButton).setOnClickListener {
            auth.logout()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.products.collect { list ->
                        productAdapter.submitList(list)
                        emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        grid.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                        resultsCount.text = "${list.size} items"
                    }
                }
                launch {
                    viewModel.categoriesState.collect { state ->
                        if (state is UiState.Success) categoryAdapter.submit(state.data)
                    }
                }
                launch {
                    viewModel.cartCount.collect { count ->
                        if (count > 0) {
                            cartBadge.text = if (count > 9) "9+" else count.toString()
                            cartBadge.visibility = View.VISIBLE
                        } else cartBadge.visibility = View.GONE
                    }
                }
                launch {
                    viewModel.wishlistIds.collect { productAdapter.notifyDataSetChanged() }
                }
                launch {
                    viewModel.refreshState.collect { state ->
                        swipe.isRefreshing = state is UiState.Loading
                    }
                }
                launch {
                    viewModel.query.collect { q ->
                        resultsLabel.setText(
                            if (q.isNotEmpty() || viewModel.category.value.slug.isNotEmpty())
                                R.string.label_results
                            else R.string.label_featured
                        )
                    }
                }
                launch {
                    viewModel.category.collect {
                        resultsLabel.setText(
                            if (viewModel.query.value.isNotEmpty() || it.slug.isNotEmpty())
                                R.string.label_results
                            else R.string.label_featured
                        )
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

    private class GridSpacingDecoration(private val spacing: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: android.graphics.Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(spacing, spacing, spacing, spacing)
        }
    }
}