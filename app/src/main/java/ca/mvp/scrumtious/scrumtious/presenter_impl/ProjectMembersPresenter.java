package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
                protected void populateViewHolder(ProjectMembersFragment.MembersViewHolder viewHolder, final User model, final int position) {
                    viewHolder.setDetails(model.getUserEmailAddress());

                    final ImageButton delete = viewHolder.getDeleteView();


                    mRef  = FirebaseDatabase.getInstance().getReference().child("projects").child(pid).child("projectOwnerUid");
                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Owner cannot delete self
                            if (dataSnapshot.getValue() == model.getUserId()){
                                delete.setVisibility(View.GONE);
                            }
                            // Only owner can delete members
                            if (dataSnapshot.getValue() != mAuth.getCurrentUser().getUid()){
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
                            String uid = getRef(position).getKey();
                            deleteMember(uid);
                        }
                    });

                }
                @Override
                public void onDataChanged() {

                }
            };
            return membersListAdapter;
    }

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


}
