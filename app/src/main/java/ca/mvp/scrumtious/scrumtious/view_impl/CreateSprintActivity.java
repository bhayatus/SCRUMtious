package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

import ca.mvp.scrumtious.scrumtious.R;

public class CreateSprintActivity extends AppCompatActivity {
    private TextView DisplayStartDate;
    private TextView DisplayEndDate;
    private DatePickerDialog.OnDateSetListener StartDateSetListner;
    private DatePickerDialog.OnDateSetListener EndDateSetListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sprint);
        DisplayStartDate = (TextView) findViewById(R.id.createSprintStartDate);
        DisplayEndDate = (TextView) findViewById(R.id.createSprintEndDate);
        DisplayStartDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog startDialog = new DatePickerDialog(
                        CreateSprintActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        StartDateSetListner,
                        year,month,day
                );
                startDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                startDialog.show();
            }
        });
        DisplayEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog endDialog = new DatePickerDialog(
                        CreateSprintActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        EndDateSetListner,
                        year,month,day
                );
                endDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                endDialog.show();
            }
        });
        final int[] startYear = new int[1];
        final int[] startMonth = new int[1];
        final int[] startDay = new int[1];
        final int[] endYear = new int[1];
        final int[] endMonth = new int[1];
        final int[] endDay = new int[1];
        StartDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month +"/" + year;
                DisplayStartDate.setText(date);
                startYear[0] = year;
                startMonth[0] = month;
                startDay[0] = day;

            }
        };
        EndDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month +"/" + year;
                DisplayEndDate.setText(date);
                endYear[0] = year;
                endMonth[0] = month;
                endDay[0] = day;
            }
        };
        String startDate = "" + startYear[0] + startMonth[0] + startDay[0];
        String endDate = "" + endYear[0] + endMonth[0] + endDay[0];
    }
}
