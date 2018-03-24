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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SignupViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.SignupPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;
import ca.mvp.scrumtious.scrumtious.utils.UserInputValidator;

public class SignupActivity extends AppCompatActivity implements SignupViewInt {

    private SignupPresenter signUpPresenter;

    private EditText signupEmailEditText, signupPasswordEditText, signupRetypePasswordEditText;
    private TextInputLayout signupEmailTextInputLayout, signupPasswordTextInputLayout, signupRetypePasswordTextInputLayout;
    private ProgressDialog signingInProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signUpPresenter = new SignupPresenter(this);
        setupFormWatcher();
    }

    @Override
    public void onBackPressed(){

        // Make sure user doesn't accidentally leave the screen with text filled in
        if(signupEmailEditText.getText().toString().trim().length() > 0 ||
                signupPasswordEditText.getText().toString().trim().length() > 0 ||
                signupRetypePasswordEditText.getText().toString().trim().length() > 0){

            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.LoginAlertDialog))
                    .setTitle("Leave The Screen?")
                    .setMessage("Are you sure you want to go back? You will lose anything " +
                            "you have typed in on this page.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else{
            super.onBackPressed();
        }

    }

    private void setupFormWatcher() {
        signupEmailEditText = findViewById(R.id.signupEmailEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        signupRetypePasswordEditText = findViewById(R.id.signupRetypePasswordEditText);
        signupEmailTextInputLayout = findViewById(R.id.signupEmailTextInputLayout);
        signupPasswordTextInputLayout = findViewById(R.id.signupPasswordTextInputLayout);
        signupRetypePasswordTextInputLayout = findViewById(R.id.signupRetypePasswordTextInputLayout);

        signupEmailTextInputLayout.setError(null);
        signupPasswordTextInputLayout.setError(null);
        signupRetypePasswordTextInputLayout.setError(null);
        signupEmailTextInputLayout.setErrorEnabled(true);
        signupPasswordTextInputLayout.setErrorEnabled(true);
        signupRetypePasswordTextInputLayout.setErrorEnabled(true);

        //create a watcher for emailField
        //create a listener for email field and validate it
        signupEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int isValidEmail = UserInputValidator.isValidEmail(signupEmailEditText.getText().toString().trim());

                if (isValidEmail < 0) {
                    signupEmailTextInputLayout.setErrorEnabled(true);
                    if (isValidEmail == -1)
                        signupEmailTextInputLayout.setError("Please enter an e-mail address.");
                    else if (isValidEmail == -2)
                        signupEmailTextInputLayout.setError("Please enter a valid length e-mail address.");
                    else if (isValidEmail == -3)
                        signupEmailTextInputLayout.setError("Must provide a valid e-mail address.");
                } else {
                    signupEmailTextInputLayout.setError(null);
                    signupEmailTextInputLayout.setErrorEnabled(false);
                }
            }
        });
        //create a watcher for password
        //create a listener for password
        signupPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Cannot have an empty password
                int isValidPassword = UserInputValidator.meetsPasswordCriteria(signupPasswordEditText.getText().toString().trim());
                //-1 on null password, -2 on invalid password length, -3 for invalid password (including* length), 0 for valid password
                if(isValidPassword == -1){
                    signupPasswordTextInputLayout.setErrorEnabled(true);
                    signupPasswordTextInputLayout.setError("Please enter a password.");

                }
                else if(isValidPassword == -2){
                    signupPasswordTextInputLayout.setErrorEnabled(true);
                    signupPasswordTextInputLayout.setError("Password has to be at least 8 characters.");
                }
                else if(isValidPassword == -3){
                    signupPasswordTextInputLayout.setErrorEnabled(true);
                    signupPasswordTextInputLayout.setError("Password must contain at least 1 letter and 1 digit.");
                }
                else{
                    signupPasswordTextInputLayout.setError(null);
                    signupPasswordTextInputLayout.setErrorEnabled(false);
                }
            }
        });

        signupRetypePasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!signupPasswordEditText.getText().toString().trim().equals
                        (signupRetypePasswordEditText.getText().toString().trim())){
                    signupRetypePasswordTextInputLayout.setErrorEnabled(true);
                    signupRetypePasswordTextInputLayout.setError("This does not match the password above.");
                }
                else{
                    signupRetypePasswordTextInputLayout.setError(null);
                    signupRetypePasswordTextInputLayout.setErrorEnabled(false);
                }
            }
        });

    }

    // User clicks the sign up button
    public void onClickSignUpSubmit(View view){

        String emailAddress = signupEmailEditText.getText().toString().trim();
        String password = signupPasswordEditText.getText().toString().trim();
        // If any error message is displaying, that means the form can't be submitted properly
        if(signupPasswordTextInputLayout.isErrorEnabled() || signupEmailTextInputLayout.isErrorEnabled() || signupRetypePasswordTextInputLayout.isErrorEnabled()) {
            showMessage("Cannot register until the fields are filled out properly.", false);
            return;
        }

        // Creates a dialog that appears to tell the user that the sign up is occurring
        signingInProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        signingInProgressDialog.setTitle("Sign Up");
        signingInProgressDialog.setCancelable(false);
        signingInProgressDialog.setMessage("Attempting to sign you up...");
        signingInProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        signingInProgressDialog.show();

        // Proceed to sign up user with backend authentication
        signUpPresenter.attemptSignUp(emailAddress, password);
    }

    // Sign up was successful, meaning verification e-mail was sent
    @Override
    public void onSuccessfulSignUp() {
        showMessage("Verification e-mail sent.", true);

        // Return to login screen
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void showMessage(String message, boolean showAsToast) {
        if (signingInProgressDialog != null && signingInProgressDialog.isShowing()) {
            signingInProgressDialog.dismiss();
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

}

