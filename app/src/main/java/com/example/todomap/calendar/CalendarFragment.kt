package com.example.todomap.calendar

import android.R
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todomap.MainActivity
import com.example.todomap.databinding.FragmentCalendarBinding
import com.example.todomap.retrofit.model.TodoCreate
import com.example.todomap.retrofit.model.TodoEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment() {

    companion object {
        private const val TAG: String = "CalendarFragment"
    }

    private lateinit var binding : FragmentCalendarBinding
    private val todoViewModel: TodoViewModel by viewModels()
    private lateinit var adapter: TodoListAdapter
    private lateinit var friendsAdapter: FriendsAdapter

    private var flag = MutableLiveData<Boolean>()
    private var tempList: List<TodoEntity> = arrayListOf()

    private lateinit var firebaseAuth: FirebaseAuth

    private val mainActivity : MainActivity
        get() {
            return activity as MainActivity
        }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid.toString()

        val recyclerView = binding.todoRecyclerView
        setRecyclerView(recyclerView)
        val friendRecyclerView = binding.friendRecyclerView
        setFriendRecyclerView(friendRecyclerView)

        Log.d("Friends", "Calendar ${friendsAdapter.itemCount}")

        //date LiveDate ?????? ??????
        todoViewModel.date.observe(viewLifecycleOwner) {
            Log.d(TAG, it.toString())
            lifecycleScope.launch {
                todoViewModel.getAllByDateLive(uid, it.toString()).observe(viewLifecycleOwner) { todoList ->
                    if (todoList != null) {
                        // Adapter ????????? ??????
                        adapter.setTodoList(todoList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        flag.observe(viewLifecycleOwner) {
            if(it == false) {
                adapter.setTodoList(tempList)
                adapter.notifyDataSetChanged()
                flag.postValue(true)
            }
        }

        binding.calendarView.setOnDateChangeListener{ _, year, month,dayOfMonth ->
            val dateStr = "$year-${month+1}-$dayOfMonth"
            todoViewModel.updateDate(dateStr)
        }

        // ?????? ??????
        var time = ""
        binding.setTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = OnTimeSetListener { view, hourOfDay, minute ->
                time = "${hourOfDay}:${minute}"
                Log.d(TAG, "time picker: $time")
                // calendar object ??? ????????? ??????
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            }
            TimePickerDialog(context, R.style.Theme_Material_Light_Dialog_NoActionBar,
                timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
        }

        //?????? ??????
        val locLatitude = 0.0
        val locLongitude = 0.0
        val locationName = ""
        binding.setLocaBtn.setOnClickListener {
            mainActivity.changeFragment(6)
        }

        binding.todoAddBtn.setOnClickListener {
            val description = binding.todoEditText.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                val date = todoViewModel.date.value!!
                todoViewModel.insert(TodoCreate(uid, date, time, locationName, locLatitude , locLongitude , description))
                tempList = todoViewModel.getAllByDate(uid, date)
                flag.postValue(false)
            }
        }
        return binding.root
    }

    private fun setRecyclerView(recyclerView: RecyclerView){
        val dateOfToday = getTodayOfDate()
        adapter = TodoListAdapter(todoViewModel, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        todoViewModel.updateDate(dateOfToday)
    }

    private fun setFriendRecyclerView(recyclerView: RecyclerView){
        friendsAdapter = FriendsAdapter(this)
        friendsAdapter.getFriendList()
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = friendsAdapter
    }

    private fun getTodayOfDate(): String {
        //?????? ??????
        val dateOfTodayLong = binding.calendarView.date
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(dateOfTodayLong)
    }
}