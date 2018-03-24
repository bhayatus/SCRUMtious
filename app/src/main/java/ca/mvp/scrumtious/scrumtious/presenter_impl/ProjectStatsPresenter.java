package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.text.format.DateFormat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectStatsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectStatsViewInt;

public class ProjectStatsPresenter implements ProjectStatsPresenterInt{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;

    private ProjectStatsViewInt projectStatsView;

    private final String PROJECT_ID;

    private long totalCost;
    private long createdTime;
    private long numTotalUserStories;
    private long numCompletedUserStories;
    private GregorianCalendar createdDate;

    private ArrayList<Long> daysFromStart;
    private ArrayList<Long> costs;


    public ProjectStatsPresenter(ProjectStatsViewInt projectStatsView, String pid){
        this.projectStatsView = projectStatsView;
        this.PROJECT_ID = pid;
    }

    @Override
    public void setupBurndownChart() {
        totalCost = 0;
        numCompletedUserStories = 0;
        numTotalUserStories = 0;
        daysFromStart = new ArrayList<Long>();
        costs = new ArrayList<Long>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID);

        firebaseRootReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    createdTime = (long) dataSnapshot.child("creationTimeStamp").getValue();

                    firebaseDatabase = FirebaseDatabase.getInstance();
                    Query databaseQuery = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("user_stories").orderByChild("completedDate");
                    databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot d: dataSnapshot.getChildren()){
                                    totalCost += Long.parseLong((String)d.child("userStoryPoints").getValue());
                                    numTotalUserStories++;
                                    // User story was marked as completed
                                    if ((long) d.child("completedDate").getValue() != 0){
                                        numCompletedUserStories++;
                                        // Need to know project creation date without hours, minutes, seconds, and milliseconds
                                        createdDate = new GregorianCalendar();
                                        createdDate.setTime(new Timestamp(createdTime));
                                        createdDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                                        createdDate.set(GregorianCalendar.MINUTE, 0);
                                        createdDate.set(GregorianCalendar.SECOND, 0);
                                        createdDate.set(GregorianCalendar.MILLISECOND, 0);

                                        // Now to add the value which indicates how many days past the initial day this is

                                        long completedTime = (long) d.child("completedDate").getValue();

                                        GregorianCalendar completedDate = new GregorianCalendar();
                                        completedDate.setTime(new Timestamp(completedTime));

                                        completedDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                                        completedDate.set(GregorianCalendar.MINUTE, 0);
                                        completedDate.set(GregorianCalendar.SECOND, 0);
                                        completedDate.set(GregorianCalendar.MILLISECOND, 0);

                                        // Now calculate the actual value of days passed
                                        long daysPassed = 0;

                                        while(completedDate.after(createdDate)){
                                            createdDate.add(GregorianCalendar.DAY_OF_YEAR, 1);
                                            daysPassed++;
                                        }

                                        // Add as a pair
                                        daysFromStart.add(daysPassed);
                                        costs.add(Long.parseLong((String)d.child("userStoryPoints").getValue()));

                                    }

                                }

                                // Add the initial entry (first point on chart)
                                daysFromStart.add(0, (long)0);
                                costs.add(0, totalCost);

                                // Send data to graph
                                projectStatsView.populateBurndownChart(daysFromStart, costs);

                                // Populate number of user stories info
                                projectStatsView.populateNumUserStories(numTotalUserStories, numCompletedUserStories);

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
    public void getNumMembers() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("numMembers");

        firebaseRootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    projectStatsView.populateNumMembers((long) dataSnapshot.getValue());
                }
                else{
                    projectStatsView.populateNumMembers(-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getNumSprints() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("numSprints");

        firebaseRootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    projectStatsView.populateNumSprints((long) dataSnapshot.getValue());
                }
                else{
                    projectStatsView.populateNumSprints(-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getProjectCreationDate() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("creationTimeStamp");

        firebaseRootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String dateFormatted = DateFormat.format("MM/dd/yyyy", (long) dataSnapshot.getValue()).toString()
                            + "\n@ " + DateFormat.format("KK:mm a", (long) dataSnapshot.getValue()).toString();

                    projectStatsView.populateProjectCreationDate(dateFormatted);
                }
                else{
                    projectStatsView.populateProjectCreationDate("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
