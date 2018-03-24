package ca.mvp.scrumtious.scrumtious.view_impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ca.mvp.scrumtious.scrumtious.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Grabs the shared preferences file
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                getString(R.string.shared_preferences), Context.MODE_PRIVATE);

        // Defaults to empty string if no shared preferences string with identifier "e-mail"
        String uid = sharedPreferences.getString("emailAddress", "");
        String password = sharedPreferences.getString("password", "");
        final int loadTimeMilliseconds = 1750;

        // User was logged in
        if (!uid.equals("")){
            // Attempt sign in
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(uid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // Sign in succeeded, go to project list screen
                    if (task.isSuccessful()){
                        // User was logged in, proceed
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreenActivity.this, ProjectTabsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }, loadTimeMilliseconds);
                    }

                    // Failed to sign in, take them to login screen
                    else{
                        Snackbar.make(findViewById(android.R.id.content), "Could not log you in.", Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Automatically dismisses
                                    }
                                }).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }, loadTimeMilliseconds);
                    }
                }
            });


        }

        // User isn't logged in, go to login screen
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, loadTimeMilliseconds);
        }
    }
}
