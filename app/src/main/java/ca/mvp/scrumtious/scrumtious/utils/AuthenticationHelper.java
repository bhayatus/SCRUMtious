package ca.mvp.scrumtious.scrumtious.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.view_impl.LoginActivity;

// Class responsible for logging user out and removing e-mail, password from SharedPreferences
public class AuthenticationHelper {

    public static void logout(final Activity context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Log Out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        if (mAuth.getCurrentUser() != null){
                            mAuth.signOut();

                            SharedPreferences sharedPreferences = context.getSharedPreferences(
                                    context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.remove("emailAddress");
                            editor.apply();
                            editor.remove("password");
                            editor.apply();

                            // Return to login screen
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            context.finish();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remain in this screen
                    }
                })
                .create().show();


    }

}
