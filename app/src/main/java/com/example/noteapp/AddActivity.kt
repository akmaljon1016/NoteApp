package com.example.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.noteapp.databinding.ActivityAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    lateinit var rootReference: DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        rootReference = database.reference.child("users").child(auth.uid.toString())
        binding.btnSave.setOnClickListener {
            val title: String = binding.edtitle.text.toString()
            val description = binding.edDescription.text.toString()

            val newNoteRef: DatabaseReference = rootReference.push()
            val note: HashMap<String, String> = HashMap()
            note.put("title", title)
            note.put("description", description)
            newNoteRef.setValue(note).addOnCompleteListener {
                if (it.isSuccessful) {
                    finish()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}