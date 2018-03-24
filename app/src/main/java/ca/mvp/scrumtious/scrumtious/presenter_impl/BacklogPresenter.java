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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.BacklogPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.BacklogViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserStory;
import ca.mvp.scrumtious.scrumtious.view_impl.BacklogFragment;

public class BacklogPresenter implements BacklogPresenterInt {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;
    private Query databaseQuery;

    private BacklogViewInt backlogView;

    private final String BACKLOG_STATUS_TYPE; // Tells the presenter what BACKLOG_STATUS_TYPE of info to grab
    private final String SPRINT_ID; // "null" if in product backlog, regular id if part of sprint
    private final String PROJECT_ID;

    // Different types of backlogs
    private final String PB_IN_PROGRESS = "PB_IN_PROGRESS";
    private final String PB_COMPLETED = "PB_COMPLETED";
    private final String SPRINT_IN_PROGRESS = "SPRINT_IN_PROGRESS";
    private final String SPRINT_COMPLETED = "SPRINT_COMPLETED";

    public BacklogPresenter(BacklogViewInt backlogView, String projectId, String backlogStatusType, String sprintId){
        this.backlogView = backlogView;
        this.PROJECT_ID = projectId;
        this.BACKLOG_STATUS_TYPE = backlogStatusType;
        this.SPRINT_ID = sprintId;
    }


