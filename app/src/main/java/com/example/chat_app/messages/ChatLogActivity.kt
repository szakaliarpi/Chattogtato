package com.example.chat_app.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chat_app.R
import com.example.chat_app.models.ChatMessage
import com.example.chat_app.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.textView_chat_tofrom_row


class ChatLogActivity : AppCompatActivity() {

    //static constant
    companion object{
        val TAG = "chatlog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter // allows adding object inside the adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) //set username
        supportActionBar?.title = toUser?.username

        listenForMessages()

        send_button_chat_log.setOnClickListener{
            Log.d(TAG, "attempt to send messages..")
            performSendMessage()
        }
    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser ?: return

                        adapter.add(ChatFromItem(chatMessage.text, currentUser)) //putting the messages onto the blanks

                    }else{
                        adapter.add(ChatToItem(chatMessage.text, toUser!!)) // text on the right side as well
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

        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(
            reference.key!!,
            text,
            fromId,
            toId,
            System.currentTimeMillis() / 1000
        )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId") //creating latest messages into firebase
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId") //creating latest messages into firebase
        latestMessageToRef.setValue(chatMessage)
    }

}
    //message blanks, left side

class ChatFromItem(val text: String, val user:User): Item<ViewHolder>(){ //making this blank more dynamic with "text"
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //access the actual ID with text view
        viewHolder.itemView.textView_chat_tofrom_row.text = text
        //viewHolder.itemView.`@+id/textView_chat_to_row`.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
//right side of the activity
class ChatToItem(val text: String, val user:User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_tofrom_row.text = text
        //viewHolder.itemView.`@+id/textView_chat_to_row`.text = text

        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

