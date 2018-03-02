package ca.mvp.scrumtious.scrumtious.presenter_impl;
import android.text.format.DateFormat;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.sql.Timestamp;
import java.util.Calendar;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SprintListPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SprintListViewInt;
import ca.mvp.scrumtious.scrumtious.model.Sprint;
import ca.mvp.scrumtious.scrumtious.view_impl.SprintListActivity;

public class SprintListPresenter implements SprintListPresenterInt {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Query mQuery;

    private SprintListViewInt sprintListView;
    private String pid;

    public SprintListPresenter (SprintListViewInt sprintListView, String pid){
        this.sprintListView = sprintListView;
        this.pid = pid;
    }

    @Override
    public FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> setupSprintListAdapter(String sortBy) {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        // Grab the list of sprints within this project
        mQuery = mRef.child("projects").child(pid).child("sprints").orderByChild(sortBy);

        FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> sprintListAdapter
                = new FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder>(
                Sprint.class,
                R.layout.sprint_row,
                SprintListActivity.SprintsViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(SprintListActivity.SprintsViewHolder viewHolder, Sprint model, int position) {
                final String sid = getRef(position).getKey();

                // Grab the dates
                long startDate = model.getSprintStartDate();
                long endDate = model.getSprintEndDate();
                final String dateFormatted = DateFormat.format("MM/dd/yyyy", startDate).toString()
                        + " to " +  DateFormat.format("MM/dd/yyyy", endDate).toString();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long currentTime = cal.getTimeInMillis();

                Timestamp startDateTimestamp = new Timestamp(startDate);
                Timestamp endDateTimestamp = new Timestamp(endDate);
                Timestamp currentTimestamp = new Timestamp(currentTime);

                // Currently inside of this sprint's time interval
                if (currentTimestamp.after(startDateTimestamp) && currentTimestamp.before(endDateTimestamp)){
                    viewHolder.setCurrentSprintViewVisible();
                }

                // Edge case, could be on the same day as start or end
                if (currentTimestamp.equals(startDateTimestamp) || currentTimestamp.equals(endDateTimestamp)){
                    viewHolder.setCurrentSprintViewVisible();
                }

                viewHolder.setDetails(model.getSprintName(), model.getSprintDesc(), dateFormatted);


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sprintListView.goToSprintScreen(sid);
                    }
                });
            }
            @Override
            public void onDataChanged() {
                sprintListView.setEmptyStateView();
            }
        };
        return sprintListAdapter;
    }


    }
