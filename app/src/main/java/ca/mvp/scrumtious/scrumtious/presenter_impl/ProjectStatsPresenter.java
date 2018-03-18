package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.text.format.DateFormat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectStatsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectStatsViewInt;

public class ProjectStatsPresenter implements ProjectStatsPresenterInt{

    private ProjectStatsViewInt projectStatsView;
    private String pid;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private long totalCost;

    private List<String> dates;
    private List<Long> costs;


    public ProjectStatsPresenter(ProjectStatsViewInt projectStatsView, String pid){
        this.projectStatsView = projectStatsView;
        this.pid = pid;
    }

    @Override
    public void setupBurndownChart() {
        dates = new ArrayList<String>();
        costs = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid).child("sprints");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Go through list of sprints
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    String sprintId = d.getKey().toString();

                    mDatabase = FirebaseDatabase.getInstance();
                    mRef = mDatabase.getReference().child("projects").child(pid).child("sprints")
                            .child(sprintId).child("user_stories");
                    mRef.orderByChild("completedDate").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot d: dataSnapshot.getChildren()){
                                String userStoryId = d.getKey().toString();

                                // Need this to add to total cost, graph y-axis starts with this value after all
                                long userStoryCost = (long) d.child("userStoryPoints").getValue();
                                totalCost += userStoryCost;

                                // If user story was marked as completed
                                if ((long) d.child("completedDate").getValue() != 0){
                                    long completedDate = (long) d.child("completedDate").getValue();
                                    final String dateFormatted = DateFormat.format("MM/dd/yyyy", completedDate).toString();

                                    costs.add(userStoryCost);
                                    dates.add(dateFormatted);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                // Grab the initial creation date
                mDatabase = FirebaseDatabase.getInstance();
                mRef = mDatabase.getReference().child("projects").child(pid);
                mRef.child("creationTimeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            long createdDate = (long) dataSnapshot.getValue();
                            final String dateFormatted = DateFormat.format("MM/dd/yyyy", createdDate).toString();
                            dates.add(0, dateFormatted);
                            costs.add(0, totalCost);

                            // Send data to view to display in burndown chart
                            projectStatsView.populateBurndownChart(dates, costs);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
