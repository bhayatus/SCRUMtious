package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectStatsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectStatsViewInt;

public class ProjectStatsPresenter implements ProjectStatsPresenterInt{

    private ProjectStatsViewInt projectStatsView;
    private String pid;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private long totalCost;
    private ArrayList<Date> dates;
    private ArrayList<Long> costs;


    public ProjectStatsPresenter(ProjectStatsViewInt projectStatsView, String pid){
        this.projectStatsView = projectStatsView;
        this.pid = pid;
    }

    @Override
    public void setupBurndownChart() {
        totalCost = 0;
        dates = new ArrayList<Date>();
        costs = new ArrayList<Long>();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid).child("user_stories");
        mRef.orderByChild("completedDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    // Need this to add to total cost, graph y-axis starts with this value after all
                    long userStoryCost = Long.parseLong(d.child("userStoryPoints").getValue().toString());
                    totalCost += userStoryCost;

                    // If user story was marked as completed
                    if ((long) d.child("completedDate").getValue() != 0){
                        final Timestamp stamp = new Timestamp((long) d.child("completedDate").getValue());
                        final Date dateFormatted = new Date(stamp.getTime());
                        costs.add(userStoryCost);
                        dates.add(dateFormatted);
                    }

                }
                // Grab the initial creation date
                mDatabase = FirebaseDatabase.getInstance();
                mRef = mDatabase.getReference().child("projects").child(pid);
                mRef.child("creationTimeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            long createdDate = (long) dataSnapshot.getValue();
                            final Timestamp stamp = new Timestamp(createdDate);
                            final Date dateFormatted = new Date(stamp.getTime());
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
