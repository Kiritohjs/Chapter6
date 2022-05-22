package com.crepuscule.jstu.homework6

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 每一个 Task 详细内容，在添加 / 修改 时切换
// 接收 task_title task_detail task_deadline 三个参数
class TaskPageEditActivity : AppCompatActivity() {
    private val saveTask: top.androidman.SuperButton by lazy { findViewById(R.id.confirm_button) }      // 保存按钮
    private val cancelTask: top.androidman.SuperButton by lazy { findViewById(R.id.cancel_button) }    // 取消按钮
    private lateinit var task_title_edittext: EditText
    private lateinit var task_details_edittext: EditText
    private lateinit var task_deadline_edittext: EditText

    private val dbHelper = DBHelper(this, "ToDoList_v8.db")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_edit_page)

        val db = dbHelper.writableDatabase
        var task_date = intent.extras?.getString("task_date").toString()
        if (task_date.startsWith("Startdate : ")) task_date = task_date.substring(12)

        // 当用户进入对应页面进行编辑时，页面应该显示用户之前输入的内容
        val task_exist = showContent(task_date)

        // 点击保存时将数据存入 database
        saveTask.setOnClickListener {
            task_title_edittext = findViewById<EditText>(R.id.task_title_edittext)
            task_details_edittext = findViewById<EditText>(R.id.task_details_edittext)
            task_deadline_edittext = findViewById<EditText>(R.id.task_deadline_edittext)

            val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            val new_date = dateFormatter.format(LocalDateTime.now())
            var task_deadline = task_deadline_edittext.text.toString()
            var task_title = task_title_edittext.text.toString()
            var task_details = task_details_edittext.text.toString()

            if (task_deadline == "") {
                task_deadline = "无截止日期"
            }
            if (task_title == "") {
                task_title = "无主题"
            }
            if (task_details == "") {
                task_details = "无具体描述"
            }

            val newTask = ContentValues().apply {
                put("task_title", task_title)
                put("task_details", task_details)
                put("task_deadline", task_deadline)
                put("task_status", "todo")
            }

            if (task_exist) {
                db.update("todolist", newTask, "task_date = ?", arrayOf(task_date))
            } else {
                newTask.put("task_date", new_date)
                db.insert("todolist", null, newTask)
            }

            Log.d("DataBaseInsert", "————————————————")
            val intent_1 = Intent()
            intent_1.setAction("MAIN_FINISH")
            sendBroadcast(intent_1)                 // 结束 MainActivity
            finish()
            Log.d("TaskPageFinished", "#################3")
            val intent_2 = Intent(this, MainActivity::class.java)
            startActivity(intent_2)                 // 重新启动 MainActivity
            Log.d("MainStarted", "##################")

            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
        }

        // 点击取消时返回
        cancelTask.setOnClickListener {
            Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showContent(Tag: String): Boolean {
        Log.d("Task Exist?", Tag)
        val myMap = dbHelper.queryTaskInfo(Tag)
        if (myMap["task_exist"] === "True") {
            task_title_edittext = findViewById<EditText>(R.id.task_title_edittext)
            task_title_edittext.setText(myMap["task_title"])

            task_details_edittext = findViewById<EditText>(R.id.task_details_edittext)
            task_details_edittext.setText(myMap["task_details"])

            task_deadline_edittext = findViewById<EditText>(R.id.task_deadline_edittext)
            task_deadline_edittext.setText(myMap["task_deadline"])
            return true
        } else return false
    }
}
