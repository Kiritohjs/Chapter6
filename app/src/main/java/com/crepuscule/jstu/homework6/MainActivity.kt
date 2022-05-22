package com.crepuscule.jstu.homework6

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// todo: 刚打开界面时会出现 recyclerview 无法滑动的清空
// todo: 添加删除功能
// todo: todo 和 done 各自占的空间仍然有问题

class MainActivity: AppCompatActivity() {
    private val addTask: FloatingActionButton by lazy { findViewById(R.id.addTask) }  // 添加待办项

    private lateinit var db: SQLiteDatabase
    private lateinit var todoAdapter : TodoListAdapter
    private lateinit var doneAdapter : TodoListAdapter
    private var curTodoTask = mutableListOf<TodoElement>()                      // 所有未完成的任务
    private var curDoneTask = mutableListOf<TodoElement>()                      // 所有已完成的任务
    private val dbHelper = DBHelper(this, "ToDoList_v8.db")       // 打开数据库
    private lateinit var finishMsgReceiver:MyBroadcastRecevier

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todolist)

        db = dbHelper.openDB()
        
        val todoRecyclerView = findViewById<RecyclerView>(R.id.todo_recycler_view)
        val todoTaskLayoutManager = LinearLayoutManagerWithMaxItem(this, 2)
        todoTaskLayoutManager.orientation = LinearLayoutManager.VERTICAL
        todoRecyclerView.layoutManager = todoTaskLayoutManager
        todoAdapter = TodoListAdapter(this)

        val doneRecyclerView = findViewById<RecyclerView>(R.id.done_recycler_view)
        val doneTaskLayoutManager = LinearLayoutManager(this)
        doneTaskLayoutManager.orientation = LinearLayoutManager.VERTICAL
        doneRecyclerView.layoutManager = doneTaskLayoutManager
        doneAdapter = TodoListAdapter(this)
        
        updateListFromDB()
//        Log.d("Debug Info", "UpdateList---------------")
//        Log.d("Debug Info", "Cur TodoTaks List\n$curTodoTask")

        todoAdapter.updateTodoList(curTodoTask)
        todoRecyclerView.adapter = todoAdapter
        doneAdapter.updateTodoList(curDoneTask)
        doneRecyclerView.adapter = doneAdapter

        // items 间增加间距
        todoRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                rect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(rect, view, parent, state)
                rect.bottom = 5
                rect.left = 0
                rect.right = 0
                rect.top = 0
            }
        })

        // 自定义分割线
//        var divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        ContextCompat.getDrawable(this, R.drawable.task_divider)?.let { divider.setDrawable(it) }
//        todoRecyclerView.addItemDecoration(divider)

        // 用户点击该按钮时跳转到添加新任务的页面
        addTask.setOnClickListener {
            val intent = Intent(this, TaskPageEditActivity::class.java)
            startActivity(intent)
        }

//        deleteSelect.setOnClickListener {
//            db.delete("todolist", "task_status = ?", arrayOf("done"))
//            updateListFromDB()
//            adapter.updateTodoList(curMap)
//        }
//
//        deleteAll.setOnClickListener {
//            db.execSQL("delete from todolist")
//            updateListFromDB()
//            adapter.updateTodoList(curMap)
//        }

        // 注册广播接收器用来接收结束当前页面的消息
        finishMsgReceiver = MyBroadcastRecevier()
        val intentFilter = IntentFilter()
        intentFilter.addAction("MAIN_FINISH")
        registerReceiver(finishMsgReceiver, intentFilter)
    }

    // 查询数据库 更新当前 list
    private fun updateListFromDB() {
        curTodoTask.clear()
        curDoneTask.clear()
        val taskCursor = (db?: dbHelper.writableDatabase).query("todolist", null, null, null, null, null, null, null)
        while (taskCursor.moveToNext()) {
            val task_title:String?  = taskCursor.getString(taskCursor.getColumnIndexOrThrow("task_title"))
            val task_status:String? = taskCursor.getString(taskCursor.getColumnIndexOrThrow("task_status"))
            val task_date:String? = taskCursor.getString(taskCursor.getColumnIndexOrThrow("task_date"))
            val task_deadline:String? = taskCursor.getString(taskCursor.getColumnIndexOrThrow("task_deadline"))
            val task_details :String? = taskCursor.getString(taskCursor.getColumnIndexOrThrow("task_details"))
            val taskElement : TodoElement = TodoElement(task_title, task_details, task_date, task_deadline, task_status)
            Log.d("DB task status", task_status.toString())
            if (task_status == "todo")
            {
                curTodoTask.add(taskElement)
            } else {
                curDoneTask.add(taskElement)
            }
        }
        taskCursor.close()
    }

    // 利用创建日期 (date) 即可唯一定位 task
    fun changeTaskStatus(tag : String, view: CheckBox){
        Log.d ("Task Date", tag)
        // 更新任务状态
        val status = if (view.isChecked) "done" else  "todo"
        val newValue = ContentValues().apply {
            put ("task_status", status)
        }

        Log.d("query result", dbHelper.queryTaskInfo(tag).toString())
        Log.d("Dst Value", status)
        db.update("todolist", newValue, "task_date = ?", arrayOf(tag))
        Log.d("query result", dbHelper.queryTaskInfo(tag).toString())
        updateListFromDB()

        // 更新 view item 内容, 更新显示
        todoAdapter.updateTodoList(curTodoTask)
        doneAdapter.updateTodoList(curDoneTask)
        Log.d("Debug Info", "Update List")
        Log.d("todo Task :", curTodoTask.toString())
        Log.d("done Task", curDoneTask.toString())
    }

    override fun onDestroy() {
        // 取消注册 recevier
        unregisterReceiver(finishMsgReceiver)
        super.onDestroy()
    }

    inner class MyBroadcastRecevier() : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            finish()
        }
    }
}
