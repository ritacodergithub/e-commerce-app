package com.example.e_commerce_app.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.data.repository.CartRepository
import com.example.e_commerce_app.domain.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepo: CartRepository
) : ViewModel() {

    val items: StateFlow<List<CartItem>> = cartRepo.observeCart()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totals: StateFlow<Totals> = cartRepo.observeCart()
        .map { items ->
            val subtotal = items.sumOf { it.lineTotal }
            val shipping = if (items.isEmpty() || subtotal >= 1000.0) 0.0 else 49.0
            val tax = subtotal * 0.05
            Totals(subtotal, shipping, tax, subtotal + shipping + tax, items.sumOf { it.quantity })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Totals())

    fun increment(item: CartItem) {
        viewModelScope.launch { cartRepo.setQuantity(item.product.id, item.quantity + 1) }
    }

    fun decrement(item: CartItem) {
        viewModelScope.launch { cartRepo.setQuantity(item.product.id, item.quantity - 1) }
    }

    fun remove(item: CartItem) {
        viewModelScope.launch { cartRepo.remove(item.product.id) }
    }

    data class Totals(
        val subtotal: Double = 0.0,
        val shipping: Double = 0.0,
        val tax: Double = 0.0,
        val total: Double = 0.0,
        val itemCount: Int = 0
    )
}