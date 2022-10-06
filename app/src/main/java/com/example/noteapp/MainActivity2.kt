package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import com.example.noteapp.databinding.ActivityMain2Binding
import com.example.noteapp.databinding.NoteLayoutBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity2 : AppCompatActivity() {
    lateinit var database: FirebaseDatabase
    lateinit var rootReference: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        rootReference = database.reference.child("users").child(auth.uid.toString())
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
        val option: FirebaseRecyclerOptions<Note> =
            FirebaseRecyclerOptions.Builder<Note>().setQuery(rootReference, Note::class.java)
                .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Note, NoteViewHolder>(option) {

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                    return NoteViewHolder(NoteLayoutBinding.inflate(layoutInflater))
                }

                override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: Note) {
                    var noteId: String = getRef(position).key.toString()
                    rootReference.child(noteId).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
//                            holder.binding.txtTitle.text = snapshot.child("title").value.toString()
//                            holder.binding.txtDescription.text =
//                                snapshot.child("description").value.toString()
                            Toast.makeText(
                                this@MainActivity2,
                                snapshot.child("title").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }
            }
        binding.recyclerview.adapter = firebaseRecyclerAdapter
        binding.recyclerview.setHasFixedSize(true)
        firebaseRecyclerAdapter.startListening()

    }
}