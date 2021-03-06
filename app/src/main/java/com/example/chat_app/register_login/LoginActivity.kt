package com.example.chat_app.register_login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chat_app.R
import com.example.chat_app.messages.LatestMessagesActivity
import com.example.chat_app.messages.NewMessageActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        requestAllPermissions()

        login_button_login.setOnClickListener {
            performLogin()
//            val intent = Intent(this, LatestMessagesActivity::class.java)
//            startActivity(intent)
        }

        goToRegister_textView_login.setOnClickListener {
            Log.d("LoginActivity", "Try to show register activity")

            //launch the register activity
            val intent2 = Intent(this, RegisterActivity::class.java)
            startActivity(intent2)
        }
    }

    private fun performLogin(){
        val email = email_textBox_login.text.toString()
        val password = password_textBox_login.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Log.d("LoginActivity", "Email or password is empty.")
            Toast.makeText(this, "Email or password is empty", Toast.LENGTH_SHORT).show()
            return
        }


        Log.d("LoginActivity", "Attempt login with email/pw: $email/***")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(){
                if (!it.isSuccessful){
                    Log.d("LoginActivity", "Couldn't login successfully")
                    Toast.makeText(this, "Couldn't login successfully", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                else{
                    finish()
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    startActivity(intent)

                    Log.d("LoginActivity", "Login successfully")
                    Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
                }


            }

            .addOnFailureListener(){
                Log.d("LoginActivity", "Failed to login")
                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun requestAllPermissions(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
    }
}