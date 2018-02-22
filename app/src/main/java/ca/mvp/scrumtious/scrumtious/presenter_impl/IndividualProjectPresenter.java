package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.util.Log;

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
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualProjectViewInt;

public class IndividualProjectPresenter implements IndividualProjectPresenterInt {
    private boolean deletedNormally = false;
    private IndividualProjectViewInt individualProjectView;
    private final String pid;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    private Map removeProjectMap;

    public IndividualProjectPresenter(IndividualProjectViewInt individualProjectView, String pid){
        this.individualProjectView = individualProjectView;
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
                        individualProjectView.onSuccessfulDeletion();

                }

                else{
                    // Check if I'm no longer a member through my uid
                    mAuth = FirebaseAuth.getInstance();
                    if(!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        individualProjectView.onSuccessfulDeletion();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Need to verify if the owner if delete project button is to show
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
                    individualProjectView.setDeleteInvisible();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    deleteProject();
                }

                // Password didn't match, tell user
                else {
                    individualProjectView.showMessage("Incorrect password, could not delete project.");
                }
            }
        });

    }

    private void deleteProject() {
        mDatabase = FirebaseDatabase.getInstance();
        //mRef = mDatabase.getReference().child("projects");
        mRef = mDatabase.getReference();

        removeProjectMap = new HashMap();

        // Remove the project itself
        removeProjectMap.put("/projects/" + pid, null);

        // Remove all users associated with this project
        mRef.child("users").orderByChild(pid).equalTo("member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get all the users that were in the project, and remove them
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    removeProjectMap.put("/users/" + d.getKey() + "/" + pid, null);
                }

                // Remove all invites associated with this project
                mRef.child("invites").orderByChild("projectId").equalTo(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get all the invites that were in the project, and remove them
                        for(DataSnapshot d: dataSnapshot.getChildren()){
                            removeProjectMap.put("/invites/" + d.getKey(), null);
                        }

                        // Remove all parts at the same time
                        mRef.updateChildren(removeProjectMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                // Successfully deleted project
                                if (task.isSuccessful()){
                                    individualProjectView.onSuccessfulDeletion();
                                }
                                // Failed, tell user
                                else{
                                    individualProjectView.showMessage("An error occurred, failed to delete project.");
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
