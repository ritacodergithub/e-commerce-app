package com.example.e_commerce_app.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.data.repository.OrderRepository
import com.example.e_commerce_app.domain.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    orderRepo: OrderRepository
) : ViewModel() {

    val orders: StateFlow<List<Order>> = orderRepo.observeOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}