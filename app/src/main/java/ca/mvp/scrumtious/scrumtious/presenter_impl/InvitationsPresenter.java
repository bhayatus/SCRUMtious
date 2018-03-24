package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
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
import java.util.HashMap;
import java.util.Map;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.InvitationsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.InvitationsViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserInvite;
import ca.mvp.scrumtious.scrumtious.view_impl.InvitationsFragment;

public class InvitationsPresenter implements InvitationsPresenterInt {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;
    private FirebaseAuth firebaseAuth;

    private Query databaseQuery;

    private InvitationsViewInt invitationsView;

    public InvitationsPresenter(InvitationsViewInt invitationsView){
        this.invitationsView = invitationsView;

    }

    @Override
    public FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder> setupInvitationListAdapter() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseRootReference = firebaseDatabase.getInstance().getReference();
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Only display invites that were meant for the current user
        databaseQuery = firebaseRootReference.child("invites").orderByChild("invitedUid").equalTo(userId);

        FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder> invitationsAdapter
                = new FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder>(
                UserInvite.class,
                R.layout.user_invite_row,
                InvitationsFragment.InvitationsViewHolder.class,
                databaseQuery
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
                invitationsView.setEmptyStateView();
            }
        };
        return invitationsAdapter;

    }

    // User decides to accept an invite
    @Override
    public void acceptInvite(final String projectId, final String inviteId) {

        firebaseAuth = FirebaseAuth.getInstance();
        final String invitedUid = firebaseAuth.getCurrentUser().getUid();

        // Get the number of members

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        firebaseRootReference.child("projects").child(projectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numMembers = (long) dataSnapshot.child("numMembers").getValue();
                numMembers++; // User accepted invite, have to increase member count

                Map acceptInviteMap = new HashMap();

                // All of the following have to happen to ensure atomicity
                acceptInviteMap.put("/projects/" + projectId + "/" + invitedUid, "member");
                acceptInviteMap.put("/projects/" + projectId + "/" + "numMembers", numMembers);
                acceptInviteMap.put("/users/" + invitedUid + "/" + projectId, "member");
                acceptInviteMap.put("/invites/" + inviteId, null);

                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseRootReference = firebaseDatabase.getReference();

                firebaseRootReference.updateChildren(acceptInviteMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()){
                            invitationsView.showMessage("An error occurred, failed to accept the invite.", false);
                        }
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Remove the invite from the database if user chooses to reject it
    @Override
    public void removeInvite(String inviteId) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        firebaseRootReference.child("invites").child(inviteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    invitationsView.showMessage("An error occurred, failed to decline the invite.", false);
                }
            }
        });
    }
}
