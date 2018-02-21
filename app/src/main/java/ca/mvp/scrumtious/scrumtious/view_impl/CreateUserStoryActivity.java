package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateUserStoryPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateUserStoryViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateUserStoryPresenter;

public class CreateUserStoryActivity extends AppCompatActivity implements CreateUserStoryViewInt {

    private EditText titleField, descriptionField, pointField;
    private TextInputLayout titleFieldLayout, descriptionFieldLayout, pointFieldLayout;

    private CreateUserStoryPresenterInt createUserStoryPresenter;
    private String pid;
    private ProgressDialog createUserStoryProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_story);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        createUserStoryPresenter = new CreateUserStoryPresenter(this, pid);
        setupFormWatcher();
    }

    @Override
    public void onBackPressed() {
        if(titleField.getText().toString().trim().length() > 0 || descriptionField.getText().toString().trim().length() > 0 ||
                pointField.getText().toString().trim().length() > 0){
            new AlertDialog.Builder(this)
                    .setTitle("Discard User Story?")
                    .setMessage("Are you sure you to discard this new user story?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CreateUserStoryActivity.this, ProductBacklogActivity.class);
                            intent.putExtra("projectId", pid);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }else {
            super.onBackPressed();
        }
    }

    public void setupFormWatcher() {
        titleField = (EditText) findViewById(R.id.createUserStoryScreenNameField);
        pointField = (EditText) findViewById(R.id.createUserStoryScreenPointsField);
        descriptionField = (EditText) findViewById(R.id.createUserStoryScreenDescField);
        titleFieldLayout = (TextInputLayout) findViewById(R.id.createUserStoryScreenTitleFieldLayout);
        descriptionFieldLayout = (TextInputLayout) findViewById(R.id.createUserStoryScreenDescFieldLayout);
        pointFieldLayout = (TextInputLayout) findViewById(R.id.createUserStoryScreenPointsFieldLayout);

        titleFieldLayout.setError(null);
        pointField.setError(null);
        descriptionFieldLayout.setError(null);
        titleFieldLayout.setErrorEnabled(true);
        pointFieldLayout.setErrorEnabled(true);
        descriptionFieldLayout.setErrorEnabled(true);

        //Create a watcher for titleField
        //Create a listener for titleField and validate it
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String titleText = titleField.getText().toString();

                if(titleText == null || (titleText.trim().length() <= 0
                        ||  titleText.trim().length() > 254)) {
                    titleFieldLayout.setErrorEnabled(true);
                    titleFieldLayout.setError("Please enter a user story name.");
                }else{
                    titleFieldLayout.setErrorEnabled(false);
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
                String descText = descriptionField.getText().toString();
                if(descText == null || (descText.trim().length() <= 0
                        || descText.trim().length() > 2048)){
                    descriptionFieldLayout.setErrorEnabled(true);
                    descriptionFieldLayout.setError("Please enter a user story description.");
                }else{
                    descriptionFieldLayout.setErrorEnabled(false);
                }

            }
        });

        pointField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pointsText = pointField.getText().toString().trim();

                if (pointsText == null || (pointsText.trim().length() <= 0)) {
                    pointFieldLayout.setErrorEnabled(true);
                    pointFieldLayout.setError("Please enter a user story priority.");
                }
                else{

                int pointsVal = Integer.valueOf(pointsText);
                if (pointsVal <= 0 || pointsVal > 9999) {
                    pointFieldLayout.setErrorEnabled(true);
                    pointFieldLayout.setError("Please enter an amount of points between 0 and 9999.");
                } else {
                    pointFieldLayout.setErrorEnabled(false);
                }
            }
            }
        });
    }

    @Override
    public void showMessage(String message) {
        if (createUserStoryProgressDialog != null && createUserStoryProgressDialog.isShowing()) {
            createUserStoryProgressDialog.dismiss();
        }
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void onClickCreateUserStory(View view) {

        if(titleFieldLayout.isErrorEnabled() || descriptionFieldLayout.isErrorEnabled()
                || pointFieldLayout.isErrorEnabled()) {
            showMessage("Cannot create user story until fields are filled out properly.");
        } else {

            String title = titleField.getText().toString().trim();
            String desc = descriptionField.getText().toString().trim();
            int points = Integer.valueOf(pointField.getText().toString().trim());

            // Creates a dialog that appears to tell the user that creating the project is occurring
            createUserStoryProgressDialog = new ProgressDialog(this);
            createUserStoryProgressDialog.setTitle("Create User Story");
            createUserStoryProgressDialog.setCancelable(false);
            createUserStoryProgressDialog.setMessage("Creating user story...");
            createUserStoryProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            createUserStoryProgressDialog.show();

            createUserStoryPresenter.addUserStoryToDatabase(title, points, desc);
        }
    }

    @Override
    public void onSuccessfulCreateUserStory() {
        if (createUserStoryProgressDialog != null && createUserStoryProgressDialog.isShowing()) {
            createUserStoryProgressDialog.dismiss();
        }
        Intent intent = new Intent(CreateUserStoryActivity.this, ProductBacklogActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
        finish();
    }

    // Project no longer exists to user, must go back to project list screen
    @Override
    public void onProjectDeleted() {
        // Return to project list screen and make sure we can't go back by clearing the task stack
        Intent intent = new Intent(CreateUserStoryActivity.this, ProjectTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
