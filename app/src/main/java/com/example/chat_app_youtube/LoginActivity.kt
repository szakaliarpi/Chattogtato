package com.example.chat_app_youtube

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

import android.util.Log
import android.content.Intent

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            performLogin()
        }

        goToRegister_text_login.setOnClickListener {
            Log.d("LoginActivity", "Try to show register activity")

            //launch the register activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){
        val email = email_text_login.text.toString()
        val password = password_text_login.text.toString()

        Log.d("LoginActivity", "Attempt login with email/pw: $email/***")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(){
                if (!it.isSuccessful){
                    Log.d("LoginActivity", "ERROR")
                }

            }
    }
}