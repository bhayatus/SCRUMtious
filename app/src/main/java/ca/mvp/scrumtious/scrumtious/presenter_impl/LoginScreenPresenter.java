package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.LoginScreenPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.LoginScreenViewInt;

public class LoginScreenPresenter implements LoginScreenPresenterInt{

    private FirebaseAuth firebaseAuth;
    private LoginScreenViewInt loginScreenView;

    public LoginScreenPresenter(LoginScreenViewInt loginScreenView) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.loginScreenView = loginScreenView;
    }

    @Override
    public void attemptLogin(Activity context, String emailAddress, String password) {
        firebaseAuth = FirebaseAuth.getInstance();
        // Attempt the sign in
        firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(context,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(!user.isEmailVerified()){
                                loginScreenView.loginExceptionMessage("You e-mail address isn't verified yet");
                            }else {
                                loginScreenView.onSuccessfulLogin();
                            }
                        }
                        else loginScreenView.loginExceptionMessage("Invalid login credentials");
                    }
                });
    }

    @Override
    public void signOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}
