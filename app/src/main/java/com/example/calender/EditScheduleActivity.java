package com.example.calender;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditScheduleActivity extends AppCompatActivity{

    private String schedule;
    private Button editBtn,deleteBtn;
    private EditText scheduleInput;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_schedule);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        // 首先获取到意图对象
        Intent intent = getIntent();
        // 获取到传递过来的姓名
        schedule = intent.getStringExtra("schedule");

        initView();

    }

    private void initView() {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();

        editBtn = findViewById(R.id.editBtn1);
        //editBtn.setOnClickListener(this);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSchedule();
            }
        });

        deleteBtn = findViewById(R.id.deleteSchedule1);
        //deleteBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMySchedule();
            }
        });
        scheduleInput = findViewById(R.id.scheduleInput);
        scheduleInput.setText(schedule);
    }

    /*@Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.deleteSchedule1:
                deleteMySchedule();
                break;
            case R.id.editBtn1:
                editSchedule();
                break;
        }
    }*/

    private void editSchedule() {
        ContentValues values = new ContentValues();
        values.put("scheduleDetail",scheduleInput.getText().toString());

        myDatabase.update("schedules",values,"scheduleDetail=?",new String[]{schedule});

        Intent intent = new Intent(EditScheduleActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void deleteMySchedule() {
        myDatabase.delete("schedules","scheduleDetail=?",new String[]{schedule});

        Intent intent = new Intent(EditScheduleActivity.this, MainActivity.class);
        startActivity(intent);
    }


}