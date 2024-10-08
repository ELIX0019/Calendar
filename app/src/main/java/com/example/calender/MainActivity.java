package com.example.calender;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText scheduleInput;
    private Context context;
    private Spinner spinner;
    private TimePicker timePicker;
    private Button addSchedule,checkAdd;
    private String dateToday;//用于记录今天的日期
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase myDatabase;
    private TextView mySchedule[] = new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        initView();

        //这里不这样的话一进去就设置当天的日程会报错
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH)+1;//注意要+1，0表示1月份
        int day = time.get(Calendar.DAY_OF_MONTH);
        dateToday = year+"-"+month+"-"+day;
        //还要直接查询当天的日程，这个要放在initView的后面，不然会出问题
        queryByDate(dateToday);

    }

    private void initView() {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();
        Spinner scheduleType = findViewById(R.id.scheduleType);

        context = this;
        addSchedule = findViewById(R.id.addSchedule);
        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMySchedule();
            }
        });
        checkAdd = findViewById(R.id.checkAdd);
        checkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAddSchedule();
            }
        });

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

//        时间选择器初始化
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);


        calendarView = findViewById(R.id.calendar);
        scheduleInput = findViewById(R.id.scheduleDetailInput);

        calendarView.setOnDateChangeListener(mySelectDate);

        mySchedule[0] = findViewById(R.id.schedule1);
        mySchedule[1] = findViewById(R.id.schedule2);
        mySchedule[2] = findViewById(R.id.schedule3);
        mySchedule[3] = findViewById(R.id.schedule4);
        mySchedule[4] = findViewById(R.id.schedule5);
        for(TextView v:mySchedule){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editSchedule(v);
                }
            });
        }
    }

    private CalendarView.OnDateChangeListener mySelectDate = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            dateToday = year+"-"+(month+1)+"-"+dayOfMonth;
            Toasty.info(context,"你选择了:"+dateToday, Toast.LENGTH_SHORT).show();

            //得把用别的日期查出来的日程删除并将其隐藏
            for(TextView v:mySchedule){
                v.setText("");
                v.setVisibility(View.GONE);
            }
            queryByDate(dateToday);
        }
    };

    //根据日期查询日程
    private void queryByDate(String date) {
        //columns为null 查询所有列
        Cursor cursor = myDatabase.query("schedules",null,"time=?",new String[]{date},null,null,null);
        if(cursor.moveToFirst()){
            int scheduleCount = 0;
            do{
                @SuppressLint("Range") String aScheduleDetail = cursor.getString(cursor.getColumnIndex("scheduleDetail"));
                @SuppressLint("Range") String aScheduleTime = cursor.getString(cursor.getColumnIndex("time"));
                @SuppressLint("Range") String aScheduleTimeInfo = cursor.getString(cursor.getColumnIndex("timeInfo"));
                @SuppressLint("Range") String aScheduleType = cursor.getString(cursor.getColumnIndex("type"));
                mySchedule[scheduleCount].setText("日程"+(scheduleCount+1)+"："+aScheduleDetail+" type:"+aScheduleType+" time:"+aScheduleTimeInfo);
                mySchedule[scheduleCount].setVisibility(View.VISIBLE);
                scheduleCount++;
                //一定要有这句 不然TextView不够多要数组溢出了
                if(scheduleCount >= 5)
                    break;
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addSchedule:
                addMySchedule();
                break;
            case R.id.checkAdd:
                checkAddSchedule();
                break;
            case R.id.schedule1:case R.id.schedule2:case R.id.schedule3:case R.id.schedule4:case R.id.schedule5:
                editSchedule(v);
                break;
        }
    }*/

    private void editSchedule(View v) {
        Intent intent = new Intent(MainActivity.this, EditScheduleActivity.class);
        String sch = ((TextView) v).getText().toString().split("：")[1];
        intent.putExtra("schedule",sch);
        startActivity(intent);
    }


    private void checkAddSchedule() {
        // 获取用户输入的日程内容
        String scheduleDetail = scheduleInput.getText().toString().trim();
//        获取下拉框内容  spinner.getSelectedItem().toString()
        Spinner spinner = findViewById(R.id.scheduleType);
        String scheduletype = spinner.getSelectedItem().toString();
        //获取当前系统时间的Calendar实例
        String hour = timePicker.getCurrentHour().toString();
        String minute = timePicker.getCurrentMinute().toString();
        //格式化时间
        String formattedTime = hour + ":" + minute;


        if (scheduleDetail.isEmpty()) {
            Toasty.info(MainActivity.this,"日程不能为空").show();
            return;
        }

        ContentValues values = new ContentValues();
        // 第一个参数是表中的列名
        values.put("scheduleDetail", scheduleDetail);
        values.put("time", dateToday);
        values.put("timeInfo", formattedTime);
        values.put("type", scheduletype);
        long result = myDatabase.insert("schedules", null, values);

        if (result == -1) {
            Toasty.warning(MainActivity.this,"添加日程失败").show();
        } else {
            scheduleInput.setVisibility(View.GONE);
            checkAdd.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
            queryByDate(dateToday);
            // 添加完以后把scheduleInput中的内容清除
            Toasty.success(MainActivity.this,"添加成功").show();
            scheduleInput.setText("");
        }
    }

    private void addMySchedule() {
//      设置可见性
        scheduleInput.setVisibility(View.VISIBLE);
        checkAdd.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        timePicker.setVisibility(View.VISIBLE);
    }

}