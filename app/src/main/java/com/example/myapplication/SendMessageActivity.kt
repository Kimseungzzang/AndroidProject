package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class SendMessageActivity : AppCompatActivity() {

    private lateinit var sellerId: String
    private lateinit var messageEditText: EditText
    private lateinit var currentUser: FirebaseUser
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        // SendMessageActivity의 onCreate 메서드
        sellerId = intent.getStringExtra("sellerId") ?: ""
        Log.d("SendMessageActivity", "SellerId: $sellerId")

        messageEditText = findViewById(R.id.editTextMessage)
        val sendButton: Button = findViewById(R.id.buttonSend)

        currentUser = FirebaseAuth.getInstance().currentUser!!
        usersRef = FirebaseDatabase.getInstance().getReference("users")

        sendButton.setOnClickListener {
            sendMessage()
        }
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



    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()

        if (messageText.isNotEmpty()) {
            // 현재 사용자의 이름 가져오기
            usersRef.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java)

                    if (userName != null) {
                        val messagesRef = FirebaseDatabase.getInstance().getReference("messages")

                        // 메시지 데이터베이스에 새로운 메시지 추가
                        val messageKey = messagesRef.push().key
                        val message = Message(userName, sellerId, messageText)

                        if (messageKey != null) {
                            messagesRef.child(messageKey).setValue(message)

                            // 메시지 전송 후 필요한 추가 작업 수행
                            // 예: UI 업데이트, 사용자 피드백 등

                            // 전송이 완료되면 액티비티를 종료하여 이전 화면으로 돌아갑니다.
                            finish()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터를 가져오는 도중 에러 발생 시 실행할 코드
                }
            })
        }
    }

}
