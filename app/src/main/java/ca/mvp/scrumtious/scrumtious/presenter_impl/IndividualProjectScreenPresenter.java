package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

    @Override
    public void setupProjectDeleteListener(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects");
        mRef.child(pid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    individualProjectScreenView.onSuccessfulDeletion();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkIfOwner(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        mRef.child("projects").child(pid).child("projectOwnerUid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue().toString().equals(mUser.getUid().toString())){
                    individualProjectScreenView.setDeleteInvisible();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void validatePassword(String password){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        AuthCredential mCredential = EmailAuthProvider.getCredential(mUser.getEmail(), password);
        mUser.reauthenticate(mCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    deleteProject();
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
                    // Project was successfully deleted
                    individualProjectScreenView.onSuccessfulDeletion();
                }
            }
        });
    }


}
