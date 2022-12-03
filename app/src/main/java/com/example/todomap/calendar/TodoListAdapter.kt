package com.example.todomap.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todomap.databinding.TodoitemRecyclerBinding
import com.example.todomap.retrofit.model.TodoEntity

class TodoListAdapter: RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {
    private lateinit var todoList: ArrayList<TodoEntity>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TodoitemRecyclerBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setTodoListUI(todoList[position])
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun setTodoList(todo: List<TodoEntity>?){
        if (todo != null){
            todoList.clear()
            todoList.addAll(todo)
            println(todoList)
        } else {
            todoList.clear()
        }
    }

    inner class ViewHolder(private val binding: TodoitemRecyclerBinding) : RecyclerView.ViewHolder(binding.root){
        fun setTodoListUI(todo: TodoEntity){
            binding.todoDescription.text = todo.description
        }
    }
}