package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.InvitationsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.InvitationsViewInt;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.model.UserInvite;
import ca.mvp.scrumtious.scrumtious.view_impl.InvitationsFragment;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectListScreenFragment;

public class InvitationsPresenter implements InvitationsPresenterInt {

    private InvitationsViewInt invitationsView;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private Query mQuery;
    public InvitationsPresenter(InvitationsViewInt invitationsView){
        this.invitationsView = invitationsView;

    }

    @Override
    public FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder> setupInvitationsAdapter(RecyclerView invitationsList) {

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
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
            protected void populateViewHolder(InvitationsFragment.InvitationsViewHolder viewHolder, UserInvite model, final int position) {
                viewHolder.setDetails(model.getProjectTitle(), model.getInvitingEmail());

                ImageButton accept = viewHolder.getAcceptButton();
                ImageButton decline = viewHolder.getDeclineButton();

                // When user chooses to accept an invite
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // When user chooses to decline invite
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
            @Override
            public void onDataChanged() {

            }
        };
        return invitationsAdapter;

    }
}
