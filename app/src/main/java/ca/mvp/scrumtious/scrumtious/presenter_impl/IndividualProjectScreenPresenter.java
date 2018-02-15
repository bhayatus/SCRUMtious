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

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectScreenPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualProjectScreenViewInt;

public class IndividualProjectScreenPresenter implements IndividualProjectScreenPresenterInt{

    private IndividualProjectScreenViewInt individualProjectScreenView;
    private final String pid;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    public IndividualProjectScreenPresenter(IndividualProjectScreenViewInt individualProjectScreenView, String pid){
        this.individualProjectScreenView = individualProjectScreenView;
        this.pid = pid;
    }

    // In case the project no longer exists, user must be returned to project list screen
    @Override
    public void setupProjectDeleteListener(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects");
        mRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If project no longer exists, exit this screen and go back
                if (!dataSnapshot.exists()){
                    individualProjectScreenView.onSuccessfulDeletion();
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
                    individualProjectScreenView.setDeleteInvisible();
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

                // If password entered matched the password of the group owner, then delete
                if (task.isSuccessful()) {
                    deleteProject();
                }

                // Password didn't match, tell user
                else {
                    individualProjectScreenView.deleteProjectExceptionMessage("Incorrect password, could not delete project.");
                }
            }
        });

    }

    private void deleteProject() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects");
        mRef.child(pid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    mRef = mDatabase.getReference().child("users");
                    mRef.orderByChild(pid).equalTo("member").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot d: dataSnapshot.getChildren()){
                               DatabaseReference ref = d.child(pid).getRef();
                               ref.removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // Project was successfully deleted
                    individualProjectScreenView.onSuccessfulDeletion();
                }
            }
        });
    }


}
