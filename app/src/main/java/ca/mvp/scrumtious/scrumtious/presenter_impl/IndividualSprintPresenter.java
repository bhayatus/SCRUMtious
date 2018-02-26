package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualSprintPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualSprintViewInt;

public class IndividualSprintPresenter implements IndividualSprintPresenterInt {

    private IndividualSprintViewInt individualSprintView;
    private String pid, sid;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    public IndividualSprintPresenter(IndividualSprintViewInt individualSprintView, String pid, String sid){
        this.individualSprintView = individualSprintView;
        this.pid = pid;
        this.sid = sid;
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
                    individualSprintView.onProjectDeleted();
                }

                else{
                    // Check if I'm no longer a member through my uid
                    mAuth = FirebaseAuth.getInstance();
                    if(!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        individualSprintView.onProjectDeleted();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // In case the sprint no longer exists or user was removed, user must be returned to project list screen
    @Override
    public void setupSprintDeletedListener() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid).child("sprints");
        mRef.child(sid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If sprint no longer exists, exit this screen and go back
                if (!dataSnapshot.exists()){
                    individualSprintView.onSprintDeleted();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
