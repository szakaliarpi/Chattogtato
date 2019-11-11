package com.example.chat_app_youtube

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

import android.util.Log
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        alreadyHave_text_registration.setOnClickListener {
            Log.d("RegisterActivity", "Close registration activity")

            finish()
        }
    }

    private fun performRegister(){
        val email = email_text_register.text.toString()
        val password = password_text_register.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }



        Log.d("RegisterActivity", "email is: " + email)
        Log.d("RegisterActivity", "password: $password")

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

            .addOnCompleteListener {
                if (!it.isSuccessful){
                    Log.d("RegisterActivity", "ERROR")
                    return@addOnCompleteListener
                }

                val result = it.result
                if(result != null){
                    val user = result.user
                    if(user != null){
                        val uid = user.uid
                        if(uid != null) {
                            Log.d("RegisterActivity", "created: $uid")
                            Toast.makeText(this, "Account created. ${it.toString()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            .addOnFailureListener {
                Log.d("RegisterActivity", "Fail to create user: ${it.message}")
                Toast.makeText(this, "Incorrect email or password: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}