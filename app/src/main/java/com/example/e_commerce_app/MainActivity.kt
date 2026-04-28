package com.example.e_commerce_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commerce_app.data.repository.AuthRepository
import com.example.e_commerce_app.ui.auth.LoginActivity
import com.example.e_commerce_app.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var auth: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val next = if (auth.isLoggedIn()) HomeActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, next))
        finish()
    }
}