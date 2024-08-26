package com.study.todolist

import android.content.DialogInterface
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.study.todolist.databinding.ActivityMainBinding
import com.study.todolist.databinding.DialogEditBinding
import com.study.todolist.model.TodoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding  // binding 준비
    private lateinit var todoAdapter: TodoAdapter // adapter 준비
    private lateinit var roomDatabase: TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        binding =
            ActivityMainBinding.inflate((layoutInflater)) // 실제 activity_main.xml에 있는 리사이클러 뷰 연동
        setContentView(binding.root)

        //어댑터 생성
        todoAdapter = TodoAdapter()

        //리사이클러 뷰에 어댑터 세팅
        binding.recyclerTodo.adapter = todoAdapter // 어댑터를 이미 만들어놨기 때문에 바로 연결 가능

        //roomdatabase 초기화
        roomDatabase = TodoDatabase.getInstance(applicationContext)!!

        //전체 데이터 load (코루틴 사용 - 비동기 처리)
        CoroutineScope(Dispatchers.IO).launch {
            val listTodo =
                roomDatabase.todoDao().readAllData() as ArrayList<TodoInfo> // 전체 데이터 가져오기

            //어댑터에 데이터 전달
            for (todoItem in listTodo) {
                todoAdapter.addListItem(todoItem)
                Log.d("todoAdapter", "${todoAdapter}")
            }
            //ui thread에서 처리
            runOnUiThread {
                todoAdapter.notifyDataSetChanged()
                Log.d("DataSetChanged", "${todoAdapter.itemCount}")
            }
        }

        // AlertDialog로 팝업창 만들기
        binding.btnWrite.setOnClickListener {
            val bindingDialog = DialogEditBinding.inflate(
                LayoutInflater.from(binding.root.context),
                binding.root,
                false
            )

            AlertDialog.Builder(this)
                .setTitle("할 일 기록하기") // 팝업 제목
                .setView(bindingDialog.root)
                .setPositiveButton("작성 완료", DialogInterface.OnClickListener { dialog, which ->
                    // 작성 완료 버튼을 눌렀을 때 동작
                    val todoItem = TodoInfo() // todoItem에 사용자가 입력한 값을 넣음
                    todoItem.todoContent = bindingDialog.etMemo.text.toString() // 사용자가 입력한 값 가져오기
                    todoItem.todoDate = SimpleDateFormat("yyyy-MM-dd").format(Date()) // 최신 날짜 설정
                    todoAdapter.addListItem(todoItem) // adapter 쪽으로 리스트 아이템 전달
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            roomDatabase.todoDao().insertTodoData(todoItem) // 데이터베이스에 클래스 데이터 삽입
                            Log.d("MainActivity", "success")
                        } catch (e: Exception) {
                            Log.e("MainActivity", "fail")
                        }
                        runOnUiThread {
                            Log.d("MainActivity", "asdfxczvijfdsalkjwernklsfda")
                            try {
                                todoAdapter.notifyDataSetChanged() // 리스트 새로고침
                                Log.d("MainActivity", "notify-success")
                                Log.d("MainActivity", "${roomDatabase.todoDao().readAllData()}")
                                Log.d("MainActivity", "read-success")
                            } catch (e: Exception) {
                                Log.e("MainActivity", "notify-fail")
                            }

                        }
                    }
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                }).show()
        }
    }
}