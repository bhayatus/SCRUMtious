package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.app.ProgressDialog;
import android.content.Context;
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

/**
 * Created by muham on 2018-02-11.
 */

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
    public void setupAuthenticationListener() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null){
            //replace that with the method
            viewProjectsScreenView.returnToLoginScreen();
        }
    }

    @Override
    public FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> setupGeneralProjectsAdapter(final Context appContext, RecyclerView projectList, ProgressDialog loadingProjectsDialog) {
        final ProgressDialog dialog = loadingProjectsDialog;
        //just to be safe if constructor wasn't called before
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        String userID = mAuth.getCurrentUser().getUid();
        mQuery = rootRef.child("projects").orderByChild(userID).equalTo("true");
        FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> projectListAdapter
                = new FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder>(
                Project.class,
                R.layout.project_row,
                ProjectListScreenFragment.ProjectsViewHolder.class,
                mQuery
        ) {
            protected void populateViewHolder(ProjectListScreenFragment.ProjectsViewHolder viewHolder, Project model, final int position) {
                viewHolder.setDetails(appContext, model.getProjectTitle(), model.getProjectOwnerEmail(), model.getProjectDesc());
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
    public FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> setupMyProjectsAdapter(final Context appContext, RecyclerView projectList, ProgressDialog loadingProjectsDialog) {
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
            protected void populateViewHolder(ProjectListScreenFragment.ProjectsViewHolder viewHolder, Project model, final int position) {
                viewHolder.setDetails(appContext, model.getProjectTitle(), model.getProjectOwnerEmail(), model.getProjectDesc());
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
    public void signOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            viewProjectsScreenView.returnToLoginScreen();
        }
    }
}

