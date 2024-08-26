package com.study.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TodoInfo {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var todoContent: String = ""
    var todoDate: String = ""

}