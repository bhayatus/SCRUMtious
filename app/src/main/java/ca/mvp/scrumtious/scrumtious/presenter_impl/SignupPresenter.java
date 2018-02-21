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
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SignupPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SignupViewInt;

public class SignupPresenter implements SignupPresenterInt {

    private SignupViewInt signupScreenView;
    private FirebaseAuth mAuth;

    public SignupPresenter(SignupViewInt signupScreenView){
        this.signupScreenView = signupScreenView;
    }

    @Override
    public void attemptSignUp(String emailAddress, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Send e-mail verification
                    sendEmailVerification();

                }
                else{
                    // Sign up failed, tell user
                    signupScreenView.showMessage(task.getException().getLocalizedMessage());
                }
            }
        });


    }

    private void sendEmailVerification(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Sign up was successful, user should change activities
                    setupUserDatabase();
                }
            }
        });

    }

    // Add user to our Firebase database
    private void setupUserDatabase(){

        mAuth = FirebaseAuth.getInstance();

        // Create the user object to store in Firebase after creating an account
        String emailAddress = mAuth.getCurrentUser().getEmail();
        String userID = mAuth.getCurrentUser().getUid();

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("emailAddress", emailAddress);
        userMap.put("userID", userID);

        // Adds new user to database using their unique UID as the key (instead
        // of the usual push ID that Firebase uses)
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference();
        mRef.child("users").child(userID).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                signupScreenView.onSuccessfulSignUp();
            }
        });

    }

}
