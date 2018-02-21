package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectListScreenPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectListScreenViewInt;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectListScreenFragment;

public class ProjectListScreenPresenter implements ProjectListScreenPresenterInt {
    private ProjectListScreenViewInt viewProjectsScreenView;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private Query mQuery;
    public ProjectListScreenPresenter(ProjectListScreenViewInt viewProjectsScreenView){
        this.viewProjectsScreenView = viewProjectsScreenView;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> setupGeneralProjectsAdapter(RecyclerView projectList, ProgressDialog loadingProjectsDialog) {
        final ProgressDialog dialog = loadingProjectsDialog;
        //just to be safe if constructor wasn't called before
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        String userID = mAuth.getCurrentUser().getUid();
        mQuery = rootRef.child("projects").orderByChild(userID).equalTo("member");

        FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> projectListAdapter
                = new FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder>(
                Project.class,
                R.layout.project_row,
                ProjectListScreenFragment.ProjectsViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(ProjectListScreenFragment.ProjectsViewHolder viewHolder, Project model, int position) {
                viewHolder.setDetails(model.getProjectTitle(), model.getProjectOwnerEmail(), model.getProjectDesc());
                final String pid = getRef(position).getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewProjectsScreenView.goToProjectScreen(pid);
                    }
                });
            }
            @Override
            public void onDataChanged() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        return projectListAdapter;
    }

    @Override
    public FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> setupMyProjectsAdapter(RecyclerView projectList, ProgressDialog loadingProjectsDialog) {
        final ProgressDialog dialog = loadingProjectsDialog;
        //just to be safe if constructor wasn't called before
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        String userID = mAuth.getCurrentUser().getUid();
        mQuery = rootRef.child("projects").orderByChild("projectOwnerUid").equalTo(userID);

        FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> projectListAdapter
                = new FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder>(
                Project.class,
                R.layout.project_row,
                ProjectListScreenFragment.ProjectsViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(ProjectListScreenFragment.ProjectsViewHolder viewHolder, Project model, int position) {
                viewHolder.setDetails(model.getProjectTitle(), model.getProjectOwnerEmail(), model.getProjectDesc());
                final String pid = getRef(position).getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewProjectsScreenView.goToProjectScreen(pid);
                    }
                });
            }
            @Override
            public void onDataChanged() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        return projectListAdapter;
    }

}

