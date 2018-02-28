package ca.mvp.scrumtious.scrumtious.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import ca.mvp.scrumtious.scrumtious.R;

// Class responsible for setting up snackbars for displaying
public class SnackbarHelper {

    // Display a snackbar to the context
    public static void showSnackbar(Activity context, String message){
        Snackbar.make(context.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        // Dismisses automatically
                    }
                }).setActionTextColor(context.getResources().getColor(R.color.colorAccent))
                .show();
    }

}
