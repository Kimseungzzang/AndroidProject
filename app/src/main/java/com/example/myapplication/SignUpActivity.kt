// SignUpActivity.kt

package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()

        binding.buttonSignUpSubmit.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        val email = binding.editTextEmailSignUp.text.toString().trim()
        val password = binding.editTextPasswordSignUp.text.toString().trim()
        val name = binding.editTextName.text.toString().trim()
        val birthday = binding.editTextBirthday.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공
                    Toast.makeText(baseContext, "가입 성공", Toast.LENGTH_SHORT).show()

                    // Firebase Database에 추가 정보 저장
                    val userId = auth.currentUser?.uid
                    val database = Firebase.database
                    val userRef = database.getReference("users").child(userId.orEmpty())

                    userRef.child("name").setValue(name)
                    userRef.child("birthday").setValue(birthday)

                    finish() // 현재 화면 종료
                } else {
                    // 회원가입 실패
                    Toast.makeText(baseContext, "가입 실패", Toast.LENGTH_SHORT).show()
                    Log.e("SignUpActivity", "가입 실패", task.exception)
                }
            }
    }

}
