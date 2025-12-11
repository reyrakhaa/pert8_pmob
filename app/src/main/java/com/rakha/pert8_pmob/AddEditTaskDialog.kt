package com.rakha.pert8_pmob

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.rakha.pert8_pmob.databinding.DialogAddEditTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditTaskDialog(
    private val context: Context,
    private val tasksRef: DatabaseReference,
    private val onSaved: (() -> Unit)? = null
) {
    fun show(existing: Task? = null, nodeKey: String? = null) {
        val dialogBinding = DialogAddEditTaskBinding.inflate(LayoutInflater.from(context))

        existing?.let {
            dialogBinding.editTextTitleTask.setText(it.title)
            dialogBinding.editDeskTask.setText(it.description)
            dialogBinding.editTextRelease.setText(it.release)
        }

        dialogBinding.editTextRelease.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dp = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    dialogBinding.editTextRelease.setText(dateFormat.format(selectedCalendar.time))
                },
                year, month, day
            )
            val todayCalendar = Calendar.getInstance()
            dp.datePicker.minDate = todayCalendar.timeInMillis
            dp.show()
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(if (existing == null) "Tambah Tugas Baru" else "Edit Tugas")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { dlg, _ ->
                val title = dialogBinding.editTextTitleTask.text.toString().trim()
                val desc = dialogBinding.editDeskTask.text.toString().trim()
                val release = dialogBinding.editTextRelease.text.toString().trim()

                if (title.isEmpty() || release.isEmpty()) {
                    Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                } else {
                    if (nodeKey == null) {
                        val node = tasksRef.push()
                        val newTask = Task(title = title, release = release, description = desc)
                        node.setValue(newTask).addOnSuccessListener {
                            Toast.makeText(context, "Tugas ditambah", Toast.LENGTH_SHORT).show()
                            onSaved?.invoke()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val updated = Task(title = title, release = release, description = desc)
                        tasksRef.child(nodeKey).setValue(updated).addOnSuccessListener {
                            Toast.makeText(context, "Tugas diperbarui", Toast.LENGTH_SHORT).show()
                            onSaved?.invoke()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}