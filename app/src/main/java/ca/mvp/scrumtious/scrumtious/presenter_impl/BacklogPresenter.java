package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
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
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.BacklogPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.BacklogViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserStory;
import ca.mvp.scrumtious.scrumtious.view_impl.BacklogFragment;

public class BacklogPresenter implements BacklogPresenterInt {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Query mQuery;

    private BacklogViewInt backlogView;
    private String pid;

    private final String type; // Tells the presenter what type of info to grab
    private final String sprintId; // "null" if in product backlog, regular id if part of sprint

    // Different types of backlogs
    private final String pb_in_progress = "PB_IN_PROGRESS";
    private final String pb_completed = "PB_COMPLETED";
    private final String sprint_in_progress = "SPRINT_IN_PROGRESS";
    private final String sprint_completed = "SPRINT_COMPLETED";

    public BacklogPresenter(BacklogViewInt backlogView, String pid, String type, String sprintId){
        this.backlogView = backlogView;
        this.pid = pid;
        this.type = type;
        this.sprintId = sprintId;
    }


    @Override
    public FirebaseRecyclerAdapter<UserStory, BacklogFragment.BacklogViewHolder> setupBacklogAdapter() {
        mRef = FirebaseDatabase.getInstance().getReference();

        String typeQuery = "";

        // Sets the equalTo string that the query needs to use to search for user stories
        switch(type){
            case pb_in_progress:
                typeQuery = "false";
                break;
            case pb_completed:
                typeQuery = "true";
                break;
            case sprint_in_progress:
                typeQuery = this.sprintId + "_" + "false";
                break;
            case sprint_completed:
                typeQuery = this.sprintId + "_" + "true";
                break;
        }

        // Dealing with sprint backlog
        if (type.equals(sprint_completed) || type.equals(sprint_in_progress)) {
            mQuery = mRef.child("projects").child(pid).child("user_stories").orderByChild("assignedTo_completed")
                    .equalTo(typeQuery);
        }

        // Product backlog situation
        if (type.equals(pb_completed) || type.equals(pb_in_progress)){
            mQuery = mRef.child("projects").child(pid).child("user_stories").orderByChild("completed")
                    .equalTo(typeQuery);
        }

        FirebaseRecyclerAdapter<UserStory, BacklogFragment.BacklogViewHolder> backlogListAdapter
                = new FirebaseRecyclerAdapter<UserStory, BacklogFragment.BacklogViewHolder>(
                UserStory.class,
                R.layout.user_story_row,
                BacklogFragment.BacklogViewHolder.class,
                mQuery
        ) {


            @Override
            protected void populateViewHolder(final BacklogFragment.BacklogViewHolder viewHolder, UserStory model, int position) {

                String nameOfSprint = model.getAssignedToName();
                String assignedToName;

                // User story is in progress
                if (model.getCompleted().equals("false")){
                    viewHolder.setCardRed();
                }
                // User story is completed
                else{
                    viewHolder.setCardGreen();
                }

                // Not assigned to a sprint, don't bother showing assigned to icon
                if (nameOfSprint.equals("")){
                    assignedToName = "Not yet assigned to a sprint";
                    viewHolder.setSprintIconInvisible();
                }
                // User story in a sprint, show the sprint it is assigned to
                else {
                    assignedToName = "Assigned to: " + nameOfSprint;
                    viewHolder.setSprintIconVisible();
                }

                // If in a sprint, don't show the assigned to layout
                if (type.equals(sprint_completed) || type.equals(sprint_in_progress)){
                    viewHolder.setAssignedToLayoutInvisible();
                }

                viewHolder.setDetails(model.getUserStoryName(), model.getUserStoryPoints(), assignedToName);

                final BacklogFragment.BacklogViewHolder mViewHolder = viewHolder;
                ImageButton completed = viewHolder.getCompleted();
                ImageButton delete = viewHolder.getDelete();

                // The id of the user story
                final String usid = getRef(position).getKey().toString();

                // Show as in progress with image
                if (type.equals("PB_IN_PROGRESS") || type.equals("SPRINT_IN_PROGRESS")) {
                    completed.setImageResource(R.drawable.ic_checkbox_not_checked);
                }
                // Show as completed with icon
                else{
                    completed.setImageResource(R.drawable.ic_checkbox_checked);
                }

                // If user clicks on the checkbox button, notify user first
                completed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        switch(type){
                            case pb_in_progress:
                                // on the pb in progress view, ask to switch status to completed
                                backlogView.onClickChangeStatus(usid, true);
                                break;
                            case pb_completed:
                                // on the pb completed view, ask to switch status to in progress
                                backlogView.onClickChangeStatus(usid, false);
                                break;
                            case sprint_in_progress:
                                // on the sprint in progress view, ask to switch status to completed
                                backlogView.onClickChangeStatus(usid, true);
                                break;
                            case sprint_completed:
                                // on the sprint completed view, ask to switch status to in progress
                                backlogView.onClickChangeStatus(usid, false);
                                break;
                        }


                    }
                });

                // Group owner wants to delete the user story, notify first
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backlogView.onClickDeleteUserStory(usid);
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

                // If user holds down on user story long enough
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        backlogView.onLongClickUserStory(usid);
                        return false;
                    }
                });

            }
            @Override
            public void onDataChanged() {
                backlogView.setEmptyStateView();
            }


        };
        return backlogListAdapter;
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
                    backlogView.showMessage("Incorrect password, could not delete user story.", false);
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
        mRef = mDatabase.getReference();
        // Gets the current user story
        mRef.child("projects").child(pid).child("user_stories").child(usid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the assigned to variable, whether it be the product backlog or a specific sprint backlog
                String assignedTo = dataSnapshot.child("assignedTo").getValue().toString();
                final String assignedTo_completed = assignedTo + "_" + completed;

                Map statusMap = new HashMap();

                // Both changes need to happen to ensure atomicity
                statusMap.put("/projects/" + pid + "/user_stories/" + userStoryId + "/" + "completed", completed);
                statusMap.put("/projects/" + pid + "/user_stories/" + userStoryId + "/" + "assignedTo_completed", assignedTo_completed);

                mRef.updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){
                            switch(type){
                                case pb_in_progress:
                                    backlogView.showMessage("Marked the user story as completed.", false);
                                    break;
                                case pb_completed:
                                    backlogView.showMessage("Marked the user story as in progress.", false);
                                    break;
                                case sprint_in_progress:
                                    backlogView.showMessage("Marked the user story as completed.", false);
                                    break;
                                case sprint_completed:
                                    backlogView.showMessage("Marked the user story as in progress.", false);
                                    break;
                            }
                        }
                        else{

                            switch(type){
                                case pb_in_progress:
                                    backlogView.showMessage("An error occurred, failed to mark the user story as completed.", false);
                                    break;
                                case pb_completed:
                                    backlogView.showMessage("An error occurred, failed to mark the user story as in progress.", false);

                                    break;
                                case sprint_in_progress:
                                    backlogView.showMessage("An error occurred, failed to mark the user story as completed.", false);

                                    break;
                                case sprint_completed:
                                    backlogView.showMessage("An error occurred, failed to mark the user story as in progress.", false);
                                    break;
                            }

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
        mRef = mDatabase.getReference().child("projects").child(pid).child("user_stories");
        mRef.child(usid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // User story was deleted successfully
                if (task.isSuccessful()){
                    backlogView.showMessage("User story was deleted.", false);
                }
                else{
                    backlogView.showMessage("An error occurred, failed to delete the user story", false);
                }
            }
        });
    }
}
