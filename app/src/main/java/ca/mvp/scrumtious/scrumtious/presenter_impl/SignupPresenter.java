package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SignupPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SignupViewInt;

public class SignupPresenter implements SignupPresenterInt {

    private FirebaseAuth firebaseAuth;

    private SignupViewInt signupView;

    public SignupPresenter(SignupViewInt signupView){
        this.signupView = signupView;
    }

    @Override
    public void attemptSignUp(String emailAddress, String password) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Send e-mail verification
                    sendEmailVerification();

                }
                else{
                    // Sign up failed, tell user
                    signupView.showMessage(task.getException().getLocalizedMessage(), false);
                }
            }
        });


    }

    private void sendEmailVerification(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Sign up was successful, user should change activities
                    setupUserDatabase();
                }
                // Failed to send verification e-mail, remove user from authentication and let them know
                else{
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.getCurrentUser().delete();
                    signupView.showMessage("An error occurred during the registration process, please try again.", false);
                }


            }
        });

    }

    // Add user to our Firebase database, outside of the authentication
    private void setupUserDatabase(){
        firebaseAuth = FirebaseAuth.getInstance();

        // Create the user object to store in Firebase after creating an account
        String emailAddress = firebaseAuth.getCurrentUser().getEmail();
        String userID = firebaseAuth.getCurrentUser().getUid();

        // Adds new user to database using their unique UserID as the key (instead
        // of the usual push ID that Firebase uses)
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference firebaseRootReference = firebaseDatabase.getReference();

        Map userMap = new HashMap<>();
        userMap.put("/users/" + userID + "/" + "emailAddress", emailAddress);
        userMap.put("/users/" + userID + "/" + "userID", userID);

        firebaseRootReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    signupView.onSuccessfulSignUp();
                }
                else{
                    // Failed to sign up user, remove them from authentication and tell them
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.getCurrentUser().delete();
                    signupView.showMessage("An error occurred during the registration process, please try again.", false);
                }
            }
        });

    }

}
