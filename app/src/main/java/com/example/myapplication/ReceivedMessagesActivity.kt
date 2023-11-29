package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ReceivedMessagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagesList: MutableList<Message>
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_received_messages)

        recyclerView = findViewById(R.id.recyclerViewReceivedMessages)
        messagesList = mutableListOf()
        messageAdapter = MessageAdapter(messagesList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = messageAdapter

        currentUser = FirebaseAuth.getInstance().currentUser!!

        loadReceivedMessages()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // 뒤로가기 버튼 클릭 시 기존의 뒤로가기 동작 수행
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun loadReceivedMessages() {
        val messagesRef = FirebaseDatabase.getInstance().getReference("messages")
        val userId = intent.getStringExtra("userId")

        // 로그 추가: userId가 어떤 값으로 설정되는지 확인
        Log.d("ReceivedMessagesActivity", "userId: $userId")

        userId?.let {
            messagesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messagesList.clear()

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)
                        if (message != null && message.receiverId == userId) {
                            messagesList.add(message)
                        }
                    }

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        } ?: run {
            // userId가 null인 경우 처리
            Log.e("ReceivedMessagesActivity", "userId is null")
        }
    }

}
