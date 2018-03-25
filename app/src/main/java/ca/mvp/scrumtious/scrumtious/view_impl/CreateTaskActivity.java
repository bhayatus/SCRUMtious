package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateTaskViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.CreateTaskPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;



public class CreateTaskActivity extends AppCompatActivity implements
        CreateTaskViewInt {

    private CreateTaskPresenter createTaskPresenter;

    private android.support.v7.widget.Toolbar createTaskToolbar;
    private ProgressDialog createTaskProgressDialog;
    private EditText createTaskDescEditText;
    private TextInputLayout createTaskDescTextInputLayout;
    private ImageButton createTaskLogoutImageButton;
    private Button createTaskCreateButton;

    private String pid, usid;

    private ValueEventListener projectListener;
    private ValueEventListener userStoryListener;

    private boolean projectAlreadyDeleted;
    private boolean userStoryAlreadyDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        usid = data.getString("userStoryId");

        // Setting the flag
        projectAlreadyDeleted = false;
        userStoryAlreadyDeleted = false;

        createTaskPresenter = new CreateTaskPresenter(this, pid, usid);

        createTaskCreateButton = findViewById(R.id.createTaskCreateButton);
        createTaskLogoutImageButton = findViewById(R.id.createTaskLogoutImageButton);
        createTaskToolbar = findViewById(R.id.createTaskToolbar);
        createTaskCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCreateTaskSubmit(v);
            }
        });

        createTaskLogoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthenticationHelper.logout(CreateTaskActivity.this);
            }
        });

        createTaskToolbar.setNavigationIcon(R.drawable.ic_back);

        createTaskToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // This should do the same as pressing back
            }
        });

        setupFormWatcher();
    }


    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        userStoryListener = ListenerHelper.setupUserStoryDeletedListener(this, pid, usid);
        super.onResume();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        ListenerHelper.removeUserStoryDeletedListener(userStoryListener, pid, usid);
        super.onPause();
    }

    @Override
    public void onProjectDeleted() {
        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(CreateTaskActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSprintDeleted() {

    }

    // NEEDS TO BE IMPLEMENTED
    @Override
    public void onUserStoryDeleted() {
        if (!userStoryAlreadyDeleted) {
            userStoryAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(CreateTaskActivity.this, ProductBacklogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("projectId", pid);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public void onBackPressed(){

        if(createTaskDescEditText.getText().toString().trim().length() > 0){
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.LoginAlertDialog))
                    .setTitle("Discard Task?")
                    .setMessage("Are you sure you want to discard this new task?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CreateTaskActivity.this, IndividualUserStoryActivity.class);
                            intent.putExtra("projectId", pid);
                            intent.putExtra("userStoryId", usid);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }else{
            super.onBackPressed();
        }
    }

    private void setupFormWatcher(){

        createTaskDescEditText = findViewById (R.id.createTaskDescEditText);
        createTaskDescTextInputLayout = findViewById(R.id.createTaskDescTextInputLayout);

        createTaskDescTextInputLayout.setErrorEnabled(true);
        createTaskDescTextInputLayout.setError(null);

        createTaskDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String descFieldText = createTaskDescEditText.getText().toString();

                if((descFieldText == null) || (descFieldText.trim().length() <= 0)){
                    createTaskDescTextInputLayout.setErrorEnabled(true);
                    createTaskDescTextInputLayout.setError("Please enter a task description.");

                }else{
                    createTaskDescTextInputLayout.setErrorEnabled(false);
                    createTaskDescTextInputLayout.setError(null);
                }
            }

        });
    }

    // Return to task board
    public void onSuccessfulCreateTask(){

        //Stop dialog if showing
        if(createTaskProgressDialog != null && createTaskProgressDialog.isShowing()){
            createTaskProgressDialog.dismiss();
        }

        Intent intent = new Intent(CreateTaskActivity.this, IndividualUserStoryActivity.class);
        intent.putExtra("projectId", pid);
        intent.putExtra("userStoryId", usid);
        startActivity(intent);
        finish();
    }

    public void showMessage(String message, boolean showAsToast) {

        if(createTaskProgressDialog != null && createTaskProgressDialog.isShowing()){
            createTaskProgressDialog.dismiss();
        }
        if(showAsToast){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }else{
            SnackbarHelper.showSnackbar(this, message);
        }
    }

    // User clicks on create task button
    public void onClickCreateTaskSubmit(View view){

        String desc = createTaskDescEditText.getText().toString().trim();
        if(createTaskDescTextInputLayout.isErrorEnabled()){
            showMessage("Cannot create a task without a description.", false);
        }else{
            createTaskProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);;
            createTaskProgressDialog.setTitle("Create Task");
            createTaskProgressDialog.setCancelable(false);
            createTaskProgressDialog.setMessage("Creating task...");
            createTaskProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            createTaskProgressDialog.show();

            createTaskPresenter.addTaskToDatabase(desc);
        }

    }

}


