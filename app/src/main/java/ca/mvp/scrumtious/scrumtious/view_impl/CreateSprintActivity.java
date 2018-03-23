package ca.mvp.scrumtious.scrumtious.view_impl;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateSprintViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateSprintPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class CreateSprintActivity extends AppCompatActivity implements CreateSprintViewInt {

    private CreateSprintPresenter createSprintPresenter;
    private String pid;
    private ValueEventListener projectListener;

    private EditText titleField, descriptionField;
    private TextView displayStartDate, displayEndDate;
    private TextInputLayout titleFieldLayout, descriptionFieldLayout;
    private DatePickerDialog startDialog, endDialog;
    private DatePickerDialog.OnDateSetListener startDateSetListener;
    private DatePickerDialog.OnDateSetListener endDateSetListener;
    private ProgressDialog createSprintProgressDialog;
    private ImageButton logoutBtn;
    private android.support.v7.widget.Toolbar toolbar;

    private boolean projectAlreadyDeleted;

    private int[] startYear = new int[1];
    private int[] startMonth = new int[1];
    private int[] startDay = new int[1];
    private int[] endYear = new int[1];
    private int[] endMonth = new int[1];
    private int[] endDay = new int[1];
    private long startDate;
    private long endDate;

    private int defaultStartYear, defaultStartMonth, defaultStartDay;
    private int defaultEndYear, defaultEndMonth, defaultEndDay;

    private boolean choseStart = false;
    private boolean choseEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sprint);

        // Setting the flag
        projectAlreadyDeleted = false;

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        createSprintPresenter = new CreateSprintPresenter(this, pid);

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

    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        super.onResume();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        super.onPause();
    }

    @Override
    public void onBackPressed(){

        if(titleField.getText().toString().trim().length() > 0 || descriptionField.getText().toString().trim().length() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Sprint?")
                    .setMessage("Are you sure you want to userStoryRowDeleteUserStoryImageButton the sprint?")
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

    // Project was deleted by another user, or user was removed from the project
    @Override
    public void onProjectDeleted() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(CreateSprintActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSprintDeleted() {
        // Needs to be here even if not implemented
    }

    @Override
    public void onUserStoryDeleted() {
        // Needs to be here even if not implemented
    }

    public void setupFormWatcher(){
        titleField = (EditText)findViewById(R.id.createSprintTitleField);
        descriptionField = (EditText)findViewById(R.id.createSprintDescField);
        displayStartDate = (TextView) findViewById(R.id.createSprintStartDate);
        displayEndDate = (TextView) findViewById(R.id.createSprintEndDate);

        titleFieldLayout = (TextInputLayout)findViewById(R.id.createSprintTitleFieldLayout);
        descriptionFieldLayout = (TextInputLayout)findViewById(R.id.createSprintDescFieldLayout);

        titleFieldLayout.setError(null);
        descriptionFieldLayout.setError(null);

        titleFieldLayout.setErrorEnabled(true);
        descriptionFieldLayout.setErrorEnabled(true);

        // Current year, month, day for start date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Makes sure it's set to proper date
        Calendar startCalender = new GregorianCalendar(year, month, day);
        defaultStartYear = startCalender.get(Calendar.YEAR);
        defaultStartMonth = startCalender.get(Calendar.MONTH);
        defaultStartDay = startCalender.get(Calendar.DAY_OF_MONTH);

        // Current year, month, day for end date
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        // Makes sure it's set to proper date
        Calendar endCalender = new GregorianCalendar(year, month, day);
        defaultEndYear = endCalender.get(Calendar.YEAR);
        defaultEndMonth = endCalender.get(Calendar.MONTH);
        defaultEndDay = endCalender.get(Calendar.DAY_OF_MONTH);

        displayStartDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                startDialog = new DatePickerDialog(
                        CreateSprintActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        startDateSetListener,
                        defaultStartYear, defaultStartMonth, defaultStartDay
                );
                startDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                startDialog.show();
            }

        });
        displayEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endDialog = new DatePickerDialog(
                        CreateSprintActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        endDateSetListener,
                        defaultEndYear, defaultEndMonth, defaultEndDay
                );
                endDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                endDialog.show();
            }
        });

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                displayStartDate.setText(date);
                startYear[0] = year;
                startMonth[0] = month;
                startDay[0] = day;
                Calendar startCalender = new GregorianCalendar(startYear[0],startMonth[0] - 1,startDay[0]);
                startDate = startCalender.getTimeInMillis();

                // Updates it so that opening the dialog picker again starts user at the previously chosen date
                defaultStartYear = year;
                defaultStartMonth = month - 1;
                defaultStartDay = day;

                choseStart = true;
            }
        };
        endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                displayEndDate.setText(date);
                endYear[0] = year;
                endMonth[0] = month;
                endDay[0] = day;
                Calendar endCalender = new GregorianCalendar(endYear[0],endMonth[0] - 1,endDay[0]);
                endDate = endCalender.getTimeInMillis();

                // Updates it so that opening the dialog picker again starts user at the previously chosen date
                defaultEndYear = year;
                defaultEndMonth = month - 1;
                defaultEndDay = day;

                choseEnd = true;
            }
        };

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
                    titleFieldLayout.setError("Please enter a title for your sprint.");
                }else if(titleFieldText.trim().length() > 18){
                    titleFieldLayout.setErrorEnabled(true);
                    titleFieldLayout.setError("Sprint name is too long.");
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
                    descriptionFieldLayout.setError("Please enter a sprint description.");
                }else{
                    descriptionFieldLayout.setErrorEnabled(false);
                    descriptionFieldLayout.setError(null);
                }
            }
        });


    }


    public void showMessage(String message, boolean showAsToast){
        if(createSprintProgressDialog != null && createSprintProgressDialog.isShowing()){
            createSprintProgressDialog.dismiss();
        }

        // Show message in toast so it persists across activity transitions
        if (showAsToast){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        else {
            // Call the utils class method to handle making the snackbar
            SnackbarHelper.showSnackbar(this, message);
        }

    }

    public void onClickCreateSprint(View view){
        if(titleFieldLayout.isErrorEnabled() || descriptionFieldLayout.isErrorEnabled() || !choseStart || !choseEnd){
            showMessage("Cannot create sprint until fields are filled out properly.", false);
        }
        else if(startDate>=endDate){
            showMessage("Cannot have start date on, or after end date.", false);
        }
        else{
            String name = titleField.getText().toString().trim();
            String desc = descriptionField.getText().toString().trim();

            // Creates a progress dialog to let the user know that the sprint is being created
            createSprintProgressDialog = new ProgressDialog(this);
            createSprintProgressDialog.setTitle("Create Sprint");
            createSprintProgressDialog.setCancelable(false);
            createSprintProgressDialog.setMessage("Creating sprint...");
            createSprintProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            createSprintProgressDialog.show();

            // Check to make sure that dates don't overlap first
            createSprintPresenter.checkConflictingSprintDates(name,desc,startDate,endDate);

        }
    }

    // Sprint has been created, go back
    public void onSuccessfulCreateSprint(){
        if(createSprintProgressDialog != null && createSprintProgressDialog.isShowing()){
            createSprintProgressDialog.dismiss();
        }

        Intent intent = new Intent(CreateSprintActivity.this, SprintListActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
        finish();
    }


}
