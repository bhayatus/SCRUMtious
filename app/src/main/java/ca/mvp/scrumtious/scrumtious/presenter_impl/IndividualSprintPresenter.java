package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualSprintPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualSprintViewInt;

public class IndividualSprintPresenter implements IndividualSprintPresenterInt {

    private IndividualSprintViewInt individualSprintView;
    private String pid, sid;

    private Map deleteSprintMap;

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

    // Need to verify if the owner if delete sprint button is to show
    public void checkIfOwner(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        mRef.child("projects").child(pid).child("projectOwnerUid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Only owner should see delete button, should be invisible otherwise
                if(!dataSnapshot.getValue().toString().equals(mUser.getUid())){
                    individualSprintView.setDeleteInvisible();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                individualSprintView.showMessage(databaseError.getMessage());
            }
        });

    }

    // Checks to see if group owner entered password correctly before going through with delete
    @Override
    public void validatePassword(String password){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        AuthCredential mCredential = EmailAuthProvider.getCredential(mUser.getEmail(), password);
        mUser.reauthenticate(mCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // If password entered matched the password of the group owner, then proceed to deletion
                if (task.isSuccessful()) {
                    deleteSprint();
                }

                // Password didn't match, tell user
                else {
                    individualSprintView.showMessage("Incorrect password, could not delete sprint.");
                }
            }
        });

    }

    // Actually delete the sprint at this point
    private void deleteSprint(){

        deleteSprintMap = new HashMap();

        deleteSprintMap.put("/projects/" + pid + "/" + "sprints" + "/" + sid, null);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mRef.child("projects").child(pid).child("user_stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    if (d.child("assignedTo").equals(sid)){
                        String usid = d.getKey().toString();
                        String completed = d.child("completed").getValue().toString();

                        String assignedTo_completed = "null" + "_" + completed;

                        deleteSprintMap.put("/projects/" + pid + "/" + "user_stories" + "/" + usid + "/" + "assignedTo", "null");
                        deleteSprintMap.put("/projects/" + pid + "/" + "user_stories" + "/" + usid + "/" + "assignedToName", "..............................");
                        deleteSprintMap.put("/projects/" + pid + "/" + "user_stories" + "/" + usid + "/" + "assignedTo_completed", assignedTo_completed);
                    }
                }

                mRef.updateChildren(deleteSprintMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Successfully deleted sprint
                        if (task.isSuccessful()){
                            individualSprintView.onSprintDeleted();
                        }
                        else{
                            individualSprintView.showMessage("An error occurred, failed to delete the sprint.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                individualSprintView.showMessage(databaseError.getMessage());
            }
        });



    }
}
