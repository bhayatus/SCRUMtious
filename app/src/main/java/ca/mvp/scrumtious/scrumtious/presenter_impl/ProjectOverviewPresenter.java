package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.text.format.DateFormat;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.sql.Timestamp;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectOverviewViewInt;

public class ProjectOverviewPresenter implements ProjectOverviewPresenterInt{

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    ValueEventListener projectDetailsListener, currentSprintListener;

    private ProjectOverviewViewInt projectOverviewView;
    private String pid;
    private String currentSprintId = "";

    private long totalStories = 0;
    private long completedStories = 0;

    private String title = " ";
    private String desc = " ";

    public ProjectOverviewPresenter(ProjectOverviewViewInt projectOverviewView, String pid){
        this.projectOverviewView = projectOverviewView;
        this.pid = pid;
    }

    // Grabs the info for the project
    @Override
    public void setupProjectDetailsListener() {

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid);

        projectDetailsListener = mRef.addValueEventListener(new ValueEventListener() {

            // Grab the new title and description, and display it
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    title = dataSnapshot.child("projectTitle").getValue().toString();
                    desc = dataSnapshot.child("projectDesc").getValue().toString();
                    projectOverviewView.setProjectDetails(title, desc);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Listener should be removed if user is no longer viewing the fragment
    @Override
    public void removeProjectDetailsListener() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid);
        if (projectDetailsListener != null) {
            mRef.removeEventListener(projectDetailsListener);
        }
    }

    @Override
    public void setupCurrentSprintListener() {

        currentSprintId = "";

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        // Grab current sprint

        mRef.child("projects").child(pid).child("sprints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    Timestamp startTimeStamp = new Timestamp((long) d.child("sprintStartDate").getValue());
                    Timestamp endTimeStamp = new Timestamp((long) d.child("sprintEndDate").getValue());
                    Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());

                    // Within this sprint
                    if (currentTimeStamp == startTimeStamp || currentTimeStamp == endTimeStamp ||
                            (currentTimeStamp.after(startTimeStamp) && currentTimeStamp.before(endTimeStamp))){
                            currentSprintId = d.getKey().toString();
                            break;
                    }

                }

                // Only proceed if there are no current sprints
                if (!currentSprintId.equals("")) {


                    currentSprintListener = FirebaseDatabase.getInstance().getReference().child("projects").child(pid)
                            .child("sprints").child(currentSprintId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        long start = (long) dataSnapshot.child("sprintStartDate").getValue();
                                        long end = (long) dataSnapshot.child("sprintEndDate").getValue();
                                        String dateFormatted = DateFormat.format("MM/dd/yyyy", start).toString() +
                                                " to " + DateFormat.format("MM/dd/yyyy", end).toString();
                                        // Set the new details
                                        projectOverviewView.setCurrentSprintDetails(
                                                currentSprintId,
                                                dataSnapshot.child("sprintName").getValue().toString(),
                                                dataSnapshot.child("sprintDesc").getValue().toString(),
                                                dateFormatted
                                        );
                                    }
                                    else{
                                        projectOverviewView.setCurrentSprintDetails("", "", "", "");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void removeCurrentSprintListener() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        if (currentSprintListener != null) {
            mRef.child("projects").child(pid).child("sprints").child(currentSprintId).removeEventListener(currentSprintListener);
        }
    }

    @Override
    public void getUserStoryProgress() {

        totalStories = 0;
        completedStories = 0;

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mRef.child("projects").child(pid).child("user_stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // Go through user stories
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        totalStories++;
                        if (d.child("completed").getValue().toString().equals("true")){
                            completedStories++;
                        }
                    }

                    projectOverviewView.setCurrentProgressCircle(totalStories, completedStories);
                }
                // Failed to read progress
                else{
                    projectOverviewView.setCurrentProgressCircle(0, 0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
