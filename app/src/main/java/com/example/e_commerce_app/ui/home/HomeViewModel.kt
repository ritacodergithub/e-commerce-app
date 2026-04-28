package com.example.e_commerce_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.data.repository.CartRepository
import com.example.e_commerce_app.data.repository.ProductRepository
import com.example.e_commerce_app.data.repository.WishlistRepository
import com.example.e_commerce_app.domain.model.Category
import com.example.e_commerce_app.domain.model.Product
import com.example.e_commerce_app.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepo: ProductRepository,
    private val wishlistRepo: WishlistRepository,
    cartRepo: CartRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _category = MutableStateFlow(Category.ALL)
    val category: StateFlow<Category> = _category.asStateFlow()

    private val _filters = MutableStateFlow(Filters())
    val filters: StateFlow<Filters> = _filters.asStateFlow()

    private val _categoriesState =
        MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState.asStateFlow()

    private val _refreshState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val refreshState: StateFlow<UiState<Unit>> = _refreshState.asStateFlow()

    val cartCount: StateFlow<Int> = cartRepo.observeCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val wishlistIds: StateFlow<Set<Int>> = wishlistRepo.observeIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val products: StateFlow<List<Product>> =
        combine(
            _query.debounce(200),
            _category,
            _filters,
            productRepo.observeProducts()
        ) { q, cat, filters, all ->
            all.filter { product ->
                (cat.slug.isEmpty() || product.category == cat.slug) &&
                    (q.isBlank() ||
                        product.title.contains(q, ignoreCase = true) ||
                        product.brand.contains(q, ignoreCase = true) ||
                        product.description.contains(q, ignoreCase = true) ||
                        product.tags.any { tag -> tag.contains(q, ignoreCase = true) }) &&
                    product.price in filters.minPrice..filters.maxPrice &&
                    product.rating >= filters.minRating
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            _refreshState.value = UiState.Loading
            productRepo.ensureCached().fold(
                onSuccess = { _refreshState.value = UiState.Success(Unit) },
                onFailure = { e ->
                    _refreshState.value = UiState.Error(e.message ?: "Failed to load products")
                }
            )
        }
        viewModelScope.launch {
            val cats = productRepo.getCategories()
            _categoriesState.value =
                if (cats.isEmpty()) UiState.Error("Couldn't load categories")
                else UiState.Success(listOf(Category.ALL) + cats)
        }
    }

    fun setQuery(value: String) {
        _query.value = value
    }

    fun setCategory(category: Category) {
        _category.value = category
    }

    fun applyFilters(filters: Filters) {
        _filters.value = filters
    }

    fun toggleWishlist(productId: Int) {
        viewModelScope.launch { wishlistRepo.toggle(productId) }
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshState.value = UiState.Loading
            productRepo.refreshProducts().fold(
                onSuccess = { _refreshState.value = UiState.Success(Unit) },
                onFailure = { e ->
                    _refreshState.value = UiState.Error(e.message ?: "Refresh failed")
                }
            )
        }
    }

    data class Filters(
        val minPrice: Double = 0.0,
        val maxPrice: Double = Double.MAX_VALUE,
        val minRating: Double = 0.0
    ) {
        val isActive: Boolean
            get() = minPrice > 0.0 || maxPrice < Double.MAX_VALUE || minRating > 0.0
    }
}