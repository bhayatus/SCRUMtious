package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateTaskViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateProjectPresenter;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateTaskPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;



public class CreateTaskActivity extends AppCompatActivity implements
        CreateTaskViewInt {

    private CreateTaskPresenter createTaskPresenter;

    private android.support.v7.widget.Toolbar toolbar;
    private ProgressDialog createTaskProgressDialog;

    private EditText descriptionField;
    private TextInputLayout descriptionFieldLayout;

    private ImageButton logoutBtn;
    private String pid, usid;

    private ValueEventListener projectListener;
    private ValueEventListener userStoryListener;

    private boolean projectAlreadyDeleted;
    private boolean userStoryAlreadyDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        usid = data.getString("userStoryId");

        // Setting the flag
        projectAlreadyDeleted = false;
        userStoryAlreadyDeleted = false;

        createTaskPresenter = new CreateTaskPresenter(this, pid, usid);

        toolbar = findViewById(R.id.createTaskToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // This should do the same as pressing back
            }
        });

        setupFormWatcher();
    }


    @Override
    public void onBackPressed(){

        if(descriptionField.getText().toString().trim().length() > 0){
            new AlertDialog.Builder(this)
                    .setTitle("Discard Task?")
                    .setMessage("Are you sure you want to discard the task?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CreateTaskActivity.this, IndividualUserStoryActivity.this);
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

    private void setupFormWatcher(){

        descriptionField = (EditText) findViewById (R.id.createTaskDescField);
        descriptionFieldLayout = (TextInputLayout)findViewById(R.id.createSprintDescFieldLayout);

        descriptionFieldLayout.setErrorEnabled(true);
        descriptionFieldLayout.setError(null);

        descriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String descFieldText = descriptionField.getText().toString();

                if((descFieldText == null) || (descFieldText.trim().length() <= 0)){
                    descriptionFieldLayout.setErrorEnabled(true);
                    descriptionFieldLayout.setError("Please enter a description for your task.");


                    //////////////////////////////////////CHANGE AFTER BECAUSE OF LENGTH, TESTING 50 /////////////////////////////////////////////////////////
                }else if(descFieldText.trim().length() > 50){
                    descriptionFieldLayout.setErrorEnabled(true);
                    descriptionFieldLayout.setError("Task name is too long.");
                }else{
                    descriptionFieldLayout.setErrorEnabled(false);
                    descriptionFieldLayout.setError(null);
                }
            }

        });
    }

    // Return to task list screen
    public void onSuccessfulCreateTask(){

        //Stop dialog if showing
        if(createTaskProgressDialog != null && createTaskProgressDialog.isShowing()){
            createTaskProgressDialog.dismiss();
        }

        //Return to IndividualUserStoryActivity
        Intent intent = new Intent(CreateTaskActivity.this, IndividualUserStoryActivity.class);
        intent.putExtra("userId", usid);
        startActivity(intent);
        finish();
    }

    public void showMessage(String message, boolean showAsToast) {

        if(createTaskProgressDialog != null && createTaskProgressDialog.isShowing()){
            createTaskProgressDialog.dismiss();
        }
        if(showAsToast){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }else{
            SnackbarHelper.showSnackbar(this, message);
        }
    }

    // User clicks on create task button
    public void onClickCreateTaskSubmit(View view){


//        String title = titleField.getText().toString().trim();
//        String description = descriptionField.getText().toString().trim();
//        // If either error message is displaying, that means the form can't be submitted properly
//        if(titleFieldLayout.isErrorEnabled() || descriptionFieldLayout.isErrorEnabled() ) {
//            showMessage("Cannot create project until fields are filled out properly.", false);
//        }
//
//        else {
//
//            // Creates a dialog that appears to tell the user project creation is occurring
//            createProjectProgressDialog = new ProgressDialog(this);
//            createProjectProgressDialog.setTitle("Create Project");
//            createProjectProgressDialog.setCancelable(false);
//            createProjectProgressDialog.setMessage("Creating project...");
//            createProjectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            createProjectProgressDialog.show();
//
//            // Proceed to create project with backend authentication
//            createProjectPresenter.addProjectToDatabase(title, description);
//        }

        String desc = descriptionField.getText().toString().trim();
        if(descriptionFieldLayout.isErrorEnabled()){
            showMessage("Cannot create task.", false);
        }else{
            createTaskProgressDialog = new ProgressDialog(this);
            createTaskProgressDialog.setTitle("Create task");
            createTaskProgressDialog.setCancelable(false);
            createTaskProgressDialog.setMessage("Creating task...");
            createTaskProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            createTaskProgressDialog.show();

            createTaskProgressDialog.addTaskToDatabase(desc);
        }

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

    // NEEDS TO BE IMPLEMENTED
    @Override
    public void onProjectDeleted() {
        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(CreateTaskActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    // NEEDS TO BE IMPLEMENTED
    @Override
    public void onSprintDeleted() {

    }

    // NEEDS TO BE IMPLEMENTED
    @Override
    public void onUserStoryDeleted() {
        if (!userStoryAlreadyDeleted) {
            userStoryAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(CreateTaskActivity.this, ProductBacklogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    }

}


