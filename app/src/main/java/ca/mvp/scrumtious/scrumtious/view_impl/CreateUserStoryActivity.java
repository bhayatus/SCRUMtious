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
import android.view.ContextThemeWrapper;
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

    private EditText createUserStoryNameEditText, createUserStoryDescEditText, createUserStoryPointsEditText;
    private TextInputLayout createUserStoryTitleTextInputLayout, createUserStoryDescTextInputLayout, createUserStoryPointsTextInputLayout;
    private ImageButton createUserStoryLogoutImageButton;
    private Toolbar createUserStoryToolbar;
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

        createUserStoryLogoutImageButton = findViewById(R.id.createUserStoryLogoutImageButton);
        createUserStoryToolbar = findViewById(R.id.createUserStoryToolbar);

        createUserStoryLogoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(CreateUserStoryActivity.this);
            }
        });

        createUserStoryToolbar.setNavigationIcon(R.drawable.ic_back);

        createUserStoryToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        if(createUserStoryNameEditText.getText().toString().trim().length() > 0 || createUserStoryDescEditText.getText().toString().trim().length() > 0 ||
                createUserStoryPointsEditText.getText().toString().trim().length() > 0){
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.LoginAlertDialog))
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
        createUserStoryNameEditText = findViewById(R.id.createUserStoryNameEditText);
        createUserStoryDescEditText = findViewById(R.id.createUserStoryDescEditText);
        createUserStoryPointsEditText = findViewById(R.id.createUserStoryPointsEditText);

        createUserStoryTitleTextInputLayout = findViewById(R.id.createUserStoryTitleTextInputLayout);
        createUserStoryDescTextInputLayout = findViewById(R.id.createUserStoryDescTextInputLayout);
        createUserStoryPointsTextInputLayout = findViewById(R.id.createUserStoryPointsTextInputLayout);

        createUserStoryTitleTextInputLayout.setError(null);
        createUserStoryDescTextInputLayout.setError(null);
        createUserStoryPointsTextInputLayout.setError(null);

        createUserStoryTitleTextInputLayout.setErrorEnabled(true);
        createUserStoryPointsTextInputLayout.setErrorEnabled(true);
        createUserStoryDescTextInputLayout.setErrorEnabled(true);

        //Create a watcher for titleField
        //Create a listener for titleField and validate it
        createUserStoryNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String titleText = createUserStoryNameEditText.getText().toString();

                if(titleText == null || (titleText.trim().length() <= 0
                        )) {
                    createUserStoryTitleTextInputLayout.setErrorEnabled(true);
                    createUserStoryTitleTextInputLayout.setError("Please enter a user story name.");
                }
                else if (titleText.trim().length() > 28){
                    createUserStoryTitleTextInputLayout.setErrorEnabled(true);
                    createUserStoryTitleTextInputLayout.setError("User story name is too long.");
                }
                else{
                    createUserStoryTitleTextInputLayout.setErrorEnabled(false);
                    createUserStoryTitleTextInputLayout.setError(null);
                }
            }
        });

        createUserStoryDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String descText = createUserStoryDescEditText.getText().toString();
                if(descText == null || (descText.trim().length() <= 0)){
                    createUserStoryDescTextInputLayout.setErrorEnabled(true);
                    createUserStoryDescTextInputLayout.setError("Please enter a user story description.");
                }
                    else{
                    createUserStoryDescTextInputLayout.setErrorEnabled(false);
                    createUserStoryDescTextInputLayout.setError(null);
                }

            }
        });

        createUserStoryPointsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pointsText = createUserStoryPointsEditText.getText().toString().trim();

                if (pointsText == null || (pointsText.trim().length() <= 0)) {
                    createUserStoryPointsTextInputLayout.setErrorEnabled(true);
                    createUserStoryPointsTextInputLayout.setError("Please enter a user story priority.");
                }
                else{

                int pointsVal = Integer.valueOf(pointsText);
                if (pointsVal <= 0 || pointsVal > 9999) {
                    createUserStoryPointsTextInputLayout.setErrorEnabled(true);
                    createUserStoryPointsTextInputLayout.setError("Please enter an amount between 0 and 9999.");
                } else {
                    createUserStoryPointsTextInputLayout.setErrorEnabled(false);
                    createUserStoryPointsTextInputLayout.setError(null);
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
        if(createUserStoryTitleTextInputLayout.isErrorEnabled() || createUserStoryDescTextInputLayout.isErrorEnabled()
                || createUserStoryPointsTextInputLayout.isErrorEnabled()) {
            showMessage("Cannot create the user story until fields are filled out properly.", false);
        } else {

            String title = createUserStoryNameEditText.getText().toString().trim();
            String desc = createUserStoryDescEditText.getText().toString().trim();
            int points = Integer.valueOf(createUserStoryPointsEditText.getText().toString().trim());

            // Creates a dialog that appears to tell the user that the user story is being created
            createUserStoryProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);;
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
