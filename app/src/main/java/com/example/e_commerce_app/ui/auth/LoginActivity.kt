package com.example.e_commerce_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
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
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ActivityLoginBinding
import com.example.e_commerce_app.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        applyEdgeInsets(R.id.loginRoot)

        var passwordField = binding.loginPassword
        var emailField = binding.loginEmail
        var loginBtn = binding.loginButton
        var toSignup = binding.goToSignup

        emailField.setText(viewModel.preFilledEmail())

        loginBtn.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.error = getString(R.string.error_email_invalid); return@setOnClickListener
            }
            if (password.length < 6) {
                passwordField.error = getString(R.string.error_password_short); return@setOnClickListener
            }
            viewModel.login(email, password)
        }

        toSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        AuthViewModel.Event.LoggedIn -> goHome()
                        AuthViewModel.Event.LoginFailed ->
                            Toast.makeText(this@LoginActivity, R.string.error_login_failed, Toast.LENGTH_SHORT).show()
                        AuthViewModel.Event.SignedUp -> goHome()
                    }
                }
            }
        }
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun applyEdgeInsets(rootId: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootId)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}