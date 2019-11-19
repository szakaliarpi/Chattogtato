package com.example.chat_app_youtube

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.io.IOException
import java.util.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        externalFileStoragePath = this.getExternalFilesDir(null).toString() + "/images/"
        storageRef = FirebaseStorage.getInstance().getReference()
        selectPhoto_button_register.visibility = View.VISIBLE

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

    final var TAG: String = "RegisterActivity"
    var selectedPhotoUri: Uri? = null
    var externalFileStoragePath: String = ""
    var storageRef: StorageReference? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was...
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotoUri = data.data
            saveToExternalStorage(selectedPhotoUri.toString())

            val imgView = selectphoto_imageview_register as ImageView

            imgView.setImageURI(Uri.parse(selectedPhotoUri.toString()))

//            bitmap thing
//            val bitmap = BitmapFactory.decodeFile(selectedPhotoUri.toString())
//            //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//            selectphoto_imageview_register.setImageBitmap(bitmap)
//
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto_imageview_register.setBackgroundDrawable(bitmapDrawable)
//            selectphoto_imageview_register.visibility = View.VISIBLE
            selectPhoto_button_register.visibility = View.GONE

            //selectphoto_imageview_register.alpha = 0f

//            val bitmap = BitmapFactory.decodeFile(selectedPhotoUri.toString())
//            // Load image using Glide very useful
//            Glide.with(this)
//                .load(Uri.fromFile(File(selectedPhotoUri.toString())))
//                .circleCrop()
//                .error(R.drawable.ic_error_black_24dp)
//                .into(this.findViewById(R.id.selectphoto_imageview_register))

        }
    }
    private fun performRegister(){
        val email = email_text_register.text.toString()
        val password = password_text_register.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Log.d("RegisterActivity", "Email or password is empty")
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("RegisterActivity", "Successfully created user with uid: ${it.result?.user?.uid}")
                Toast.makeText(this, "Successfully created user", Toast.LENGTH_SHORT).show()

                uploadImageToFirebaseStorage()
            }

            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }

    }

    private fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri == null) return

        //random id to image
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener{
                Log.d("RegisterActivity", "Successfully uploaded image ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    //it.toString()
                    Log.d("RegisterActivity", "File location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }

            .addOnFailureListener{
                Log.e(TAG,"Cannot upload file to Firebase Storage:" + it.message)
            }

            .addOnCanceledListener {
                Log.e(TAG,"File upload cancelled")
            }
    }



    private fun saveUserToFirebaseDatabase(profileImageUrl: String){

        val uid = FirebaseAuth.getInstance().uid ?: " "
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, userName_text_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener{
                /*Log.d("register activity", "saved user to database")
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)*/
            }

            .addOnFailureListener(){
                Log.d("RegisterActivity", "Failed to save data in Firebase database")
            }
    }

    private fun saveToExternalStorage(filePath: String){
        var fromFile = File(filePath)
        var toFileName = "picture"
        var toFile = File(externalFileStoragePath)
        toFile.mkdirs()

        try {
            fromFile.copyTo(toFile,true)
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.MediaColumns.DATA, toFile.toString() + "/" + toFileName)
            this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }




}

class User(val uid: String, val username: String, val profileImageUrl: String)