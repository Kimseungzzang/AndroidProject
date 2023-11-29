package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonSignUp.setOnClickListener {
            // 가입 화면으로 이동

            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            loginUser()
        }
    }

    // MainActivity.kt

// ...

    private fun loginUser() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid // 현재 사용자의 아이디 정보
                    val database = Firebase.database
                    val itemsRef = database.getReference("users")
                    itemsRef.child(userId.toString()).child("name").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // 데이터베이스에서 실제 데이터를 가져옴
                            val userName = dataSnapshot.getValue(String::class.java)

                            // 로그인 성공 시 다음 화면으로 전환
                            val intent = Intent(this@MainActivity, UserProfileActivity::class.java)
                            intent.putExtra("userId", userName)
                            startActivity(intent)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // 데이터를 가져오는 도중 에러 발생 시 실행할 코드
                        }
                    })
                } else {
                    Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

