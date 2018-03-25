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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateProjectViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateProjectPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;


public class CreateProjectActivity extends AppCompatActivity implements
        CreateProjectViewInt {

    private CreateProjectPresenter createProjectPresenter;

    private EditText createProjectTitleEditText, createProjectDescEditText;
    private TextInputLayout createProjectTitleTextInputLayout, createProjectDescTextInputLayout;
    private ImageButton createProjectLogoutBtn;
    private android.support.v7.widget.Toolbar createProjectToolbar;
    private ProgressDialog createProjectProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        createProjectPresenter = new CreateProjectPresenter(this);

        createProjectLogoutBtn = findViewById(R.id.createProjectLogoutBtn);
        createProjectToolbar = findViewById(R.id.createProjectToolbar);


        createProjectLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(CreateProjectActivity.this);
            }
        });

        createProjectToolbar.setNavigationIcon(R.drawable.ic_back);

        createProjectToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // This should do the same as pressing back
            }
        });

        setupFormWatcher();
    }


    @Override
    public void onBackPressed(){

        if(createProjectTitleEditText.getText().toString().trim().length() > 0 || createProjectDescEditText.getText().toString().trim().length() > 0){
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.LoginAlertDialog))
                    .setTitle("Discard Project?")
                    .setMessage("Are you sure you to discard this new project?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CreateProjectActivity.this, ProjectTabsActivity.class);
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



    private void setupFormWatcher(){
        createProjectTitleEditText = findViewById(R.id.createProjectTitleEditText);
        createProjectDescEditText = findViewById(R.id.createProjectDescEditText);
        createProjectTitleTextInputLayout = findViewById(R.id.createProjectTitleTextInputLayout);
        createProjectDescTextInputLayout = findViewById(R.id.createProjectDescTextInputLayout);

        createProjectTitleTextInputLayout.setError(null);
        createProjectDescTextInputLayout.setError(null);
        createProjectTitleTextInputLayout.setErrorEnabled(true);
        createProjectDescTextInputLayout.setErrorEnabled(true);

        //Create a watcher for createProjectTitleEditText
        //Create a listener for createProjectTitleEditText and validate it
        createProjectTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String titleText = createProjectTitleEditText.getText().toString();

                if(titleText == null || (titleText.trim().length() <= 0)) {
                    createProjectTitleTextInputLayout.setErrorEnabled(true);
                    createProjectTitleTextInputLayout.setError("Please enter a project title.");
                }
                else if (titleText.trim().length() > 18){
                    createProjectTitleTextInputLayout.setErrorEnabled(true);
                    createProjectTitleTextInputLayout.setError("Project title is too long.");
                }
                else{
                    createProjectTitleTextInputLayout.setErrorEnabled(false);
                }
            }
        });

        createProjectDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String descText = createProjectDescEditText.getText().toString();
                if(descText == null || (descText.trim().length() <= 0)){
                    createProjectDescTextInputLayout.setErrorEnabled(true);
                    createProjectDescTextInputLayout.setError("Please enter a project description.");
                }
                else{
                    createProjectDescTextInputLayout.setErrorEnabled(false);
                }

            }
        });
    }

    // Return to project list screen
    public void onSuccessfulCreateProject(){
        // Stop dialog if showing
        if (createProjectProgressDialog != null && createProjectProgressDialog.isShowing()) {
            createProjectProgressDialog.dismiss();
        }

        // Return to project list screen
        Intent intent = new Intent(CreateProjectActivity.this, ProjectTabsActivity.class);
        startActivity(intent);
        finish();

    }

    public void showMessage(String message, boolean showAsToast) {
        // Stop dialog if showing
        if (createProjectProgressDialog != null && createProjectProgressDialog.isShowing()) {
            createProjectProgressDialog.dismiss();
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

    // User clicks on create project button
    public void onClickCreateProjectSubmit(View view){
        String title = createProjectTitleEditText.getText().toString().trim();
        String description = createProjectDescEditText.getText().toString().trim();
        // If either error message is displaying, that means the form can't be submitted properly
        if(createProjectTitleTextInputLayout.isErrorEnabled() || createProjectDescTextInputLayout.isErrorEnabled() ) {
            showMessage("Cannot create the project until fields are filled out properly.", false);
        }

        else {

            // Creates a dialog that appears to tell the user project creation is occurring
            createProjectProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);;
            createProjectProgressDialog.setTitle("Create Project");
            createProjectProgressDialog.setCancelable(false);
            createProjectProgressDialog.setMessage("Creating project...");
            createProjectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            createProjectProgressDialog.show();

            // Proceed to create project with backend authentication
            createProjectPresenter.addProjectToDatabase(title, description);
        }
    }

}

