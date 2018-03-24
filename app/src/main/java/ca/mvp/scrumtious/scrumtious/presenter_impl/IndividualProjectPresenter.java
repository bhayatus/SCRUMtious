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
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualProjectViewInt;

public class IndividualProjectPresenter implements IndividualProjectPresenterInt {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;
    private FirebaseAuth firebaseAuth;

    private IndividualProjectViewInt individualProjectView;

    private final String PROJECT_ID;

    private Map removeProjectMap;

    public IndividualProjectPresenter(IndividualProjectViewInt individualProjectView, String pid){
        this.individualProjectView = individualProjectView;
        this.PROJECT_ID = pid;
    }

    // Need to verify if the owner, if delete project button is to show
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

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential userCredentials = EmailAuthProvider.getCredential(user.getEmail(), password);
        user.reauthenticate(userCredentials).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // If password entered matched the password of the group owner, then proceed to deletion
                if (task.isSuccessful()) {
                    deleteProject();
                }

                // Password didn't match, tell user
                else {
                    individualProjectView.showMessage("Incorrect password, could not delete the project.", false);
                }
            }
        });

    }

    private void deleteProject() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();

        removeProjectMap = new HashMap();

        // Remove the project itself
        removeProjectMap.put("/projects/" + PROJECT_ID, null);

        // Remove all users associated with this project
        firebaseRootReference.child("users").orderByChild(PROJECT_ID).equalTo("member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get all the users that were in the project, and remove them
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    removeProjectMap.put("/users/" + d.getKey() + "/" + PROJECT_ID, null);
                }

                // Remove all invites associated with this project
                firebaseRootReference.child("invites").orderByChild("projectId").equalTo(PROJECT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get all the invites that were in the project, and remove them
                        for(DataSnapshot d: dataSnapshot.getChildren()){
                            removeProjectMap.put("/invites/" + d.getKey(), null);
                        }

                        // Remove all parts at the same time
                        firebaseRootReference.updateChildren(removeProjectMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                // Successfully deleted project
                                if (task.isSuccessful()){
                                    individualProjectView.onSuccessfulDeletion();
                                }
                                // Failed, tell user
                                else{
                                    individualProjectView.showMessage("An error occurred, failed to delete the project.", false);
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
