package com.example.chat_app_youtube

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

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

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity", "Photo was selected")
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_imageview_register.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //selectPhoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun performRegister(){
        val email = email_text_register.text.toString()
        val password = password_text_register.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegisterActivity", "ERROR")

                uploadImageToFirebaseStorage()


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

    private fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener{
                Log.d("Register", "Image chosen ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    //it.toString()
                    Log.d("Register Act", "file loc:$it ")

                    saveUserToFirebaseDatabase(it.toString())
                }

            }
            .addOnFailureListener {

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){

        val uid = FirebaseAuth.getInstance().uid ?: " "
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, userName_text_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("register activity", "saved user to database")
            }
    }

}

class User(val uid: String, val username: String, val profileImageUrl: String)