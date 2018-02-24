package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectListPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectListViewInt;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectListFragment;

public class ProjectListPresenter implements ProjectListPresenterInt {
    private ProjectListViewInt projectListView;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private Query mQuery;
    public ProjectListPresenter(ProjectListViewInt projectListView){
        this.projectListView = projectListView;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> setupGeneralProjectsAdapter(final RecyclerView projectList, final ProgressDialog progressDialog) {
        //just to be safe if constructor wasn't called before
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        String userID = mAuth.getCurrentUser().getUid();
        mQuery = rootRef.child("projects").orderByChild(userID).equalTo("member");

        FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> projectListAdapter
                = new FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder>(
                Project.class,
                R.layout.project_row,
                ProjectListFragment.ProjectsViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(ProjectListFragment.ProjectsViewHolder viewHolder, Project model, int position) {
                // Grab the date
                long timestamp = model.getCreationTimeStamp();
                String dateFormatted = "Date Created: ";
                dateFormatted += DateFormat.format("MM/dd/yyyy", timestamp).toString();

                viewHolder.setDetails(model.getProjectTitle(), model.getProjectOwnerEmail(), model.getProjectDesc(), dateFormatted);
                final String pid = getRef(position).getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        projectListView.goToProjectScreen(pid);
                    }
                });
            }
            @Override
            public void onDataChanged() {

                projectListView.setView();

                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        };
        return projectListAdapter;
    }

    @Override
    public FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> setupMyProjectsAdapter(RecyclerView projectList, final ProgressDialog progressDialog) {
        //just to be safe if constructor wasn't called before
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        String userID = mAuth.getCurrentUser().getUid();
        mQuery = rootRef.child("projects").orderByChild("projectOwnerUid").equalTo(userID);

        FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> projectListAdapter
                = new FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder>(
                Project.class,
                R.layout.project_row,
                ProjectListFragment.ProjectsViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(ProjectListFragment.ProjectsViewHolder viewHolder, Project model, int position) {
                // Grab the date
                long timestamp = model.getCreationTimeStamp();
                String dateFormatted = "Date Created: ";
                dateFormatted += DateFormat.format("MM/dd/yyyy", timestamp).toString();

                viewHolder.setDetails(model.getProjectTitle(), model.getProjectOwnerEmail(), model.getProjectDesc(), dateFormatted);
                final String pid = getRef(position).getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        projectListView.goToProjectScreen(pid);
                    }
                });
            }
            @Override
            public void onDataChanged() {

                projectListView.setView();

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };
        return projectListAdapter;
    }

}

