package com.crepuscule.jstu.homework6

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.widget.Toast

/*
 * 数据库中新建 table : todolist
 * entry 包括: task title, details, date (设立时间，可以用于唯一标识一个任务), deadline, status (todo | done)
 */

class DBHelper(val context: Context, name: String, version: Int = 1): SQLiteOpenHelper(context, name, null, version) {
    private val TodoListCreate by lazy { "create table todolist(" + " id integer primary key autoincrement, " + " task_title text, " +  " task_status done, " + " task_details text, " + " task_deadline text, " + " task_date text) " }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TodoListCreate)
        Toast.makeText(context, "Todo List 初始化完成", Toast.LENGTH_SHORT).show()
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun openDB() : SQLiteDatabase {
        return this.writableDatabase
    }

    // 根据 task date 查询 task 其它内容, 返回 Map (String -> String)
    fun queryTaskInfo(Tag : String) : MutableMap<String?, String?>{
        var myMap = mutableMapOf<String?, String?>(Pair("task_exist", "False"))
        val cursor = this.writableDatabase.query("todolist", arrayOf("task_title", "task_status", "task_date", "task_deadline", "task_details"), "task_date = ?", arrayOf(Tag), null, null, null)
        while (cursor.moveToNext()) {
            val task_title: String? = cursor.getString(cursor.getColumnIndexOrThrow("task_title"));
            val task_details : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_details"));
            val task_status : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_status"));
            val task_deadline : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_deadline"));
            myMap.put("task_title", task_title)
            myMap.put("task_details", task_details)
            myMap.put("task_deadline", task_deadline)
            myMap.put("task_status", task_status)
            myMap.put("task_date", Tag)
            myMap["task_exist"] = "True"
        }
        return myMap
    }
}
