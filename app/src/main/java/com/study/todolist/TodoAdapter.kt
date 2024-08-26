package com.study.todolist

import android.app.Activity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.study.todolist.databinding.DialogEditBinding
import com.study.todolist.databinding.ListItemTodoBinding
import com.study.todolist.model.TodoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var listTodo: ArrayList<TodoInfo> = ArrayList() // TodoInfo라는 데이터를 가진 리스트를 listTodo에 담음
    private lateinit var roomDatabase: TodoDatabase


//    // init = 클래스가 생설될때 가징먼저 호출되는것
//    init {
//        // 샘플 리스트 아이템 인스턴스 생성(임의로 만든거) (room DB연동 후에는 삭제함)
//        val todoItem = TodoInfo()
//        todoItem.todoContent = "To Do List앱 만들기!"
//        todoItem.todoDate = "2023-06-01"
//        listTodo.add(todoItem)
//
//        // 샘플 리스트 아이템 인스턴스 생성
//        val todoItem2 = TodoInfo()
//        todoItem2.todoContent = "블로그 작성하기"
//        todoItem2.todoDate = "2023-06-01"
//        listTodo.add(todoItem2)
//
//        // 샘플 리스트 아이템 인스턴스 생성
//        val todoItem3 = TodoInfo()
//        todoItem3.todoContent = "기말 공부하기!"
//        todoItem3.todoDate = "2023-06-01"
//        listTodo.add(todoItem3)
//    }

    fun addListItem(todoItem: TodoInfo) {
        // 4, 0, 1, 2, 3
        listTodo.add(0, todoItem) // 인덱스 0에 데이터 삽입, 데이터가 추가될 때 마다 최근 순으로 배치
    }

    inner class TodoViewHolder(private val binding: ListItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // viewHolder = 각 리스트 아이템들을 보관하는 객체
        fun bind(todoItem: TodoInfo) {
            // 리스트 뷰 데이터를 ui에 연동
            binding.tvContent.setText(todoItem.todoContent)
            binding.tvDate.setText(todoItem.todoDate)

            //삭제 버튼 클릭 이벤트
            binding.btnDelete.setOnClickListener {
                //다이얼로그 띄우기
                AlertDialog.Builder(binding.root.context)
                    .setTitle("삭제")
                    .setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("네", DialogInterface.OnClickListener { dialog, which ->
                        CoroutineScope(Dispatchers.IO).launch {
                            // 현재 DB에 저장되어있는 데이터를 전부 가져와 arraylist 형태로 만듦
                            val innerListTodo = roomDatabase.todoDao().readAllData()
                            for (item in innerListTodo) {
                                if (item.todoContent == todoItem.todoContent && item.todoDate == todoItem.todoDate) {
                                    //database item 삭제
                                    roomDatabase.todoDao().deleteTodoData(item)
                                }
                            }

                            // ui 삭제
                            listTodo.remove(todoItem) // listTodo 배열에 담겨 있는 todoItem 데이터 삭제
                            (binding.root.context as Activity).runOnUiThread {
                                notifyDataSetChanged()
                                Toast.makeText(binding.root.context, "삭제되었습니다", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->

                    })
                    .show()
            }

            // 리스트 수정하기
            binding.root.setOnClickListener {
                val bindingDialog = DialogEditBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    binding.root,
                    false
                )
                //기존에 작성한 데이터 보여주기
                bindingDialog.etMemo.setText(todoItem.todoContent)
                AlertDialog.Builder(binding.root.context)
                    .setTitle("수정")
                    .setView(bindingDialog.root)
                    .setPositiveButton("수정 완료", DialogInterface.OnClickListener { dialog, which ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val innerListTodo = roomDatabase.todoDao().readAllData()
                            for (item in innerListTodo) {
                                if (item.todoContent == todoItem.todoContent && item.todoDate == todoItem.todoDate) {
                                    item.todoContent = bindingDialog.etMemo.text.toString()
                                    item.todoDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                                    roomDatabase.todoDao().updateTodoData(item)
                                }
                            }

                            // ui 수정하기
                            todoItem.todoContent = bindingDialog.etMemo.text.toString()
                            todoItem.todoDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                            listTodo.set(adapterPosition, todoItem)

                            (binding.root.context as Activity).runOnUiThread {
                                notifyDataSetChanged()
                            }
                        }
                    })
                    // 취소 버튼 클릭 이벤트
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                        // 취소 버튼 눌렀을 때 동작
                    })
                    .show() // 이걸 적어줘야 정상적으로 작동함
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        /*
        * viewHolder가 만들어질 때
        * 각 리스트 아이템이 하나 씩 구성될 때 마다 이 메소드가 호출됨*/
        val binding = ListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // roomDatabase 초기화
        roomDatabase = TodoDatabase.getInstance(binding.root.context)!!

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        // viewHolder가 연결(binding)될 때
        holder.bind(listTodo[position])
    }

    override fun getItemCount(): Int {
        // 리스트의 총 개수
        return listTodo.size
    }
}