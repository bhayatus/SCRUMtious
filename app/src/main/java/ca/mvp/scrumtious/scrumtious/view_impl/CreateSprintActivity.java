package ca.mvp.scrumtious.scrumtious.view_impl;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateSprintViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateSprintPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;


public class CreateSprintActivity extends AppCompatActivity implements CreateSprintViewInt {

    private EditText titleField, descriptionField;
    private TextInputLayout titleFieldLayout, descriptionFieldLayout;
    private ProgressDialog createSprintProgessDialog;
    private CreateSprintPresenter createSprintPresenter;

    private TextView DisplayStartDate;
    private TextView DisplayEndDate;
    private DatePickerDialog.OnDateSetListener StartDateSetListner;
    private DatePickerDialog.OnDateSetListener EndDateSetListner;

    private ImageButton logoutBtn;
    private boolean alreadyDeleted;
    private String pid;
    private android.support.v7.widget.Toolbar toolbar;

    private int[] startYear = new int[1];
    private int[] startMonth = new int[1];
    private int[] startDay = new int[1];
    private int[] endYear = new int[1];
    private int[] endMonth = new int[1];
    private int[] endDay = new int[1];
    private long startDate;
    private long endDate;
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
        Calendar startCalender = new GregorianCalendar(startYear[0],startMonth[0],startDay[0]);
        Calendar endCalender = new GregorianCalendar(endYear[0],endMonth[0],endDay[0]);
        startDate = startCalender.getTimeInMillis();
        endDate = endCalender.getTimeInMillis();
        //Setting the variables
        alreadyDeleted = false;

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        createSprintPresenter = new CreateSprintPresenter(this, pid);
        createSprintPresenter.setupProjectDeletedListener();

        logoutBtn = findViewById(R.id.createSprintLogoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthenticationHelper.logout(CreateSprintActivity.this);
            }
        });

        toolbar = findViewById(R.id.createSprintToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setupFormWatcher();

    }

    public void onBackPressed(){

        if(titleField.getText().toString().trim().length() > 0 || descriptionField.getText().toString().trim().length() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Sprint?")
                    .setMessage("Are you sure you want to delete the sprint?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CreateSprintActivity.this, SprintListActivity.class);
                            intent.putExtra("projectId", pid);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        }else{
                super.onBackPressed();
        }

    }


    public void setupFormWatcher(){
        titleField = (EditText)findViewById(R.id.createSprintTitleField);
        descriptionField = (EditText)findViewById(R.id.createSprintDescField);

        titleFieldLayout = (TextInputLayout)findViewById(R.id.createSprintTitleFieldLayout);
        descriptionFieldLayout = (TextInputLayout)findViewById(R.id.createSprintDescFieldLayout);

        titleFieldLayout.setError(null);
        descriptionFieldLayout.setError(null);

        titleFieldLayout.setErrorEnabled(true);
        descriptionFieldLayout.setErrorEnabled(true);

        //Create watcher and listener for titleField

        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String titleFieldText = titleField.getText().toString();

                if((titleFieldText == null) || (titleFieldText.trim().length() <= 0)){
                    titleFieldLayout.setErrorEnabled(true);
                    titleFieldLayout.setError("Please enter a title for your sprint");
                }else if(titleFieldText.trim().length() > 28){
                    titleFieldLayout.setErrorEnabled(true);
                    titleFieldLayout.setError("Sprint name is too long");
                }else{
                    titleFieldLayout.setErrorEnabled(false);
                    titleFieldLayout.setError(null);
                }
            }
        });

        descriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String descriptionFieldText = descriptionField.getText().toString();
                if(descriptionFieldText == null || (descriptionFieldText.trim().length() <= 0)){
                    descriptionFieldLayout.setErrorEnabled(true);
                    descriptionFieldLayout.setError("Please enter a description for your sprint");
                }else{
                    descriptionFieldLayout.setErrorEnabled(false);
                    descriptionFieldLayout.setError(null);
                }
            }
        });


    }


    public void showMessage(String message){
        if(createSprintProgessDialog != null && createSprintProgessDialog.isShowing()){
            createSprintProgessDialog.dismiss();
        }
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();

    }

    public void onCreateSprint(View view){
        if(titleFieldLayout.isErrorEnabled() || descriptionFieldLayout.isErrorEnabled()){
            showMessage("Cannot create sprint until fields are filled out proeprly");
        }else{
            String name = titleField.getText().toString().trim();
            String desc = descriptionField.getText().toString().trim();

            createSprintProgessDialog = new ProgressDialog(this);
            createSprintProgessDialog.setTitle("Create Sprint");
            createSprintProgessDialog.setCancelable(false);
            createSprintProgessDialog.setMessage("Creating sprint...");
            createSprintProgessDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            createSprintProgessDialog.show();

            //createSprintProgessDialog.onCheckConflictingSprintDates(name, desc, startDate, endDate);

        }
    }

    public void onSuccessfulCreateSprint(){
        if(createSprintProgessDialog != null && createSprintProgessDialog.isShowing()){
            createSprintProgessDialog.dismiss();
        }

        Intent intent = new Intent(CreateSprintActivity.this, SprintListActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
        finish();
    }

    public void onProjectDeleted() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!alreadyDeleted) {
            alreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(CreateSprintActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    


}
