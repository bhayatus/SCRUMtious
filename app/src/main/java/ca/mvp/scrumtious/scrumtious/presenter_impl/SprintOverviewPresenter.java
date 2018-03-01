package ca.mvp.scrumtious.scrumtious.presenter_impl;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SprintOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectOverviewViewInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SprintOverviewViewInt;

public class SprintOverviewPresenter implements SprintOverviewPresenterInt {


    private SprintOverviewViewInt sprintOverviewView;
    private String pid, sid;

    private String title = "";
    private String desc = "";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    public SprintOverviewPresenter(SprintOverviewViewInt sprintOverviewView, String pid, String sid){
        this.sprintOverviewView = sprintOverviewView;
        this.pid = pid;
        this.sid = sid;
    }


    // Grabs the info from the database
    @Override
    public ValueEventListener getSprintDetailsListener() {

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid).child("sprints").child(sid);

        ValueEventListener sprintDetailsListener = mRef.addValueEventListener(new ValueEventListener() {

            // Grab the new title and description, and display it
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    title = dataSnapshot.child("sprintName").getValue().toString();
                    desc = dataSnapshot.child("sprintDesc").getValue().toString();
                    sprintOverviewView.setSprintDetails(title, desc);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return sprintDetailsListener;

    }

    @Override
    public void removeSprintDetailsListener(ValueEventListener sprintDetailsListener) {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid).child("sprints").child(sid);
        mRef.removeEventListener(sprintDetailsListener);
    }
}
