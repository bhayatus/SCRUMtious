package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectMembersPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectMembersViewInt;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.model.User;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectListScreenFragment;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectMembersFragment;

public class ProjectMembersPresenter implements ProjectMembersPresenterInt {

    private final String pid;
    private ProjectMembersViewInt projectMembersView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Query mQuery;

    public ProjectMembersPresenter (ProjectMembersViewInt projectMembersView, String pid){
        this.projectMembersView = projectMembersView;
        this.pid = pid;
    }

    // In case the project no longer exists
    @Override
    public void setupProjectDeleteListener(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects");
        mRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If project no longer exists, exit this screen and go back
                if (!dataSnapshot.exists()){
                    projectMembersView.onSuccessfulDeletion();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public FirebaseRecyclerAdapter<User, ProjectMembersFragment.MembersViewHolder> setupMembersAdapter(RecyclerView memberList) {
            mDatabase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            DatabaseReference rootRef = mDatabase.getReference();
            mQuery = rootRef.child("users").orderByChild(pid).equalTo("member");

            FirebaseRecyclerAdapter<User, ProjectMembersFragment.MembersViewHolder> membersListAdapter
                    = new FirebaseRecyclerAdapter<User, ProjectMembersFragment.MembersViewHolder>(
                    User.class,
                    R.layout.member_row,
                    ProjectMembersFragment.MembersViewHolder.class,
                    mQuery
            ) {

                @Override
                protected void populateViewHolder(ProjectMembersFragment.MembersViewHolder viewHolder, User model, int position) {
                    viewHolder.setDetails(model.getEmailAddress());
                    final User userModel = model;
                    final int currentPosition = position;
                    final ImageButton delete = viewHolder.getDeleteView();

                    mRef  = FirebaseDatabase.getInstance().getReference().child("projects").child(pid).child("projectOwnerUid");
                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // Only owner can delete members
                            if ((dataSnapshot.getValue().toString().trim()).equals(mAuth.getCurrentUser().getUid()) == false){
                                delete.setVisibility(View.GONE);
                            }

                            if(userModel.getUserID().equals(dataSnapshot.getValue().toString().trim())){
                                delete.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // If user chooses to remove member, do so
                            String uid = getRef(currentPosition).getKey();
                            projectMembersView.onClickDelete(uid);
                        }
                    });

                }
                @Override
                public void onDataChanged() {

                }
            };
            return membersListAdapter;
    }

    // Delete the member from the project
    private void deleteMember(final String uid){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();


        mRef.child("users").child(uid).child(pid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mRef.child("projects").child(pid).child(uid).removeValue();
            }
        });
    }

    // Check the group owner's password before deleting member from project
    @Override
    public void validatePassword(String password, final String uid) {

        if (password == null) {
            projectMembersView.deleteMemberExceptionMessage("Password incorrect, could not delete member.");
        } else {

            if(password.length() == 0) {
                projectMembersView.deleteMemberExceptionMessage("Password incorrect, could not delete member.");
            }
            else{
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser mUser = mAuth.getCurrentUser();
            AuthCredential mCredential = EmailAuthProvider.getCredential(mUser.getEmail(), password);
            mUser.reauthenticate(mCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    // If password entered matched the password of the group owner, then delete
                    if (task.isSuccessful()) {
                        deleteMember(uid);
                    }

                    // Password didn't match, tell user
                    else {
                        projectMembersView.deleteMemberExceptionMessage("Password incorrect, could not delete member.");
                    }
                }
            });
        }
        }
    }


}
