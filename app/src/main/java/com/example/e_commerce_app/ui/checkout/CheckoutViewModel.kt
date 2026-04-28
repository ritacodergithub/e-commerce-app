package com.example.e_commerce_app.ui.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.data.repository.CartRepository
import com.example.e_commerce_app.data.repository.OrderRepository
import com.example.e_commerce_app.domain.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val _placement = MutableStateFlow<Placement>(Placement.Idle)
    val placement: StateFlow<Placement> = _placement.asStateFlow()

    val items: StateFlow<List<CartItem>> = cartRepo.observeCart()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val totals: StateFlow<Totals> = cartRepo.observeCart()
        .map { items ->
            val subtotal = items.sumOf { it.lineTotal }
            val shipping = if (items.isEmpty() || subtotal >= 1000.0) 0.0 else 49.0
            val tax = subtotal * 0.05
            Totals(subtotal, shipping, tax, subtotal + shipping + tax)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Totals())

    fun placeOrder(paymentMethod: String, shippingAddress: String) {
        viewModelScope.launch {
            val current = cartRepo.observeCart().first()
            if (current.isEmpty()) {
                _placement.value = Placement.Error("Cart is empty")
                return@launch
            }
            viewModelScope.launch {
                _placement.value = Placement.Loading
                val totals = totals.value
                val orderId = orderRepo.placeOrder(
                    items = current,
                    subtotal = totals.subtotal,
                    shipping = totals.shipping,
                    tax = totals.tax,
                    total = totals.total,
                    paymentMethod = paymentMethod,
                    shippingAddress = shippingAddress
                )
                cartRepo.clear()
                _placement.value = Placement.Success(orderId, totals.total)
            }
        }
    }

    fun reset() {
        _placement.value = Placement.Idle
    }

    data class Totals(
        val subtotal: Double = 0.0,
        val shipping: Double = 0.0,
        val tax: Double = 0.0,
        val total: Double = 0.0
    )

    sealed class Placement {
        data object Idle : Placement()
        data object Loading : Placement()
        data class Success(val orderId: String, val total: Double) : Placement()
        data class Error(val message: String) : Placement()
    }
}