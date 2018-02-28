package ca.mvp.scrumtious.scrumtious.presenter_impl;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectOverviewViewInt;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class ProjectOverviewPresenter implements ProjectOverviewPresenterInt{


    private ProjectOverviewViewInt projectOverviewView;
    private String pid;

    private String title = "";
    private String desc = "";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    public ProjectOverviewPresenter(ProjectOverviewViewInt projectOverviewView, String pid){
        this.projectOverviewView = projectOverviewView;
        this.pid = pid;
    }


    // Grabs the info from the database
    @Override
    public void getDetails() {

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid);

        mRef.addValueEventListener(new ValueEventListener() {

            // Grab the new title and description, and display it
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    title = dataSnapshot.child("projectTitle").getValue().toString();
                    desc = dataSnapshot.child("projectDesc").getValue().toString();
                }

                projectOverviewView.setDetails(title, desc);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