    @Override
    public FirebaseRecyclerAdapter<UserStory, BacklogFragment.BacklogViewHolder> setupBacklogAdapter() {
        firebaseRootReference = FirebaseDatabase.getInstance().getReference();

        String typeQuery = "";

        // Sets the equalTo string that the query needs to use to search for user stories
        switch(BACKLOG_STATUS_TYPE){
            case PB_IN_PROGRESS:
                typeQuery = "false";
                break;
            case PB_COMPLETED:
                typeQuery = "true";
                break;
            case SPRINT_IN_PROGRESS:
                typeQuery = this.SPRINT_ID + "_" + "false";
                break;
            case SPRINT_COMPLETED:
                typeQuery = this.SPRINT_ID + "_" + "true";
                break;
        }

        // Dealing with sprint backlog
        if (BACKLOG_STATUS_TYPE.equals(SPRINT_COMPLETED) || BACKLOG_STATUS_TYPE.equals(SPRINT_IN_PROGRESS)) {
            databaseQuery = firebaseRootReference.child("projects").child(PROJECT_ID).child("user_stories").orderByChild("assignedTo_completed")
                    .equalTo(typeQuery);
        }

        // Product backlog situation
        if (BACKLOG_STATUS_TYPE.equals(PB_COMPLETED) || BACKLOG_STATUS_TYPE.equals(PB_IN_PROGRESS)){
            databaseQuery = firebaseRootReference.child("projects").child(PROJECT_ID).child("user_stories").orderByChild("completed")
                    .equalTo(typeQuery);
        }

        FirebaseRecyclerAdapter<UserStory, BacklogFragment.BacklogViewHolder> backlogListAdapter
                = new FirebaseRecyclerAdapter<UserStory, BacklogFragment.BacklogViewHolder>(
                UserStory.class,
                R.layout.user_story_row,
                BacklogFragment.BacklogViewHolder.class,
                databaseQuery
        ) {


            @Override
            protected void populateViewHolder(final BacklogFragment.BacklogViewHolder viewHolder, UserStory model, int position) {
                // The id of the user story
                final String usid = getRef(position).getKey().toString();
                final BacklogFragment.BacklogViewHolder mViewHolder = viewHolder;

                String nameOfSprint = model.getAssignedToName();
                String assignedToName;

                // User story is in progress
                if (model.getCompleted().equals("false")){
                    viewHolder.setCardRed();
                }
                // User story is completed
                else{
                    viewHolder.setCardGreen();
                    String dateFormatted = "Completed on " +
                            android.text.format.DateFormat.format("MM/dd/yyyy", model.getCompletedDate()).toString()
                            + " at " + android.text.format.DateFormat.format("KK:mm a", model.getCompletedDate()).toString();
                    viewHolder.setCompletedDateDetails(dateFormatted);
                }

                // Not assigned to a sprint, don't bother showing assigned to icon
                if (nameOfSprint.equals("")){
                    assignedToName = "Not yet assigned to a sprint";
                }
                // User story in a sprint, show the sprint it is assigned to
                else {
                    assignedToName = "Assigned to: " + nameOfSprint;
                }

                // If in a sprint, don't show the assigned to layout
                if (BACKLOG_STATUS_TYPE.equals(SPRINT_COMPLETED) || BACKLOG_STATUS_TYPE.equals(SPRINT_IN_PROGRESS)){
                    viewHolder.setAssignedToLayoutInvisible();
                }

                // If in the product backlog, cannot change completed status
                if (BACKLOG_STATUS_TYPE.equals(PB_IN_PROGRESS) || BACKLOG_STATUS_TYPE.equals(PB_COMPLETED)){
                    mViewHolder.setCompletedInvisible();
                }

                viewHolder.setDetails(model.getUserStoryName(), model.getUserStoryPoints(), assignedToName, model.getUserStoryDetails());

                ImageButton completed = viewHolder.getUserStoryRowCompletedImageButton();
                ImageButton delete = viewHolder.getUserStoryRowDeleteImageButton();


                // Show as in progress with icon
                if (BACKLOG_STATUS_TYPE.equals("SPRINT_IN_PROGRESS")) {
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

                        // Check if current date falls within duration of sprint
                        if (BACKLOG_STATUS_TYPE.equals(SPRINT_IN_PROGRESS)){
                            checkWithinSprint(usid, true);
                        }
                        else if(BACKLOG_STATUS_TYPE.equals(SPRINT_COMPLETED)){
                            checkWithinSprint(usid, false);

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
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("projects").child(PROJECT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        // Not allowed to re-assign
                        if (BACKLOG_STATUS_TYPE.equals(PB_COMPLETED) || BACKLOG_STATUS_TYPE.equals(SPRINT_COMPLETED)){
                            backlogView.showMessage("Cannot re-assign a completed user story.", false);
                        }
                        else {
                            backlogView.onLongClickUserStory(usid);
                        }
                        return true;
                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backlogView.goToUserStoryScreen(usid);
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

    // Change the user story completed flag from either true to false, or false to true
    @Override
    public void changeCompletedStatus(String usid, final boolean newStatus){
        final String completed = Boolean.toString(newStatus);
        final String userStoryId = usid;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        // Gets the current user story
        firebaseRootReference.child("projects").child(PROJECT_ID).child("user_stories").child(usid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the assigned to variable, whether it be the product backlog or a specific sprint backlog
                String assignedTo = dataSnapshot.child("assignedTo").getValue().toString();
                final String assignedTo_completed = assignedTo + "_" + completed;

                Map statusMap = new HashMap();

                // Marking as completed, get current timestamp
                if (newStatus){
                    statusMap.put("/projects/" + PROJECT_ID + "/user_stories/" + userStoryId + "/" + "completedDate", System.currentTimeMillis());
                }
                // Marking as in progress
                else{
                    statusMap.put("/projects/" + PROJECT_ID + "/user_stories/" + userStoryId + "/" + "completedDate", 0);
                }

                // Both changes need to happen to ensure atomicity
                statusMap.put("/projects/" + PROJECT_ID + "/user_stories/" + userStoryId + "/" + "completed", completed);
                statusMap.put("/projects/" + PROJECT_ID + "/user_stories/" + userStoryId + "/" + "assignedTo_completed", assignedTo_completed);

                firebaseRootReference.updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){
                            switch(BACKLOG_STATUS_TYPE){

                                case SPRINT_IN_PROGRESS:
                                    backlogView.showMessage("Marked the user story as \"Completed\".", false);
                                    break;
                                case SPRINT_COMPLETED:
                                    backlogView.showMessage("Marked the user story as \"In Progress\".", false);
                                    break;
                            }
                        }
                        else{

                            switch(BACKLOG_STATUS_TYPE){

                                case SPRINT_IN_PROGRESS:
                                    backlogView.showMessage("An error occurred, failed to mark the user story as \"Completed\".", false);

                                    break;
                                case SPRINT_COMPLETED:
                                    backlogView.showMessage("An error occurred, failed to mark the user story as \"In Progress\".", false);
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
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("user_stories");
        firebaseRootReference.child(usid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // User story was deleted successfully
                if (task.isSuccessful()){
                    backlogView.showMessage("Successfully deleted the user story.", false);
                }
                else{
                    backlogView.showMessage("An error occurred, failed to delete the user story.", false);
                }
            }
        });
    }

    // Cannot mark a user story as completed within a sprint, unless the current date falls within the sprint's
    // lifespan
    private void checkWithinSprint(final String usid, final boolean newStatus){

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();

        firebaseRootReference.child("projects").child(PROJECT_ID).child("sprints").child(SPRINT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // converts the longs to timestamp for the snapshot
                Timestamp snapshotStartDateTimestamp = new Timestamp((long) dataSnapshot.child("sprintStartDate")
                        .getValue());
                Timestamp snapshotEndDateTimestamp = new Timestamp((long) dataSnapshot.child("sprintEndDate")
                        .getValue());

                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                Calendar currentCalender = new GregorianCalendar(year,month,day);

                Timestamp currentTimestamp = new Timestamp(currentCalender.getTimeInMillis());


                // On edges
                if (currentTimestamp.equals(snapshotStartDateTimestamp) || currentTimestamp.equals(snapshotEndDateTimestamp)){
                    backlogView.onClickChangeStatus(usid, newStatus);
                }
                // In between
                else if (currentTimestamp.after(snapshotStartDateTimestamp) && currentTimestamp.before(snapshotEndDateTimestamp)){
                    backlogView.onClickChangeStatus(usid, newStatus);
                }
                // Not within
                else{
                    backlogView.showMessage("Cannot change completed status as you must be within the sprint duration.", false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
