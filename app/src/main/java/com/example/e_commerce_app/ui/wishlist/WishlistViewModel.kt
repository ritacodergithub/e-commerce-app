package com.example.e_commerce_app.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.data.repository.CartRepository
import com.example.e_commerce_app.data.repository.WishlistRepository
import com.example.e_commerce_app.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepo: WishlistRepository,
    private val cartRepo: CartRepository
) : ViewModel() {

    val items: StateFlow<List<Product>> = wishlistRepo.observeWishlist()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun remove(productId: Int) {
        viewModelScope.launch { wishlistRepo.remove(productId) }
    }

    fun moveToCart(productId: Int) {
        viewModelScope.launch {
            cartRepo.add(productId)
            wishlistRepo.remove(productId)
        }
    }
}