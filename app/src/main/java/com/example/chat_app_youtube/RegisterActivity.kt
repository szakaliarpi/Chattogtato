package com.example.chat_app_youtube

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
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

        selectPhoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity", "Photo was selected")
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            selectPhoto_button_register.setBackgroundDrawable(bitmapDrawable)
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