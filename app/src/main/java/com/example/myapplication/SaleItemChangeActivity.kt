package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SaleItemChangeActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_change)
        val priceTextView: TextView = findViewById(R.id.changePrice)
        val sellingSwitch: Switch = findViewById(R.id.changeSelling)
        val saleItem: SaleItem? = intent.getParcelableExtra("saleItem")
        val changebutton: Button =findViewById(R.id.change)
        databaseReference = FirebaseDatabase.getInstance().getReference("saleItems")
        changebutton.setOnClickListener{
        if(saleItem!=null){
            val query = databaseReference.orderByChild("title").equalTo(saleItem.title.toString())
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (itemSnapshot in dataSnapshot.children) {
                        // SaleItem 객체 가져오기
                        val saleItem = itemSnapshot.getValue(SaleItem::class.java)

                        // SaleItem의 고유한 키 가져오기
                        val saleItemKey = itemSnapshot.key

                        // SaleItem의 키를 사용하여 원하는 작업 수행
                        if (saleItemKey != null) {
                            if(saleItem!=null) {
                                val priceUpdateTask = databaseReference.child(saleItemKey).child("price").setValue(priceTextView.text.toString())
                                val sellingUpdateTask = if (sellingSwitch.isChecked) {
                                    databaseReference.child(saleItemKey).child("selling").setValue(false)
                                } else {
                                    databaseReference.child(saleItemKey).child("selling").setValue(true)
                                }

                                // 모든 작업이 완료된 후 finish() 호출
                                Tasks.whenAll(priceUpdateTask, sellingUpdateTask)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            finish()
                                        } else {
                                            // 실패 처리
                                        }
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 에러 처리 코드
                }
            })
        }}
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


    }




