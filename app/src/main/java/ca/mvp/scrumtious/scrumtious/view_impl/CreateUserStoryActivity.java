package ca.mvp.scrumtious.scrumtious.view_impl;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateUserStoryViewInt;

public class CreateUserStoryActivity extends AppCompatActivity implements CreateUserStoryViewInt {

    private EditText titleField, descriptionField, pointField;
    private TextInputLayout titleFieldLayout, descriptionFieldLayout, pointFieldLayout;

    private CreateUserStoryPresenterInt createUserStoryPresenter;
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_story);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createUserStoryPresenter = new CreateUserStoryPresenter(this, pid);
        setupFormWatcher();
    }

    @Override
    public void onBackPressed() {

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
                    descriptionFieldLayout.setError("Please enter a user story description/CoS.");
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
                String descText = pointField.getText().toString();
                boolean isDigit = android.text.TextUtils.isDigitsOnly(descText);
                if(descText == null || (descText.trim().length() <= 0) || isDigit == false){
                    descriptionFieldLayout.setErrorEnabled(true);
                    descriptionFieldLayout.setError("Please enter a user story description.");
                } else {

                    int pointsVal = Integer.valueOf(descText);
                    if (0 <= pointsVal || pointsVal > 9999) {
                        descriptionFieldLayout.setErrorEnabled(true);
                        descriptionFieldLayout.setError("Please enter a priority between 0 and 9999");
                    } else {
                        descriptionFieldLayout.setErrorEnabled(false);
                    }
                }

            }
        });
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClickCreateUserStory() {

    }

    @Override
    public void onSuccessfulCreateUserStory() {
        
    }
}
