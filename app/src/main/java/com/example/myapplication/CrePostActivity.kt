package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CrePostActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_form)

        auth = FirebaseAuth.getInstance()

        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)
        priceEditText = findViewById(R.id.editTextPrice)
        submitButton = findViewById(R.id.buttonSubmit)

        // Firebase Realtime Database 레퍼런스 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("saleItems")

        submitButton.setOnClickListener {
            val currentUserId = auth.currentUser?.uid
            val userId = intent.getStringExtra("userId")
            if (userId != null) {
                currentUserId?.let { submitSaleItem(userId, it) }
            } else {
                // userId가 null이면 예외 처리 또는 기본값 설정 등을 수행할 수 있습니다.
                Toast.makeText(this, "사용자 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }


    }



    private fun submitSaleItem(userId: String,currentUserId:String) {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()

        if (title.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty()) {
            // 새로운 SaleItem 객체 생성
            val newSaleItem = SaleItem(title, description, price, userId, true, currentUserId)


            // Firebase Realtime Database에 새로운 데이터 추가
            val newItemReference = databaseReference.push()
            newItemReference.setValue(newSaleItem)
                .addOnSuccessListener {
                    // 데이터 추가 성공
                    Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    finish() // 현재 액티비티 종료
                }
                .addOnFailureListener {
                    // 데이터 추가 실패
                    Toast.makeText(this, "게시글 등록 실패", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }
}

