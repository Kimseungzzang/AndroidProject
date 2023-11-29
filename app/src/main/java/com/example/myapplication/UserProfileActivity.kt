// UserProfileActivity.kt

package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class SaleItem(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val userid: String? = "",
    val selling: Boolean = true,
    var currentUserUid: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(price)
        parcel.writeString(userid)
        parcel.writeByte(if (selling) 1 else 0)
        parcel.writeString(currentUserUid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SaleItem> {
        override fun createFromParcel(parcel: Parcel): SaleItem {
            return SaleItem(parcel)
        }

        override fun newArray(size: Int): Array<SaleItem?> {
            return Array(size) { null }
        }
    }
}
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var saleItemAdapter: SaleItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("userId")
        // Firebase Realtime Database 레퍼런스 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("saleItems")
        var checkSelling  : Switch = findViewById(R.id.checkSelling)
        // SaleItemAdapter 초기화

        saleItemAdapter = SaleItemAdapter(emptyList()){ selectedItem ->
            // 아이템 클릭 시 호출되는 람다 함수
            showSaleItemDetail(selectedItem,userId)
        }
        if (userId != null) {
            binding.buttonShowMessages.setOnClickListener {
                val intent = Intent(this, ReceivedMessagesActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "사용자 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // RecyclerView 초기화
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSaleList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = saleItemAdapter
        // 어댑터 초기화 및 RecyclerView에 설정

        checkSelling.setOnCheckedChangeListener { _, isChecked ->
            // isChecked 값에 따라 판매 여부 업데이트
            if (isChecked) {
                // 스위치가 선택된 경우
                // 판매 중으로 설정
                saleItemAdapter.filterBySelling()
            } else {
                // 스위치가 선택되지 않은 경우
                // 판매 중이 아닌 상태로 설정
                saleItemAdapter.showAllItems()
            }
        }
        auth = FirebaseAuth.getInstance()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val saleItems = mutableListOf<SaleItem>()

                for (itemSnapshot in snapshot.children) {
                    val saleItem = itemSnapshot.getValue(SaleItem::class.java)
                    if (saleItem != null) {
                        saleItems.add(saleItem)
                    }
                }

                // SaleItemAdapter에 데이터 변경 알림
                saleItemAdapter.updateData(saleItems)
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터를 가져오는 도중 에러 발생 시 실행할 코드
            }
        })

        // userId를 사용하여 UI 업데이트 또는 필요한 작업 수행
        binding.textViewUserId.text = "사용자 아이디: $userId"

        binding.buttonLogout.setOnClickListener {
            logoutUser()
        }
        if (userId != null) {
            binding.post.setOnClickListener {
                val intent = Intent(this, CrePostActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "사용자 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        auth.signOut()

        // 로그아웃 후 로그인 화면으로 이동
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 화면 종료
    }
    private fun showSaleItemDetail(saleItem: SaleItem, userId: String?) {
        if (userId != null && saleItem.userid != userId) {
            // 다른 사용자의 게시물이라면 상세 페이지로 이동
            val intent = Intent(this, SaleItemDetailActivity::class.java)
            intent.putExtra("saleItem", saleItem)
            startActivity(intent)
        } else {
            // 현재 사용자의 게시물이라면 수정 페이지로 이동
            val currentUserUid = auth.currentUser?.uid
            val intent = Intent(this, SaleItemChangeActivity::class.java)
            val saleItemWithCurrentUserUid = saleItem.copy(currentUserUid = currentUserUid)
            intent.putExtra("saleItem", saleItemWithCurrentUserUid)
            startActivity(intent)
        }
    }


}
