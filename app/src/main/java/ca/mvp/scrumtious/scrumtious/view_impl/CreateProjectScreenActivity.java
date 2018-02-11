package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateProjectScreenViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateProjectScreenPresenter;


public class CreateProjectScreenActivity extends AppCompatActivity implements
        CreateProjectScreenViewInt {

    private EditText titleField, descriptionField;
    private TextInputLayout titleFieldLayout, descriptionFieldLayout;

    private ProgressDialog createProjectProgressDialog;
    private CreateProjectScreenPresenter createProjectScreenPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTitle("Create Project");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project_screen);
        createProjectScreenPresenter = new CreateProjectScreenPresenter(this);
        setupFormWatcher();
    }

    private void setupFormWatcher(){
        titleField = (EditText) findViewById(R.id.createProjectScreenTitleField);
        descriptionField = (EditText) findViewById(R.id.createProjectScreenDescField);
        titleFieldLayout = (TextInputLayout) findViewById(R.id.createProjectScreenTitleFieldLayout);
        descriptionFieldLayout = (TextInputLayout)
                findViewById(R.id.createProjectScreenDescFieldLayout);

        titleFieldLayout.setError(null);
        descriptionFieldLayout.setError(null);
        titleFieldLayout.setErrorEnabled(true);
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
                    titleFieldLayout.setError("Please enter a project title");
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
                        || descText.trim().length()> 512)){
                    descriptionFieldLayout.setErrorEnabled(true);
                    descriptionFieldLayout.setError("Please enter a project description.");
                }

            }
        });
    }


    public void onSuccessfulCreateProject(){
        Toast.makeText(this, "Created Project"+titleField.getText().toString().trim(), Toast.LENGTH_SHORT).show();
        createProjectProgressDialog.dismiss();
        Intent intent = new Intent(CreateProjectScreenActivity.this, ProjectTabsScreenActivity.class);
        startActivity(intent);
        finish();

    }

    public void createProjectExceptionMessage(String error) {
        createProjectProgressDialog.dismiss();
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
    }

    public void onClickCreateProjectSubmit(View view){
        String title = titleField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();
        // If either error message is displaying, that means the form can't be submitted properly
        if(titleFieldLayout.isErrorEnabled() || descriptionFieldLayout.isErrorEnabled() ) {
            Toast.makeText(this, "Cannot create projects until fields are filled out properly", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creates a dialog that appears to tell the user that creating the project is occurring
        createProjectProgressDialog = new ProgressDialog(this);
        createProjectProgressDialog.setTitle("Create Project");
        createProjectProgressDialog.setCancelable(false);
        createProjectProgressDialog.setMessage("Creating project...");
        createProjectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        createProjectProgressDialog.show();

        // Proceed to create project with backend authentication
        createProjectScreenPresenter.addProjectToDatabase(title, description);
    }

    @Override
    public void onBackPressed(){

        if(titleField.getText().toString().trim().length() > 0 || descriptionField.getText().toString().trim().length() > 0){
            new AlertDialog.Builder(this)
                    .setTitle("Delete Project?")
                    .setMessage("Are you sure you want to delete the project?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CreateProjectScreenActivity.this, ProjectTabsScreenActivity.class);
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






}

