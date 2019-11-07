package com.example.chat_app_youtube

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            performLogin()
        }

        backToRegister_text_login.setOnClickListener {
            finish()
        }
    }

    private fun performLogin(){
        val email = email_text_login.text.toString()
        val password = password_text_login.text.toString()

        Log.d("Login", "Attempt login with email/pw: $email/***")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(){
                if (!it.isSuccessful){
                    Log.d("'Login", "ERROR")
                }

            }
    }
}