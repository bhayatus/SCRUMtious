package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import javax.swing.text.View;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SignupScreenViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.SignupScreenPresenter;
import ca.mvp.scrumtious.scrumtious.utils.UserInputValidator;

public class SignupScreenActivity extends AppCompatActivity implements SignupScreenViewInt {

    private EditText emailField, passwordField, retypePasswordField;
    private TextInputLayout emailFieldLayout, passwordFieldLayout, retypePasswordFieldLayout;

    private ProgressDialog signingInProgressDialog;
    private SignupScreenPresenter signUpScreenPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        signUpScreenPresenter = new SignupScreenPresenter(this);
        setupFormWatcher();
    }

    private void setupFormWatcher() {
        //replace thing with emailField name
        emailField = (EditText) findViewById(R.id.signupScreenEmailField);
        //replace thing2 with passwordField name
        passwordField = (EditText) findViewById(R.id.signupScreenPasswordField);
        //replace thing3 with retypePasswordField name
        retypePasswordField = (EditText) findViewById(R.id.signupScreenRetypePasswordField);
        //replace thingLayout with emailFieldLayout name
        emailFieldLayout = (TextInputLayout) findViewById(R.id.signupScreenEmailFieldLayout);
        //replace thing2Layout with passwordFieldLayout name
        passwordFieldLayout = (TextInputLayout) findViewById(R.id.signupScreenPasswordFieldLayout);
        //replace thing3Layout with retypePasswordFieldLayout name
        retypePasswordFieldLayout = (TextInputLayout)
                findViewById(R.id.signupScreenRetypePasswordFieldLayout);

        emailFieldLayout.setError(null);
        passwordFieldLayout.setError(null);
        retypePasswordFieldLayout.setError(null);
        emailFieldLayout.setErrorEnabled(true);
        passwordFieldLayout.setErrorEnabled(true);
        retypePasswordFieldLayout.setErrorEnabled(true);

        //create a watcher for emailField
        //create a listener for email field and validate it
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int isValidEmail = UserInputValidator.isValidEmail(emailField.getText().toString().trim());

                if (isValidEmail < 0) {
                    emailFieldLayout.setErrorEnabled(true);
                    if (isValidEmail == -1)
                        emailFieldLayout.setError("Please enter an email address.");
                    else if (isValidEmail == -2)
                        emailFieldLayout.setError("Please enter a valid length email address.");
                    else if (isValidEmail == -3)
                        emailFieldLayout.setError("Must provide a valid email address.");
                } else {
                    emailFieldLayout.setError(null);
                    emailFieldLayout.setErrorEnabled(false);
                }
            }
        });
        //create a watcher for password
        //create a listener for password
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Cannot have an empty password
                int isValidPassword = UserInputValidator.meetsPasswordCriteria(passwordField.getText().toString().trim());
                //-1 on null password, -2 on invalid password length, -3 for invalid password (including* length), 0 for valid password
                if(isValidPassword == -1){
                    passwordFieldLayout.setErrorEnabled(true);
                    passwordFieldLayout.setError("please enter a password");

                }
                else if(isValidPassword == -2){
                    passwordFieldLayout.setErrorEnabled(true);
                    passwordFieldLayout.setError("password has to be between 8-254 characters");
                }
                else if(isValidPassword == -3){
                    passwordFieldLayout.setErrorEnabled(true);
                    passwordFieldLayout.setError("atleast 1 character, 1 digit, and 1 special character");
                }
                else{
                    passwordFieldLayout.setError(null);
                    passwordFieldLayout.setErrorEnabled(false);
                }
            }
        });
        //create a watcher for retypePassword
        //create a listener for retypePassword
        retypePasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (passwordField.getText().toString().trim().equals
                        (retypePasswordField.getText().toString().trim())){
                    retypePasswordFieldLayout.setErrorEnabled(true);
                    retypePasswordFieldLayout.setError("This does not match the password");
                }
                else{
                    retypePasswordFieldLayout.setError(null);
                    retypePasswordFieldLayout.setErrorEnabled(false);
                }
            }
        });

    }

    public void onClickSignUpSubmit(View view){
        emailField = (EditText) findViewById(R.id.signupScreenEmailField);
        passwordField = (EditText) findViewById(R.id.signupScreenPasswordField);

        String emailAddress = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        // If either error message is displaying, that means the form can't be submitted properly
        if(passwordFieldLayout.isErrorEnabled() || emailFieldLayout.isErrorEnabled() || retypePasswordFieldLayout.isErrorEnabled()) {
            signUpExceptionMessage("Cannot submit until the fields are filled out properly");
            return;
        }
        // Creates a dialog that appears to tell the user that the sign up is occurring
        signingInProgressDialog = new ProgressDialog(this);
        signingInProgressDialog.setTitle("Sign up");
        signingInProgressDialog.setCancelable(false);
        signingInProgressDialog.setMessage("Attempting to sign you up...");
        signingInProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        signingInProgressDialog.show();

        // Proceed to sign up user with backend authentication
        signUpScreenPresenter.attemptSignUp(emailAddress, password);
    }

    @Override
    public void signUpExceptionMessage(String error) {
        signingInProgressDialog.dismiss();
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessfulSignUp() {
        Toast.makeText(this,"Verification e-mail sent!",Toast.LENGTH_SHORT).show();
        signingInProgressDialog.dismiss();
        Intent intent = new Intent(SignupScreenActivity.this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
