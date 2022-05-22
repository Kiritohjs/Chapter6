package com.crepuscule.jstu.homework6

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi

// 该页面仅在点击已完成事件时出现，只显示内容而不可编辑
class TaskPageShowActivity : AppCompatActivity() {
    private val saveTask: Button by lazy { findViewById(R.id.confirm_button) }      // 保存按钮
    private val cancelTask : Button by lazy { findViewById(R.id.cancel_button) }    // 取消按钮
    private lateinit var task_title_edittext: EditText
    private lateinit var task_details_edittext: EditText
    private lateinit var task_deadline_edittext: EditText
    private val dbHelper = DBHelper(this, "ToDoList_v7.db")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_show_page)

        val db = dbHelper.readableDatabase
        val task_date = intent.extras?.getString("task_date").toString().substring(12)

        // 当用户进入对应页面进行编辑时，页面应该显示用户之前输入的内容
        showContent(task_date)

    }

    private fun showContent(Tag : String) : Boolean {
        val myMap = dbHelper.queryTaskInfo(Tag)
        if (myMap["task_exist"] === "True") {
            task_title_edittext = findViewById<EditText>(R.id.task_title_edittext)
            task_title_edittext.setText(myMap["task_title"])

            task_details_edittext = findViewById<EditText>(R.id.task_details_edittext)
            task_details_edittext.setText(myMap["task_details"])

            task_deadline_edittext = findViewById<EditText>(R.id.task_deadline_edittext)
            task_deadline_edittext.setText(myMap["task_deadline"])
            return true
        }
        else return false
    }
}

