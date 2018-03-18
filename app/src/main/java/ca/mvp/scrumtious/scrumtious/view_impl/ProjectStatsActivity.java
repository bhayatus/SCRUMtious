package ca.mvp.scrumtious.scrumtious.view_impl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectStatsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectStatsViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectStatsPresenter;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;

public class ProjectStatsActivity extends AppCompatActivity implements ProjectStatsViewInt {

    private ProjectStatsPresenterInt projectStatsPresenter;
    private String pid;
    private ValueEventListener projectListener;
    private boolean projectAlreadyDeleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_stats);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        this.projectStatsPresenter = new ProjectStatsPresenter(this, pid);

        this.projectAlreadyDeleted = false;
    }

    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        super.onResume();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        super.onPause();
    }

    // TODO
    @Override
    public void onProjectDeleted() {

    }

    @Override
    public void onSprintDeleted() {

    }

    @Override
    public void onUserStoryDeleted() {

    }
}
