package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class SaleItemDetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_detail)
        val titleTextView: TextView = findViewById(R.id.textViewTitle)
        val descriptionTextView: TextView = findViewById(R.id.textViewDescription)
        val priceTextView: TextView = findViewById(R.id.textViewPrice)
        val sellerTextView: TextView = findViewById(R.id.textViewSeller)
        val sellingTextView: TextView = findViewById(R.id.textViewSelling)
        // SaleItemDetailActivity의 onCreate 메서드
        val saleItem: SaleItem? = intent.getParcelableExtra("saleItem")
        if (saleItem != null) {
            Log.d("SaleItemDetailActivity", "SellerId: ${saleItem.userid}")
        }

        if (saleItem != null) {
            // Update UI with saleItem details
            titleTextView.text = saleItem.title
            descriptionTextView.text = saleItem.description
            priceTextView.text = saleItem.price
            sellerTextView.text = saleItem.userid
            if (saleItem.selling) {
                sellingTextView.text = "판매중"
            } else {
                sellingTextView.text = "판매완료"
            }
        }

        val messageButton: Button = findViewById(R.id.button)
        messageButton.setOnClickListener {
            // 클릭 시 메시지 보내는 화면으로 전환
            val intent = Intent(this, SendMessageActivity::class.java)
            intent.putExtra("sellerId", saleItem?.userid)
            startActivity(intent)
        }

    }
}
