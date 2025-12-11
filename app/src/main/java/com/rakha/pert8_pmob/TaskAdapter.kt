package com.rakha.pert8_pmob

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rakha.pert8_pmob.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onDelete: (Task, Int) -> Unit,
    private val onEdit: (Task, Int) -> Unit,
    private val onToggleDone: (Task, Int, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun updateList(newTasks: List<Task>) {
        val sortedTasks = newTasks.sortedWith(compareBy { it.done })
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task, position: Int) {

            binding.tvTitle.text = task.title
            binding.tvDesc.text = task.description ?: ""
            binding.tvRelease.text = task.release ?: ""

            val isDone = task.done

            binding.root.alpha = if (isDone) 0.5f else 1f

            binding.tvTitle.paint.isStrikeThruText = isDone

            val gray = Color.parseColor("#9E9E9E")
            val normalTitle = Color.parseColor("#1A1A1A")
            val normalDesc = Color.parseColor("#666666")
            val normalDate = Color.parseColor("#673AB7")

            if (isDone) {
                binding.tvTitle.setTextColor(gray)
                binding.tvDesc.setTextColor(gray)
                binding.tvRelease.setTextColor(gray)

                binding.tvRelease.setBackgroundResource(R.drawable.date_bg)
                binding.tvRelease.alpha = 0.5f
            } else {
                binding.tvTitle.setTextColor(normalTitle)
                binding.tvDesc.setTextColor(normalDesc)
                binding.tvRelease.setTextColor(normalDate)

                binding.tvRelease.setBackgroundResource(R.drawable.date_bg)
                binding.tvRelease.alpha = 1f
            }

            // Checkbox
            binding.checkDone.setOnCheckedChangeListener(null)
            binding.checkDone.isChecked = isDone

            binding.checkDone.setOnCheckedChangeListener { _, isChecked ->
                task.done = isChecked
                onToggleDone(task, position, isChecked)

                notifyItemChanged(position)
            }

            // Delete
            binding.btnDelete.setOnClickListener { onDelete(task, position) }

            // Edit
            binding.root.setOnClickListener { onEdit(task, position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], position)
    }

    override fun getItemCount() = tasks.size
}