package com.example.noteapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.noteapp.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var callBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var storedVerificationId: String? = null
    lateinit var preference: SharedPreferences
    lateinit var database: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var rootRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        userRef = database.reference.child("users")
        preference = getSharedPreferences("baza", MODE_PRIVATE)
        if (isUserCreated()) {
            startActivity(Intent(this, MainActivity2::class.java))
        }
        auth = FirebaseAuth.getInstance()
        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@MainActivity, p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = p0
                resendToken = p1
            }
        }

        binding.btnSend.setOnClickListener {
            val phoneNumber = binding.edNumber.text.toString()
            sendNumber(phoneNumber)
        }
        binding.btnSendOtp.setOnClickListener {
            val otp = binding.edotp.text.toString()
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)
            signInWithPhoneAuthCredential(credential)
        }
    }


    fun sendNumber(number: String) {
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }


    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this, MainActivity2::class.java))
                    saveIsUserLogged()
                } else {
                    Toast.makeText(this, "Verification code wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun saveIsUserLogged() {
        val myEdit = preference.edit()
        myEdit.putBoolean("isCreated", true)
        myEdit.apply()
    }

    fun isUserCreated(): Boolean {
        return preference.getBoolean("isCreated", false)
    }
}