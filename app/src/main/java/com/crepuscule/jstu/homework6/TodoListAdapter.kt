package com.crepuscule.jstu.homework6

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

/**
 * List: 有序接口, 只能读取, 不能更改元素;
 * MutableList: 有序接口, 可以读写与更改, 删除, 增加元素.
 */

class TodoListAdapter(activity: MainActivity) : RecyclerView.Adapter<TodoListAdapter.TodoElementViewHolder>() {
    private var todoList = mutableListOf<TodoElement>()
    private val main_activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoElementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_todolist_element, parent, false)       // 内容横向铺满
        val viewHolder = TodoElementViewHolder(view)

        var checkboxView = view.findViewById<CheckBox>(R.id.task_status)

        checkboxView.setOnClickListener {
//            Log.d("checkbox Status", checkboxView.isChecked().toString())
//            Log.d("Debug Info", "checkbox status change button clicked")
            main_activity.changeTaskStatus(viewHolder.task_date.text.toString().substring(12), checkboxView)
        }

        // 为每一个 task viewholder 创建长按事件, 长按跳转到编辑页, 根据 checkbox 状态不同跳转不同的页面
        viewHolder.itemView.setOnLongClickListener() {
            if (checkboxView.isChecked) {
                val intent = Intent(main_activity, TaskPageShowActivity::class.java)
                intent.putExtra("task_date", viewHolder.task_date.text)
                main_activity.startActivity(intent)
                false
            }
            else {
                val intent = Intent(main_activity, TaskPageEditActivity::class.java)
                intent.putExtra("task_date", viewHolder.task_date.text)
                main_activity.startActivity(intent)
                false
            }
        }
        return viewHolder
    }
    
    // 为 view 设置 内容 与 checkbox 状态
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoElementViewHolder, idx: Int) {
        holder.task_date.text = "Startdate : ${todoList[idx].task_date}"
        holder.task_title.text = "Title : ${todoList[idx].task_title}"
        holder.task_status.isChecked = todoList[idx].task_status != "todo"
        holder.task_deadline.text = "Deadline : ${todoList[idx].task_deadline}"
    }

    override fun getItemCount(): Int = todoList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTodoList(myList : List<TodoElement>) {
        todoList.clear()
        todoList.addAll(myList)
//        todoList.sortWith(compareBy({it.task_status}, {it.task_deadline}))          // 将已完成放到一侧，未完成放到一侧
        notifyDataSetChanged()      // 当数据发生变化时，更新 view
    }

    // 每一个 view item 只显示 task title, task deadline, task status, task date, 而不显示 task details
    class TodoElementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val task_date : TextView = view.findViewById<TextView>(R.id.task_date)
        val task_title: TextView = view.findViewById<TextView>(R.id.task_title)
        val task_status: CheckBox = view.findViewById<CheckBox>(R.id.task_status)
        val task_deadline : TextView = view.findViewById<TextView>(R.id.task_deadline)
    }
}
