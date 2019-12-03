package com.example.chat_app_youtube.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chat_app_youtube.R
import com.example.chat_app_youtube.models.ChatMessage
import com.example.chat_app_youtube.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.textView
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatLogActivity : AppCompatActivity() {

    //static constant
    companion object{
        val TAG = "chatlog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter // allows adding object inside the adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) //set username
        supportActionBar?.title = user.username

        //setupDummyData()
        listenForMessages()

        send_button_chat_log.setOnClickListener{
            Log.d(TAG, "attempt to send messages..")
            performSendMessage()
        }
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text)) //putting the messages onto the blanks
                    }else{
                        adapter.add(ChatToItem(chatMessage.text)) // text on the right side as well

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
            })
    }


    private fun performSendMessage(){
        //send message to firebase
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if(fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved message: ${reference.key}")
            }
    }

    private fun setupDummyData(){
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem("from messageeeeeeeeeee"))
        adapter.add(ChatToItem("to messaggeeeeeeeeeeeee\ntomessss"))

        recyclerview_chat_log.adapter = adapter
    }
}
    //message blanks, left side
    class ChatFromItem(val text: String): Item<ViewHolder>(){ //making this blank more dynamic with "text"
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //access the actual ID with text view
        viewHolder.itemView.textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
//right side of the activity
class ChatToItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
