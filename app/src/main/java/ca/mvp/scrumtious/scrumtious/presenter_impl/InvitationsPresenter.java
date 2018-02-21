package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.InvitationsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.InvitationsViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserInvite;
import ca.mvp.scrumtious.scrumtious.view_impl.InvitationsFragment;

public class InvitationsPresenter implements InvitationsPresenterInt {

    private InvitationsViewInt invitationsView;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private Query mQuery;
    public InvitationsPresenter(InvitationsViewInt invitationsView){
        this.invitationsView = invitationsView;

    }

    @Override
    public FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder> setupInvitationsAdapter(RecyclerView invitationsList) {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        rootRef = mDatabase.getInstance().getReference();
        String userID = mAuth.getCurrentUser().getUid();
        mQuery = rootRef.child("invites").orderByChild("invitedUid").equalTo(userID);

        FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder> invitationsAdapter
                = new FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder>(
                UserInvite.class,
                R.layout.user_invite_row,
                InvitationsFragment.InvitationsViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(InvitationsFragment.InvitationsViewHolder viewHolder, UserInvite model, int position) {
                viewHolder.setDetails(model.getProjectTitle(), model.getInvitingEmail());

                ImageButton accept = viewHolder.getAcceptButton();
                ImageButton decline = viewHolder.getDeclineButton();
                final UserInvite userModel = model;

                final String inviteId = getRef(position).getKey();


                // When user chooses to accept an invite
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        invitationsView.onClickAccept(userModel.getProjectId(), inviteId);
                    }
                });

                // When user chooses to decline invite
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        invitationsView.onClickDecline(inviteId);
                    }
                });

            }
            @Override
            public void onDataChanged() {

            }
        };
        return invitationsAdapter;

    }

    // User decides to accept an invite
    @Override
    public void acceptInvite(final String projectId, final String inviteId) {

        mAuth = FirebaseAuth.getInstance();
        final String invitedUid = mAuth.getCurrentUser().getUid();

        // First add user to project

        mDatabase = FirebaseDatabase.getInstance();
        rootRef = mDatabase.getReference();
        rootRef.child("projects").child(projectId).child(invitedUid).setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Do the opposite now

                    mDatabase = FirebaseDatabase.getInstance();
                    rootRef = mDatabase.getReference();
                    rootRef.child("users").child(invitedUid).child(projectId).setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Now remove the invite from the database
                                removeInvite(inviteId);
                            }

                        }
                    });
                }
            }
        });


    }

    // Remove the invite from the database
    @Override
    public void removeInvite(String inviteId) {
        mDatabase = FirebaseDatabase.getInstance();
        rootRef = mDatabase.getReference();
        rootRef.child("invites").child(inviteId).removeValue();
    }
}
