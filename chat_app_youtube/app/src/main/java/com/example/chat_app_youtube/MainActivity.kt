package com.example.chat_app_youtube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            val email = email_text_register.text.toString()
            val password = password_text_register.text.toString()

            Log.d("MainActivity", "email is: " + email)
            Log.d("MainActivity", "password: $password")
        }

        alreadyHave_text_registration.setOnClickListener {
            Log.d("MainActivity","try to show login acticity")

            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
