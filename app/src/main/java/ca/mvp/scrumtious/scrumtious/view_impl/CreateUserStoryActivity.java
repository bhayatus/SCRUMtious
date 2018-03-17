package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.database.ValueEventListener;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateUserStoryPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateUserStoryViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateUserStoryPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class CreateUserStoryActivity extends AppCompatActivity implements CreateUserStoryViewInt {

    private CreateUserStoryPresenterInt createUserStoryPresenter;
    private String pid;
    private ValueEventListener projectListener;

    private EditText titleField, descriptionField, pointField;
    private TextInputLayout titleFieldLayout, descriptionFieldLayout, pointFieldLayout;
    private ImageButton logoutBtn;
    private Toolbar toolbar;
    private ProgressDialog createUserStoryProgressDialog;

    private boolean projectAlreadyDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_story);

        projectAlreadyDeleted = false; // Project isn't deleted at this point

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        createUserStoryPresenter = new CreateUserStoryPresenter(this, pid);

        logoutBtn = findViewById(R.id.createUserStoryLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(CreateUserStoryActivity.this);
            }
        });

        toolbar = findViewById(R.id.createUserStoryToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Should do the same as the user pressing back
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

    // Prevent user from accidentally leaving if form is filled in
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

    // Project was deleted by another user, or user was removed from the project
    @Override
    public void onProjectDeleted() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(CreateUserStoryActivity.this, ProjectTabsActivity.class);
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

    public void setupFormWatcher() {
        titleField = (EditText) findViewById(R.id.createUserStoryNameField);
        descriptionField = (EditText) findViewById(R.id.createUserStoryDescField);
        pointField = (EditText) findViewById(R.id.createUserStoryPointsField);

        titleFieldLayout = (TextInputLayout) findViewById(R.id.createUserStoryTitleFieldLayout);
        descriptionFieldLayout = (TextInputLayout) findViewById(R.id.createUserStoryDescFieldLayout);
        pointFieldLayout = (TextInputLayout) findViewById(R.id.createUserStoryPointsFieldLayout);

        titleFieldLayout.setError(null);
        descriptionFieldLayout.setError(null);
        pointFieldLayout.setError(null);

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
                        )) {
                    titleFieldLayout.setErrorEnabled(true);
                    titleFieldLayout.setError("Please enter a user story name.");
                }
                else if (titleText.trim().length() > 28){
                    titleFieldLayout.setErrorEnabled(true);
                    titleFieldLayout.setError("User story name is too long.");
                }
                else{
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
                String descText = descriptionField.getText().toString();
                if(descText == null || (descText.trim().length() <= 0)){
                    descriptionFieldLayout.setErrorEnabled(true);
                    descriptionFieldLayout.setError("Please enter a user story description.");
                }
                    else{
                    descriptionFieldLayout.setErrorEnabled(false);
                    descriptionFieldLayout.setError(null);
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
                    pointFieldLayout.setError("Please enter an amount between 0 and 9999.");
                } else {
                    pointFieldLayout.setErrorEnabled(false);
                    pointFieldLayout.setError(null);
                }
            }
            }
        });
    }

    @Override
    public void showMessage(String message, boolean showAsToast) {
        if (createUserStoryProgressDialog != null && createUserStoryProgressDialog.isShowing()) {
            createUserStoryProgressDialog.dismiss();
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

    public void onClickCreateUserStory(View view) {

        // If any of the errors are showing, cannot proceed
        if(titleFieldLayout.isErrorEnabled() || descriptionFieldLayout.isErrorEnabled()
                || pointFieldLayout.isErrorEnabled()) {
            showMessage("Cannot create user story until fields are filled out properly.", false);
        } else {

            String title = titleField.getText().toString().trim();
            String desc = descriptionField.getText().toString().trim();
            int points = Integer.valueOf(pointField.getText().toString().trim());

            // Creates a dialog that appears to tell the user that the user story is being created
            createUserStoryProgressDialog = new ProgressDialog(this);
            createUserStoryProgressDialog.setTitle("Create User Story");
            createUserStoryProgressDialog.setCancelable(false);
            createUserStoryProgressDialog.setMessage("Creating user story...");
            createUserStoryProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            createUserStoryProgressDialog.show();

            createUserStoryPresenter.addUserStoryToDatabase(title, points, desc);
        }
    }

    // User story has been created, return to product backlog
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
}
