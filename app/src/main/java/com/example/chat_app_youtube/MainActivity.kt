package com.example.chat_app_youtube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegister()
        }

        alreadyHave_text_registration.setOnClickListener {
            Log.d("MainActivity", "try to show login acticity")

            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        val email = email_text_register.text.toString()
        val password = password_text_register.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }



        Log.d("MainActivity", "email is: " + email)
        Log.d("MainActivity", "password: $password")

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

            .addOnCompleteListener {
                if (!it.isSuccessful){
                    Log.d("Main", "ERROR")
                    return@addOnCompleteListener
                }

                val result = it.result
                if(result != null){
                    val user = result.user
                    if(user != null){
                        val uid = user.uid
                        if(uid != null) {
                            Log.d("Main", "created: $uid")
                            Toast.makeText(this, "Account created. ${it.toString()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            .addOnFailureListener {
                Log.d("Main", "Fail to create user: ${it.message}")
                Toast.makeText(this, "Incorrect email or password: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
