package com.example.calender;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

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
    private Spinner spinner;
    private TimePicker timePicker;

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
        String str = intent.getStringExtra("schedule");
        int index = str.indexOf(" type");
        schedule = str.substring(0, index);

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

//        下拉框初始化
        spinner = (Spinner) findViewById(R.id.scheduleType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Do something with the selection
                String selected = parent.getItemAtPosition(position).toString();
//                Toasty.info(context,selected).show();
//                Toast.makeText(parent.getContext(), selected, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

        //时间选择器初始化
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

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
        Spinner spinner = findViewById(R.id.scheduleType);
        String scheduletype = spinner.getSelectedItem().toString();
        //获取当前系统时间的Calendar实例
        String hour = timePicker.getCurrentHour().toString();
        String minute = timePicker.getCurrentMinute().toString();
        //格式化时间
        String formattedTime = hour + ":" + minute;

        values.put("scheduleDetail",scheduleInput.getText().toString());
        values.put("timeInfo", formattedTime);
        values.put("type", scheduletype);

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