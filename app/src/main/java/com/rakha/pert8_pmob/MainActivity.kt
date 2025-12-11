package com.rakha.pert8_pmob

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rakha.pert8_pmob.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tasksRef: DatabaseReference
    private lateinit var adapter: TaskAdapter
    private val keys = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tasksRef = FirebaseDatabase.getInstance().getReference("tasks")

        adapter = TaskAdapter(mutableListOf(),
            onDelete = { task, pos ->
                val key = keys.getOrNull(pos)
                if (key != null) {
                    tasksRef.child(key).removeValue().addOnSuccessListener {
                        Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onEdit = { task, pos ->
                val key = keys.getOrNull(pos)
                AddEditTaskDialog(this, tasksRef) .show(existing = task, nodeKey = key)
            },
            onToggleDone = { task, pos, isDone ->
                val key = keys.getOrNull(pos)
                if (key != null) {
                    tasksRef.child(key)
                        .child("done")
                        .setValue(isDone)
                        .addOnSuccessListener {
                        }
                }
            }
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter

        binding.fabAddTasks.setOnClickListener {
            AddEditTaskDialog(this, tasksRef) {
            }.show()
        }

        fetchData()
    }

    private fun fetchData() {
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Task>()
                keys.clear()
                for (child in snapshot.children) {
                    val b = child.getValue(Task::class.java)
                    if (b != null) {
                        list.add(b)
                        keys.add(child.key ?: "")
                    }
                }
                adapter.updateList(list)
                binding.emptyState.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}