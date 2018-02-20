package ca.mvp.scrumtious.scrumtious.view_impl;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateUserStoryViewInt;

public class CreateUserStoryActivity extends AppCompatActivity implements CreateUserStoryViewInt {

    private CreateUserStoryPresenterInt createUserStoryPresenter;
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_story);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createUserStoryPresenter = new CreateUserStoryPresenter(this, pid);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClickCreateUserStory() {

    }
}
