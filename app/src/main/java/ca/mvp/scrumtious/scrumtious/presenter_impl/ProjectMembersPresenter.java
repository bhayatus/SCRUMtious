package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
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
import java.util.HashMap;
import java.util.Map;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectMembersPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectMembersViewInt;
import ca.mvp.scrumtious.scrumtious.model.User;
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
            // Only query users who are in the project
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
                    final String uid = getRef(position).getKey();
                    ImageButton delete = viewHolder.getDeleteView();
                    ImageButton owner = viewHolder.getOwner();
                    final ProjectMembersFragment.MembersViewHolder mViewHolder = viewHolder;
                    final User userModel = model;

                    mRef  = FirebaseDatabase.getInstance().getReference().child("projects").child(pid).child("projectOwnerUid");
                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // Only owner can delete members
                            if ((dataSnapshot.getValue().toString().trim()).equals(mAuth.getCurrentUser().getUid()) == false){
                                mViewHolder.setDeleteInvisible();
                            }

                            // Current member in the view isn't the project owner, disable the owner icon
                            if (userModel.getUserID().equals(dataSnapshot.getValue().toString()) == false){
                                mViewHolder.setOwnerInvisible();
                            }

                            // Owner should not be able to remove them self from the project
                            if(userModel.getUserID().equals(dataSnapshot.getValue().toString().trim())){
                                mViewHolder.setDeleteInvisible();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // If user chooses to remove member, do so after confirming
                            projectMembersView.onClickDelete(uid);
                        }
                    });

                    // Shows tooltip for owner icon if user holds long enough
                    owner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TooltipCompat.setTooltipText(v, "This is the owner of the project");
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

        // Grab the number of members
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mRef.child("projects").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numMembers = (long) dataSnapshot.child("numMembers").getValue();
                numMembers--; // User is being removed, decrease member count

                mDatabase = FirebaseDatabase.getInstance();
                mRef = mDatabase.getReference();

                Map removeMemberMap = new HashMap();

                // Following changes need to occur to ensure atomicity
                removeMemberMap.put("/users/" + uid + "/" + pid, null);
                removeMemberMap.put("/projects/" + pid + "/" + uid, null);
                removeMemberMap.put("/projects/" + pid + "/" + "numMembers", numMembers);

                mRef.updateChildren(removeMemberMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            projectMembersView.showMessage("Deleted member from project.");
                        }
                        else{
                            projectMembersView.showMessage("An error occurred, failed to delete member from project.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Check the group owner's password before deleting member from project
    @Override
    public void validatePassword(String password, final String uid) {

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
                    projectMembersView.showMessage("Incorrect password, could not delete member.");
                }
            }
        });


    }

    // Need to verify if the owner if add member button is to show
    public void checkIfOwner(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        mRef.child("projects").child(pid).child("projectOwnerUid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Only owner should see invite member button, should be invisible otherwise
                if(!dataSnapshot.getValue().toString().equals(mUser.getUid())){
                    projectMembersView.setAddMemberInvisible();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void checkBeforeInvite(String emailAddress) {

        // First check if user even exists
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("users");
        mRef.orderByChild("emailAddress").equalTo(emailAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    projectMembersView.showMessage("Cannot invite user with that e-mail address " +
                            "as they do not exist.");
                    return;
                }

                // Proceed with checking if user is already in the project
                else{
                        String id = "";
                        for(DataSnapshot d: dataSnapshot.getChildren()){
                           id = d.child("userID").getValue(String.class);
                        }

                        final String invitedUid = id;

                    // Check if user is already in project
                    mDatabase = FirebaseDatabase.getInstance();
                    mRef = mDatabase.getReference().child("projects");
                    mRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean checkMore = true; // flag to ensure the rest of the checks happen
                            // if necessary

                            // User already in project, don't invite again
                            if (dataSnapshot.exists()){
                                    String id = dataSnapshot.getKey().toString();

                                    if(id.equals(pid) && dataSnapshot.hasChild(invitedUid)){
                                        projectMembersView.showMessage("Cannot invite member as they are already" +
                                                " part of this project.");
                                        checkMore = false;
                                        return;
                                    }
                            }

                            if (checkMore){

                                // Proceed with checking if user has already been invited

                                mDatabase = FirebaseDatabase.getInstance();
                                mRef = mDatabase.getReference().child("invites");
                                mRef.orderByChild("projectId").equalTo(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            for (DataSnapshot d: dataSnapshot.getChildren()){
                                                // If invitedUid matches, meaning the user has already been invited
                                                if (d.child("invitedUid").getValue().toString().equals(invitedUid)){
                                                    projectMembersView.showMessage("This user has already been invited to the project.");
                                                    return;
                                                }
                                            }
                                            // This is a case where we can actually invite the user
                                            inviteMember(invitedUid);
                                        }

                                        else{
                                            // This is a case where we can actually invite the user
                                            inviteMember(invitedUid);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Actually send the invite to the user at this point
    private void inviteMember(final String invitedUid){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects");
        mRef.child(pid).child("projectTitle").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String projectTitle = dataSnapshot.getValue().toString();

                // Get uid and password of me, the person inviting
                String invitingUid;
                String invitingEmail;
                mAuth = FirebaseAuth.getInstance();
                invitingUid = mAuth.getCurrentUser().getUid();
                invitingEmail = mAuth.getCurrentUser().getEmail();

                // Generating a unique push id
                mDatabase = FirebaseDatabase.getInstance();
                mRef = mDatabase.getReference();
                final String inviteId = mRef.push().getKey();

                Map inviteMap = new HashMap();

                // All of the following have to occur, to ensure atomicity
                inviteMap.put("/invites/" + inviteId + "/" + "projectId", pid);
                inviteMap.put("/invites/" + inviteId + "/" + "projectTitle", projectTitle);
                inviteMap.put("/invites/" + inviteId + "/" + "invitingUid", invitingUid);
                inviteMap.put("/invites/" + inviteId + "/" + "invitingEmail", invitingEmail);
                inviteMap.put("/invites/" + inviteId + "/" + "invitedUid", invitedUid);

                mRef.updateChildren(inviteMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            projectMembersView.showMessage("Sent an invite.");
                        }
                        else{
                            projectMembersView.showMessage("An error occurred, failed to send an invite.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
