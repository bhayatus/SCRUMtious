package ca.mvp.scrumtious.scrumtious.view_impl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.achartengine.model.XYSeries;

import java.util.ArrayList;
import java.util.Date;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectStatsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectStatsViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectStatsPresenter;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;

public class ProjectStatsActivity extends AppCompatActivity implements ProjectStatsViewInt {

    private ProjectStatsPresenterInt projectStatsPresenter;
    private String pid;
    private ValueEventListener projectListener;
    private boolean projectAlreadyDeleted;
    private LineGraphSeries<DataPoint> series;
    private GraphView burndownGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_stats);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        this.projectStatsPresenter = new ProjectStatsPresenter(this, pid);

        this.projectAlreadyDeleted = false;
        burndownGraph = (GraphView) findViewById(R.id.burndownGraph);
    }


    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        super.onResume();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        super.onPause();
    }

    // TODO
    @Override
    public void onProjectDeleted() {

    }

    @Override
    public void onSprintDeleted() {

    }

    @Override
    public void onUserStoryDeleted() {

    }

    @Override
    public void populateBurndownChart(ArrayList<String> dates, ArrayList<Long> points) {

        //setup first
        long leftOverPoints = points.get(0);
        Log.e(dates.get(0),Long.toString(leftOverPoints));
        //series.appendData(new DataPoint(dates.get(0),leftOverPoints),true,dates.size());
        for (int i = 1; i<dates.size();i++){
            leftOverPoints = leftOverPoints - points.get(i);
            Log.e(dates.get(0),Long.toString(leftOverPoints));
            //series.appendData(new DataPoint(dates.get(i),leftOverPoints), true,dates.size());
        }
        burndownGraph.addSeries(series);
    }
}
