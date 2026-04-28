package com.example.e_commerce_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import com.example.e_commerce_app.databinding.ActivitySignupBinding
import com.example.e_commerce_app.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        applyEdgeInsets(R.id.signupRoot)

        val nameField = binding.signupName
        val emailField = binding.signupEmail
        val passField = binding.signupPassword
        val confirmField = binding.signupConfirm
        val signupBtn = binding.signupButton
        val backBtn = binding.signupBack
        val toLogin = binding.goToLogin

        backBtn.setOnClickListener { finish() }
        toLogin.setOnClickListener { finish() }

        signupBtn.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passField.text.toString()
            val confirm = confirmField.text.toString()

            if (name.isEmpty()) {
                nameField.error = getString(R.string.error_name_required); return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.error = getString(R.string.error_email_invalid); return@setOnClickListener
            }
            if (password.length < 6) {
                passField.error = getString(R.string.error_password_short); return@setOnClickListener
            }
            if (password != confirm) {
                confirmField.error = getString(R.string.error_passwords_mismatch); return@setOnClickListener
            }
            viewModel.signUp(name, email, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    if (event is AuthViewModel.Event.SignedUp) {
                        Toast.makeText(this@SignupActivity, R.string.msg_signup_success, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignupActivity, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
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
}