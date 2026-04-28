package com.example.e_commerce_app.ui.auth

import androidx.lifecycle.ViewModel
import com.example.e_commerce_app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AuthRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun preFilledEmail(): String = auth.userEmail()
    fun userName(): String = auth.userName()
    fun isLoggedIn(): Boolean = auth.isLoggedIn()

    fun login(email: String, password: String) {
        if (auth.login(email, password)) _events.tryEmit(Event.LoggedIn)
        else _events.tryEmit(Event.LoginFailed)
    }

    fun signUp(name: String, email: String, password: String) {
        auth.signUp(name, email, password)
        _events.tryEmit(Event.SignedUp)
    }

    fun logout() = auth.logout()

    sealed class Event {
        data object LoggedIn : Event()
        data object SignedUp : Event()
        data object LoginFailed : Event()
    }
}