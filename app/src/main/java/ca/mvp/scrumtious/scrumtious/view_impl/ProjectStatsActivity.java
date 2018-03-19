package ca.mvp.scrumtious.scrumtious.view_impl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectStatsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectStatsViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectStatsPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;

public class ProjectStatsActivity extends AppCompatActivity implements ProjectStatsViewInt {

    private ProjectStatsPresenterInt projectStatsPresenter;
    private String pid;
    private ValueEventListener projectListener;

    private boolean projectAlreadyDeleted;

    private LineGraphSeries<DataPoint> series;
    private GraphView burndownGraph;
    private SimpleDateFormat sdf;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageButton logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_stats);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        this.projectStatsPresenter = new ProjectStatsPresenter(this, pid);

        this.projectAlreadyDeleted = false;
        burndownGraph = (GraphView) findViewById(R.id.burndownGraph);


        //burndownGraph.getViewport().setScrollable(true);
        //burndownGraph.getViewport().setScalable(true);


        logoutBtn = findViewById(R.id.projectStatsLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(ProjectStatsActivity.this);
            }
        });

        // The following sets up the navigation drawer
        mDrawerLayout = findViewById(R.id.projectStatsNavDrawer);
        navigationView = findViewById(R.id.projectStatsNavView);

        // By default, should highlight chat option to indicate that is where the user is
        navigationView.setCheckedItem(R.id.nav_stats);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        int item = menuItem.getItemId();
                        switch(item){
                            // User chooses project overview in menu, do nothing as we are already there
                            case R.id.nav_overview:
                                // Allow nav drawer to close smoothly before switching activities
                                Handler handler = new Handler();
                                int delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ProjectStatsActivity.this, IndividualProjectActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                            // User chooses product backlog, go to it
                            case R.id.nav_product_backlog:
                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ProjectStatsActivity.this, ProductBacklogActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses to view sprints, go there
                            case R.id.nav_sprints:

                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ProjectStatsActivity.this, SprintListActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses to go to chat room
                            case R.id.nav_chat:

                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ProjectStatsActivity.this, GroupChatActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses to go to project stats, do nothing as already there
                            case R.id.nav_stats:
                                break;
                        }

                        return true;
                    }
                });


        Toolbar toolbar = (Toolbar) findViewById(R.id.projectStatsToolbar);
        setSupportActionBar(toolbar);
        // Sets icon for menu on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        super.onResume();
        projectStatsPresenter.setupBurndownChart();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // User clicks on the menu icon on the top left
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProjectDeleted() {
        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;
            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(ProjectStatsActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSprintDeleted() {

    }

    @Override
    public void onUserStoryDeleted() {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProjectStatsActivity.this, IndividualProjectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("projectId", pid);
        startActivity(intent);
        finish();
    }

    @Override
    public void populateBurndownChart(ArrayList<Date> dates, ArrayList<Long> points) {
        burndownGraph.removeAllSeries();
        burndownGraph.setTitle("Burndown Chart");
        burndownGraph.getViewport().setXAxisBoundsManual(true);
        burndownGraph.getViewport().setMinX(dates.get(0).getTime());
        burndownGraph.getViewport().setMaxX(dates.get((dates.size()-1)).getTime());

        // enable scaling and scrolling
        burndownGraph.getViewport().setScrollable(true); // enables horizontal scrolling
        burndownGraph.getViewport().setScrollableY(true); // enables vertical scrolling
        burndownGraph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        burndownGraph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        Log.e(dates.toString(), points.toString());
        sdf = new SimpleDateFormat("MM/dd/yyyy");
        burndownGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return sdf.format(new Date((long)value));
                }
                else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        DataPoint[] dp = new DataPoint[dates.size()];
        //Log.e(dates.toString(), points.toString());
        series = new LineGraphSeries<DataPoint>();
        //setup first
        long leftOverPoints = points.get(0);
        //Log.e(dates.get(0),Long.toString(leftOverPoints));
        //series.appendData(new DataPoint(dates.get(0),0),false,dates.size());
        dp[0]=new DataPoint(dates.get(0),leftOverPoints);
        for (int i = 1; i<dates.size();i++){
            leftOverPoints = leftOverPoints - points.get(i);
            //Log.e(dates.get(0),Long.toString(leftOverPoints));
            dp[i]= new DataPoint(dates.get(i),leftOverPoints);
        }
        series = new LineGraphSeries<>(dp);
        burndownGraph.addSeries(series);
    }
}
