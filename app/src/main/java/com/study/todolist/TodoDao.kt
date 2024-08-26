package com.study.todolist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.study.todolist.model.TodoInfo


@Dao
interface TodoDao {
    @Insert
    fun insertTodoData(todoInfo: TodoInfo)

    @Update
    fun updateTodoData(todoInfo: TodoInfo)

    @Delete
    fun deleteTodoData(todoInfo: TodoInfo)

    @Query("SELECT * FROM TodoInfo ORDER BY todoDate")
    fun readAllData(): List<TodoInfo>
}