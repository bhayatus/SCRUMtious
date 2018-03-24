package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.app.ProgressDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;

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

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseRootReference;
    private Query databaseQuery;

    private ProjectListViewInt projectListView;

    public ProjectListPresenter(ProjectListViewInt projectListView){
        this.projectListView = projectListView;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> setupProjectListAdapter(final ProgressDialog progressDialog, boolean ownedOnly) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseRootReference = FirebaseDatabase.getInstance().getReference();
        String userID = firebaseAuth.getCurrentUser().getUid();

        // User only wants to view the projects that they own
        if (ownedOnly) {
            databaseQuery = firebaseRootReference.child("projects").orderByChild("projectOwnerUid").equalTo(userID);
        }
        // User wants to see all projects that they are part of
        else{
            databaseQuery = firebaseRootReference.child("projects").orderByChild(userID).equalTo("member");
        }

        FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> projectListAdapter
                = new FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder>(
                Project.class,
                R.layout.project_row,
                ProjectListFragment.ProjectsViewHolder.class,
                databaseQuery
        ) {

            @Override
            protected void populateViewHolder(ProjectListFragment.ProjectsViewHolder viewHolder, Project model, int position) {
                final String pid = getRef(position).getKey();

                final ProjectListFragment.ProjectsViewHolder mViewHolder = viewHolder;
                final Project projectModel = model;

                // Grab the date
                long timestamp = model.getCreationTimeStamp();
                final String dateFormatted = "Date Created: " + DateFormat.format("MM/dd/yyyy", timestamp).toString();

                // Grab the number of members
                long numMembers = model.getNumMembers();
                String numMembersString = Long.toString(numMembers);

                // Grab the number of sprints
                long numSprints = model.getNumSprints();
                String numSprintsString = Long.toString(numSprints);

                viewHolder.setDetails(model.getProjectTitle(), model.getProjectOwnerEmail(), model.getProjectDesc(), dateFormatted, numMembersString, numSprintsString);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        projectListView.goToProjectScreen(pid);
                    }
                });

                ImageButton moreBtn = viewHolder.getProjectRowExpandDescIconImageButton();

                // When user clicks the button, toggle the description showing boolean and reset description
                moreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewHolder.switchShowFull(projectModel.getProjectDesc());

                    }
                });
            }
            @Override
            public void onDataChanged() {

                projectListView.setEmptyStateView();

                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        };
        return projectListAdapter;
    }

}

