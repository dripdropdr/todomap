package com.example.todomap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todomap.databinding.FragmentCalendarBinding
import com.example.todomap.retrofit.model.TodoCreate
import com.example.todomap.retrofit.model.TodoEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment(private var uid: String) : Fragment() {


    private lateinit var binding : FragmentCalendarBinding
    private val todoViewModel: TodoViewModel by viewModels()
    private lateinit var adapter: TodoListAdapter

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        val recyclerView = binding.todoRecyclerView
        setRecyclerView(recyclerView)

        //date LiveDate 변경 감지
        todoViewModel.date.observe(viewLifecycleOwner) {
            Log.d("date", it.toString())
            todoViewModel.getAllByDate(uid, it.toString()).observe(viewLifecycleOwner) { todoList ->
                if (todoList != null) {
                    // Adapter 데이터 갱신
                    adapter.setTodoList(todoList)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.calendarView.setOnDateChangeListener{ _, year, month,dayOfMonth ->
            val dateStr = "$year-${month+1}-$dayOfMonth"
            todoViewModel.updateDate(dateStr)
        }

        binding.todoAddBtn.setOnClickListener {
            val description = binding.todoEditText.text.toString()
            // time
            // location
            GlobalScope.launch {
                val date = todoViewModel.date.value!!
                todoViewModel.insert(TodoCreate(uid, date, time, locLatitude , locLongitude , description))
            }
        }

        return binding.root
    }

    private fun setRecyclerView(recyclerView: RecyclerView){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        val dateOfToday = getTodayOfDate()
        todoViewModel.updateDate(dateOfToday)
    }

    private fun getTodayOfDate(): String {
        //오늘 날짜
        val dateOfTodayLong = binding.calendarView.date
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(dateOfTodayLong)
    }

}