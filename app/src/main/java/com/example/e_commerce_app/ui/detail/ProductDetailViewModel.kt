package com.example.e_commerce_app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.data.repository.CartRepository
import com.example.e_commerce_app.data.repository.ProductRepository
import com.example.e_commerce_app.data.repository.WishlistRepository
import com.example.e_commerce_app.domain.model.Product
import com.example.e_commerce_app.domain.model.Review
import com.example.e_commerce_app.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val wishlistRepo: WishlistRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Detail>>(UiState.Loading)
    val state: StateFlow<UiState<Detail>> = _state.asStateFlow()

    private val productIdFlow = MutableStateFlow<Int?>(null)

    val isInWishlist: StateFlow<Boolean> =
        kotlinx.coroutines.flow.flow {
            productIdFlow.collect { id ->
                if (id == null) emit(false)
                else wishlistRepo.observeContains(id).collect { emit(it) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun load(productId: Int) {
        productIdFlow.value = productId
        viewModelScope.launch {
            _state.value = UiState.Loading
            val pair = productRepo.getProductWithReviews(productId)
            _state.value = if (pair == null) {
                UiState.Error("Couldn't load product")
            } else {
                UiState.Success(Detail(product = pair.first, reviews = pair.second))
            }
        }
    }

    fun addToCart() {
        val product = (state.value as? UiState.Success<Detail>)?.data?.product ?: return
        viewModelScope.launch { cartRepo.add(product.id) }
    }

    fun toggleWishlist() {
        val product = (state.value as? UiState.Success<Detail>)?.data?.product ?: return
        viewModelScope.launch { wishlistRepo.toggle(product.id) }
    }

    data class Detail(val product: Product, val reviews: List<Review>)
}