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

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateTaskViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateProjectPresenter;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateTaskPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;


public class CreateTaskActivity extends AppCompatActivity implements
        CreateTaskViewInt {

    private CreateTaskPresenter createTaskPresenter;

    private android.support.v7.widget.Toolbar toolbar;
    private ProgressDialog createTaskProgressDialog;

    private String pid, usid;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        usid = data.getString("userStoryId");

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

//        if(titleField.getText().toString().trim().length() > 0 || descriptionField.getText().toString().trim().length() > 0){
//            new AlertDialog.Builder(this)
//                    .setTitle("Discard Project?")
//                    .setMessage("Are you sure you to discard this new project?")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent intent = new Intent(CreateProjectActivity.this, ProjectTabsActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        }else {
//            super.onBackPressed();
//        }
    }

    private void setupFormWatcher(){
//        titleField = (EditText) findViewById(R.id.createProjectTitleField);
//        descriptionField = (EditText) findViewById(R.id.createProjectDescField);
//        titleFieldLayout = (TextInputLayout) findViewById(R.id.createProjectTitleFieldLayout);
//        descriptionFieldLayout = (TextInputLayout)
//                findViewById(R.id.createProjectDescFieldLayout);
//
//        titleFieldLayout.setError(null);
//        descriptionFieldLayout.setError(null);
//        titleFieldLayout.setErrorEnabled(true);
//        descriptionFieldLayout.setErrorEnabled(true);
//
//        //Create a watcher for titleField
//        //Create a listener for titleField and validate it
//        titleField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                String titleText = titleField.getText().toString();
//
//                if(titleText == null || (titleText.trim().length() <= 0)) {
//                    titleFieldLayout.setErrorEnabled(true);
//                    titleFieldLayout.setError("Please enter a project title.");
//                }
//                else if (titleText.trim().length() > 18){
//                    titleFieldLayout.setErrorEnabled(true);
//                    titleFieldLayout.setError("Project title is too long.");
//                }
//                else{
//                    titleFieldLayout.setErrorEnabled(false);
//                }
//            }
//        });
//
//        descriptionField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String descText = descriptionField.getText().toString();
//                if(descText == null || (descText.trim().length() <= 0)){
//                    descriptionFieldLayout.setErrorEnabled(true);
//                    descriptionFieldLayout.setError("Please enter a project description.");
//                }
//                else{
//                    descriptionFieldLayout.setErrorEnabled(false);
//                }
//
//            }
//        });

    }

    // Return to task list screen
    public void onSuccessfulCreateTask(){
//        // Stop dialog if showing
//        if (createProjectProgressDialog != null && createProjectProgressDialog.isShowing()) {
//            createProjectProgressDialog.dismiss();
//        }
//
//        // Return to project list screen
//        Intent intent = new Intent(CreateProjectActivity.this, ProjectTabsActivity.class);
//        startActivity(intent);
//        finish();

    }

    public void showMessage(String message, boolean showAsToast) {
//        // Stop dialog if showing
//        if (createProjectProgressDialog != null && createProjectProgressDialog.isShowing()) {
//            createProjectProgressDialog.dismiss();
//        }
//
//        // Show message in toast so it persists across activity transitions
//        if (showAsToast){
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        }
//
//        else {
//            // Call the utils class method to handle making the snackbar
//            SnackbarHelper.showSnackbar(this, message);
//        }
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

    }

    // NEEDS TO BE IMPLEMENTED
    @Override
    public void onProjectDeleted() {

    }

    // NEEDS TO BE IMPLEMENTED
    @Override
    public void onSprintDeleted() {

    }

    // NEEDS TO BE IMPLEMENTED
    @Override
    public void onUserStoryDeleted() {

    }

}

