package ca.mvp.scrumtious.scrumtious.presenter_impl;

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

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;
    private FirebaseAuth firebaseAuth;

    private IndividualSprintViewInt individualSprintView;

    private final String PROJECT_ID;
    private final String SPRINT_ID;

    private Map deleteSprintMap;

    public IndividualSprintPresenter(IndividualSprintViewInt individualSprintView, String pid, String sid){
        this.individualSprintView = individualSprintView;
        this.PROJECT_ID = pid;
        this.SPRINT_ID = sid;
    }

    // Need to verify if the owner if delete sprint button is to show
    @Override
    public void checkIfOwner(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        firebaseRootReference.child("projects").child(PROJECT_ID).child("projectOwnerUid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Only owner should see delete button, should be invisible otherwise
                if(!dataSnapshot.getValue().toString().equals(user.getUid())){
                    individualSprintView.setDeleteInvisible();
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

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential userCredentials = EmailAuthProvider.getCredential(user.getEmail(), password);
        user.reauthenticate(userCredentials).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // If password entered matched the password of the group owner, then proceed to deletion
                if (task.isSuccessful()) {
                    deleteSprint();
                }

                // Password didn't match, tell user
                else {
                    individualSprintView.showMessage("Incorrect password, could not delete the sprint.", false);
                }
            }
        });

    }

    // Actually delete the sprint at this point
    private void deleteSprint(){

        deleteSprintMap = new HashMap();

        deleteSprintMap.put("/projects/" + PROJECT_ID + "/" + "sprints" + "/" + SPRINT_ID, null);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        firebaseRootReference.child("projects").child(PROJECT_ID).child("user_stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    if (d.child("assignedTo").getValue().toString().equals(SPRINT_ID)){
                        String usid = d.getKey().toString();
                        String completed = d.child("completed").getValue().toString();

                        String assignedTo_completed = "null" + "_" + completed;

                        deleteSprintMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + usid + "/" + "assignedTo", "null");
                        deleteSprintMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + usid + "/" + "assignedToName", "");
                        deleteSprintMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + usid + "/" + "assignedTo_completed", assignedTo_completed);
                    }
                }

                // Grab the old number of sprints

                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseRootReference = firebaseDatabase.getReference();
                firebaseRootReference.child("projects").child(PROJECT_ID).child("numSprints").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long numSprints = (long) dataSnapshot.getValue();

                        numSprints--;

                        deleteSprintMap.put("/projects/" + PROJECT_ID + "/" + "numSprints", numSprints);

                        // Update database at this point
                        firebaseRootReference.updateChildren(deleteSprintMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                // Successfully deleted sprint
                                if (task.isSuccessful()){
                                    individualSprintView.onSprintDeleted();
                                }
                                else{
                                    individualSprintView.showMessage("An error occurred, failed to delete the sprint.", false);
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
