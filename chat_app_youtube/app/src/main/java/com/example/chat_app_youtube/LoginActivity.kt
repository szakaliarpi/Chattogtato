package com.example.chat_app_youtube

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val email = email_text_login.text.toString()
            val password = password_text_login.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")
        }

        backToRegister_text_login.setOnClickListener {
            finish()
        }
    }
}