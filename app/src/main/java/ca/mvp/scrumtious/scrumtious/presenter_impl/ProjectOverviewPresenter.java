package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectOverviewViewInt;

public class ProjectOverviewPresenter implements ProjectOverviewPresenterInt{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;

    ValueEventListener projectDetailsListener, currentSprintListener, currentVelocityListener, daysListener;

    private ProjectOverviewViewInt projectOverviewView;

    private final String PROJECT_ID;

    private String currentSprintId = "";

    private long totalStories = 0;
    private long completedStories = 0;

    private String title = " ";
    private String desc = " ";

    public ProjectOverviewPresenter(ProjectOverviewViewInt projectOverviewView, String pid){
        this.projectOverviewView = projectOverviewView;
        this.PROJECT_ID = pid;
    }

    // Grabs the info for the project
    @Override
    public void setupProjectDetailsListener() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID);

        projectDetailsListener = firebaseRootReference.addValueEventListener(new ValueEventListener() {

            // Grab the new title and description, and display it
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    title = dataSnapshot.child("projectTitle").getValue().toString();
                    desc = dataSnapshot.child("projectDesc").getValue().toString();
                    projectOverviewView.setProjectDetails(title, desc);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Listener should be removed if user is no longer viewing the fragment
    @Override
    public void removeProjectDetailsListener() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID);
        if (projectDetailsListener != null) {
            firebaseRootReference.removeEventListener(projectDetailsListener);
        }
    }

    @Override
    public void setupCurrentSprintListener() {

        currentSprintId = "";

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();

        // Grab current sprint

        firebaseRootReference.child("projects").child(PROJECT_ID).child("sprints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    Timestamp startTimeStamp = new Timestamp((long) d.child("sprintStartDate").getValue());
                    Timestamp endTimeStamp = new Timestamp((long) d.child("sprintEndDate").getValue());


                    Calendar cal = Calendar.getInstance();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);
                    int year = cal.get(Calendar.YEAR);

                    Calendar currentCalender = new GregorianCalendar(year,month,day);

                    Timestamp currentTimeStamp = new Timestamp(currentCalender.getTimeInMillis());

                    // Within this sprint
                    if (currentTimeStamp.equals(startTimeStamp) || currentTimeStamp.equals(endTimeStamp) ||
                            (currentTimeStamp.after(startTimeStamp) && currentTimeStamp.before(endTimeStamp))){
                            currentSprintId = d.getKey().toString();
                            break;
                    }

                }

                // Only proceed if there are no current sprints
                if (!currentSprintId.equals("")) {


                    currentSprintListener = FirebaseDatabase.getInstance().getReference().child("projects").child(PROJECT_ID)
                            .child("sprints").child(currentSprintId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        long start = (long) dataSnapshot.child("sprintStartDate").getValue();
                                        long end = (long) dataSnapshot.child("sprintEndDate").getValue();
                                        String dateFormatted = DateFormat.format("MM/dd/yyyy", start).toString() +
                                                " to " + DateFormat.format("MM/dd/yyyy", end).toString();
                                        // Set the new details
                                        projectOverviewView.setCurrentSprintDetails(
                                                currentSprintId,
                                                dataSnapshot.child("sprintName").getValue().toString(),
                                                dataSnapshot.child("sprintDesc").getValue().toString(),
                                                dateFormatted
                                        );
                                    }
                                    else{
                                        projectOverviewView.setCurrentSprintDetails("", "", "", "");
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

    @Override
    public void removeCurrentSprintListener() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        if (currentSprintListener != null) {
            firebaseRootReference.child("projects").child(PROJECT_ID).child("sprints").child(currentSprintId).removeEventListener(currentSprintListener);
        }
    }

    @Override
    public void setupCurrentVelocityListener() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("currentVelocity");
        currentVelocityListener = firebaseRootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long currentVelocity = (long) dataSnapshot.getValue();
                    projectOverviewView.setCurrentVelocity(currentVelocity);
                }
                else{
                    projectOverviewView.setCurrentVelocity(-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void removeCurrentVelocityListener() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        if (currentVelocityListener != null) {
            firebaseRootReference.child("projects").child(PROJECT_ID).child("currentVelocity").removeEventListener(currentVelocityListener);
        }
    }

    @Override
    public void setupDaysListener() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("creationTimeStamp");
        daysListener = firebaseRootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long creationTimestamp = (long) dataSnapshot.getValue();

                    // Need to know project creation date without hours, minutes, seconds, and milliseconds
                    GregorianCalendar createdDate = new GregorianCalendar();
                    createdDate.setTime(new Timestamp(creationTimestamp));
                    createdDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                    createdDate.set(GregorianCalendar.MINUTE, 0);
                    createdDate.set(GregorianCalendar.SECOND, 0);
                    createdDate.set(GregorianCalendar.MILLISECOND, 0);

                    // Need to know current date without hours, minutes, seconds, and milliseconds
                    GregorianCalendar currentDate = new GregorianCalendar();
                    currentDate.setTime(new Timestamp(System.currentTimeMillis()));
                    currentDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                    currentDate.set(GregorianCalendar.MINUTE, 0);
                    currentDate.set(GregorianCalendar.SECOND, 0);
                    currentDate.set(GregorianCalendar.MILLISECOND, 0);

                    long daysAfter = 0;

                    while (currentDate.after(createdDate)) {
                        daysAfter++;
                        createdDate.add(GregorianCalendar.DAY_OF_YEAR, 1);
                    }

                    projectOverviewView.setDays(daysAfter);
                }
                else{
                    projectOverviewView.setDays(-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void removeDaysListener() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        if (daysListener != null) {
            firebaseRootReference.child("projects").child(PROJECT_ID).child("creationTimeStamp").removeEventListener(daysListener);
        }
    }

    @Override
    public void getUserStoryProgress() {

        totalStories = 0;
        completedStories = 0;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();

        firebaseRootReference.child("projects").child(PROJECT_ID).child("user_stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // Go through user stories
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        totalStories++;
                        if (d.child("completed").getValue().toString().equals("true")){
                            completedStories++;
                        }
                    }

                    projectOverviewView.setCurrentProgressCircle(totalStories, completedStories);
                }
                // Failed to read progress
                else{
                    projectOverviewView.setCurrentProgressCircle(0, 0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void changeCurrentVelocity(long newVelocity) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();

        Map changeVelocityMap = new HashMap();
        changeVelocityMap.put("/projects/" + PROJECT_ID + "/currentVelocity", newVelocity);

        firebaseRootReference.updateChildren(changeVelocityMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                // Success
                if (task.isSuccessful()){
                    projectOverviewView.showMessage("Successfully changed the velocity.", false);
                }
                // Failure
                else{
                    projectOverviewView.showMessage("An error occurred, failed to change the velocity.", false);
                }
            }
        });
    }

}
