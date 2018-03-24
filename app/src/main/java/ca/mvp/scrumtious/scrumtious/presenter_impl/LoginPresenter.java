package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.LoginPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.LoginViewInt;

public class LoginPresenter implements LoginPresenterInt {

    private FirebaseAuth firebaseAuth;
    private LoginViewInt loginScreenView;

    public LoginPresenter(LoginViewInt loginScreenView) {
        this.loginScreenView = loginScreenView;
    }

    @Override
    public void attemptLogin(Activity context, final String emailAddress, final String password) {
        firebaseAuth = FirebaseAuth.getInstance();
        // Attempt the sign in
        firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(context,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(!user.isEmailVerified()){
                                loginScreenView.showMessage("Your e-mail address isn't verified yet.", false);
                            } else {
                                loginScreenView.onSuccessfulLogin(emailAddress, password);
                            }
                        }
                        // Login failed
                        else {
                            loginScreenView.showMessage("Login failed.", false);
                        }
                    }
                });
    }

}
