package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SprintListPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SprintListViewInt;
import ca.mvp.scrumtious.scrumtious.model.Sprint;
import ca.mvp.scrumtious.scrumtious.view_impl.SprintListActivity;

public class SprintListPresenter implements SprintListPresenterInt {

    private SprintListViewInt sprintListView;
    private String pid;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private Query mQuery;

    public SprintListPresenter (SprintListViewInt sprintListView, String pid){
        this.sprintListView = sprintListView;
        this.pid = pid;
    }

    // In case the project no longer exists or user was removed, user must be returned to project list screen
    @Override
    public void setupProjectDeletedListener(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects");
        mRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // If project no longer exists, exit this screen and go back
                if (!dataSnapshot.exists()){
                    sprintListView.onProjectDeleted();
                }

                else{
                    // Check if I'm no longer a member through my uid
                    mAuth = FirebaseAuth.getInstance();
                    if(!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        sprintListView.onProjectDeleted();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> setupSprintListAdapter(final RecyclerView sprintList) {
        mRef = FirebaseDatabase.getInstance().getReference();
        mQuery = mRef.child("projects").child(pid).child("sprints").orderByChild("sprintStartDate");

        FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> sprintListAdapter
                = new FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder>(
                Sprint.class,
                R.layout.sprint_row,
                SprintListActivity.SprintsViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(SprintListActivity.SprintsViewHolder viewHolder, Sprint model, int position) {
                final String sid = getRef(position).getKey();

                // Grab the dates
                long startDate = model.getSprintStartDate();
                long endDate = model.getSprintEndDate();
                final String dateFormatted = DateFormat.format("MM/dd/yyyy", startDate).toString()
                        + " to " +  DateFormat.format("MM/dd/yyyy", endDate).toString();

                viewHolder.setDetails(model.getSprintName(), model.getSprintDesc(), dateFormatted);


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sprintListView.goToSprintScreen(sid);
                    }
                });
            }
            @Override
            public void onDataChanged() {
                sprintListView.setView();
            }
        };
        return sprintListAdapter;
    }


    }
