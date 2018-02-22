package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.PBInProgressPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.PBInProgressViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserStory;
import ca.mvp.scrumtious.scrumtious.view_impl.PBInProgressFragment;

public class PBInProgressPresenter implements PBInProgressPresenterInt {

    private PBInProgressViewInt pbInProgressView;
    private String pid;
    private FirebaseDatabase mDatabase;
    private DatabaseReference rootRef;
    private Query mQuery;

    public PBInProgressPresenter(PBInProgressViewInt pbInProgressView, String pid){
        this.pbInProgressView = pbInProgressView;
        this.pid = pid;
    }


    @Override
    public FirebaseRecyclerAdapter<UserStory, PBInProgressFragment.InProgressViewHolder> setupInProgressAdapter(RecyclerView inProgressList) {
        rootRef = FirebaseDatabase.getInstance().getReference();

        // The query below grabs all user stories that are not assigned to any sprints
        // and that are not complete, hence the "false"
        // If I wanted to check for completed, the second part would be "true"
        mQuery = rootRef.child("projects").child(pid).child("user_stories").orderByChild("assignedTo_completed")
                .equalTo("null_false");

        FirebaseRecyclerAdapter<UserStory, PBInProgressFragment.InProgressViewHolder> inProgressListAdapter
                = new FirebaseRecyclerAdapter<UserStory, PBInProgressFragment.InProgressViewHolder>(
                UserStory.class,
                R.layout.user_story_row,
                PBInProgressFragment.InProgressViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(PBInProgressFragment.InProgressViewHolder viewHolder, UserStory model, int position) {
                viewHolder.setDetails(model.getUserStoryName(), model.getUserStoryPoints());
                final PBInProgressFragment.InProgressViewHolder mViewHolder = viewHolder;
                ImageButton completed = viewHolder.getCompleted();
                ImageButton delete = viewHolder.getDelete();

                // The id of the user story
                final String usid = getRef(position).getKey().toString();

                // Right now we are looking at in progress user stories, so they should appear as unchecked
                // The opposite of this would be setting to ic_checkbox_checked
                completed.setImageResource(R.drawable.ic_checkbox_not_checked);
                // If user clicks on the checkbox button, notify user first
                completed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pbInProgressView.onClickChangeStatus(usid, true);
                    }
                });

                // Group owner wants to delete the user story, notify first
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pbInProgressView.onClickDeleteUserStory(usid);
                    }
                });

                // The following checks if the current user is the project owner
                mDatabase = FirebaseDatabase.getInstance();
                mDatabase.getReference().child("projects").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String projectOwnerUid = dataSnapshot.child("projectOwnerUid").getValue().toString();

                            // Current user is not group owner, don't allow them to view delete button
                            if(!projectOwnerUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                mViewHolder.setDeleteInvisible();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onDataChanged() {

            }


        };
        return inProgressListAdapter;
    }

    // Check the group owner's password before deleting user story
    @Override
    public void validatePassword(String password, final String usid) {

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential mCredential = EmailAuthProvider.getCredential(mUser.getEmail(), password);
        mUser.reauthenticate(mCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // If password entered matched the password of the group owner, then delete user story
                if (task.isSuccessful()) {
                    deleteUserStory(usid);
                }

                // Password didn't match, tell user
                else {
                    pbInProgressView.showMessage("Incorrect password, could not delete user story.");
                }
            }
        });


    }

    // Change the user story completed flag from either true to false, or false to true
    @Override
    public void changeCompletedStatus(String usid, boolean newStatus){
        final String completed = Boolean.toString(newStatus);
        final String userStoryId = usid;

        mDatabase = FirebaseDatabase.getInstance();
        rootRef = mDatabase.getReference();
        // Gets the current user story
        rootRef.child("projects").child(pid).child("user_stories").child(usid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the assigned to variable, whether it be the product backlog or a specific sprint backlog
                String assignedTo = dataSnapshot.child("assignedTo").getValue().toString();
                final String assignedTo_completed = assignedTo + "_" + completed;

                Map statusMap = new HashMap();

                // Both changes need to happen to ensure atomicity
                statusMap.put("/projects/" + pid + "/user_stories/" + userStoryId + "/" + "completed", completed);
                statusMap.put("/projects/" + pid + "/user_stories/" + userStoryId + "/" + "assignedTo_completed", assignedTo_completed);

                rootRef.updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){
                            pbInProgressView.showMessage("Marked the user story as completed.");
                        }
                        else{
                            pbInProgressView.showMessage("An error occurred, failed to mark the user story as completed.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Deletes the user story from the database
    @Override
    public void deleteUserStory(String usid) {
        mDatabase = FirebaseDatabase.getInstance();
        rootRef = mDatabase.getReference().child("projects").child(pid).child("user_stories");
        rootRef.child(usid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // User story was deleted successfully
                if (task.isSuccessful()){
                    pbInProgressView.showMessage("User story was deleted.");
                }
                else{
                    pbInProgressView.showMessage("An error occurred, failed to delete the user story");
                }
            }
        });
    }
}
