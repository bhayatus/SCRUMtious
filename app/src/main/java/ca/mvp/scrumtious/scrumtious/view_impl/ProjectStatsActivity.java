package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

    private TextView projectCreationDateView, numMembersView, numSprintsView, numUserStoriesView, daysPassedView,
    totalPointsView, emptyChartView;
    private TextView projectCreationDateViewNotEmpty, numMembersViewNotEmpty, numSprintsViewNotEmpty,
            numUserStoriesViewNotEmpty;
    private LineChart burndownChart;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageButton logoutBtn, helpBtn, refreshBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_stats);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        this.projectStatsPresenter = new ProjectStatsPresenter(this, pid);

        this.projectAlreadyDeleted = false;

        this.burndownChart = (LineChart) findViewById(R.id.projectStatsBurndownChart);

        this.projectCreationDateView = (TextView) findViewById(R.id.projectStatsCreationDateErrorTextView);
        this.numMembersView = (TextView) findViewById(R.id.projectStatsNumMembersErrorTextView);
        this.numSprintsView = (TextView) findViewById(R.id.projectStatsNumSprintsErrorTextView);
        this.numUserStoriesView = (TextView) findViewById(R.id.projectStatsNumUserStoriesErrorTextView);

        this.totalPointsView = (TextView) findViewById(R.id.projectStatsTotalPointsTextView);
        this.daysPassedView = (TextView) findViewById(R.id.projectStatsDaysPassedTextView);
        this.emptyChartView = (TextView) findViewById(R.id.projectStatsEmptyChartTextView);

        this.projectCreationDateViewNotEmpty = findViewById(R.id.projectStatsCreationDateValueTextView);
        this.numMembersViewNotEmpty = findViewById(R.id.projectStatsNumMembersValueTextView);
        this.numSprintsViewNotEmpty = findViewById(R.id.projectStatsNumSprintsValueTextView);
        this.numUserStoriesViewNotEmpty = findViewById(R.id.projectStatsNumUserStoriesValueTextView);

        this.projectCreationDateViewNotEmpty.setVisibility(View.GONE);
        this.numMembersViewNotEmpty.setVisibility(View.GONE);
        this.numMembersViewNotEmpty.setVisibility(View.GONE);
        this.numUserStoriesViewNotEmpty.setVisibility(View.GONE);

        logoutBtn = findViewById(R.id.projectStatsLogoutImageButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(ProjectStatsActivity.this);
            }
        });

        helpBtn = findViewById(R.id.projectStatsHelpImageButton);
        // Displays a help popup
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProjectStatsActivity.this)
                        .setTitle("About the Burndown Chart")
                        .setMessage("The burndown chart represents the progress your group has made through" +
                                " completing user stories." + "\n" +
                                "On the y-axis, the numbers represent the total amount of" +
                                " user story points left that need to be completed. " + "\n" +
                                "On the x-axis, the numbers represent" +
                                " the number of days that have passed since the project was created." +
                                "\n" +
                                "You can change the scale of either axis by pinching in or pinching out on the chart.")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        refreshBtn = findViewById(R.id.projectStatsRefreshImageButton);
        // Displays a help popup
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProjectStatsActivity.this)
                        .setTitle("Refresh Project Stats")
                        .setMessage("Refresh all project stats?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Refresh all stats
                                Intent intent = new Intent(ProjectStatsActivity.this, ProjectStatsActivity.class);
                                intent.putExtra("projectId", pid);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null) // Do nothing
                        .show();
            }
        });


        // The following sets up the navigation drawer
        mDrawerLayout = findViewById(R.id.projectStatsDrawerLayout);
        navigationView = findViewById(R.id.projectStatsNavigationView);

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
        projectStatsPresenter.getNumMembers();
        projectStatsPresenter.getNumSprints();
        projectStatsPresenter.getProjectCreationDate();
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
    public void populateBurndownChart(ArrayList<Long> daysFromStart, ArrayList<Long> costs) {
        List<Entry> entries = new ArrayList<Entry>();

        // the labels that should be drawn on the XAxis
        long leftOverPoints = costs.get(0);

        entries.add(new Entry(0, leftOverPoints));

        for(int i = 1; i < daysFromStart.size(); i++) {
            leftOverPoints = leftOverPoints - costs.get(i);
            entries.add(new Entry(daysFromStart.get(i), leftOverPoints));
        }


        LineDataSet dataSet = new LineDataSet(entries, "Burndown Chart"); // add entries to dataset
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(0);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleColorHole(Color.RED);
        dataSet.setCircleRadius(4f);
        dataSet.setLineWidth(3f);

        List<Entry> finalPoint = new ArrayList<Entry>();
        finalPoint.add(new Entry(daysFromStart.get(daysFromStart.size()-1)+1, 0));

        LineDataSet finalSet = new LineDataSet(finalPoint, "");
        finalSet.setVisible(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(dataSet);
        sets.add(finalSet);

        LineData lineData = new LineData(sets);

        burndownChart.setData(lineData);

        XAxis xAxis = burndownChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);

        xAxis.setGranularity(1f);

        YAxis yAxisLeft = burndownChart.getAxisLeft();
        yAxisLeft.setTextSize(10f);
        yAxisLeft.setTextColor(Color.BLACK);

        YAxis yAxisRight = burndownChart.getAxisRight();
        yAxisRight.setDrawLabels(false);

        burndownChart.getLegend().setEnabled(false);
        burndownChart.getDescription().setEnabled(false);
        burndownChart.setDragEnabled(true);
        burndownChart.setScaleEnabled(true);
        burndownChart.setHighlightPerTapEnabled(false);
        burndownChart.setHighlightPerDragEnabled(false);

        burndownChart.invalidate(); // refresh

        if (!burndownChart.isEmpty()){
        burndownChart.setVisibility(View.VISIBLE);
        daysPassedView.setVisibility(View.VISIBLE);
        totalPointsView.setVisibility(View.VISIBLE);
        emptyChartView.setVisibility(View.GONE);
        }
    }

    @Override
    public void populateNumMembers(long numMembers) {

        if (numMembers == -1){
            numMembersViewNotEmpty.setVisibility(View.GONE);
            numMembersView.setVisibility(View.VISIBLE);
            numMembersView.setText("Unable to retrieve number of members data");
            return;
        }
        else if(numMembers == 1){
            numMembersView.setVisibility(View.GONE);
            numMembersViewNotEmpty.setText("1");
            numMembersViewNotEmpty.setVisibility(View.VISIBLE);

            return;
        }

        numMembersView.setVisibility(View.GONE);
        numMembersViewNotEmpty.setText(String.valueOf(numMembers));
        numMembersViewNotEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void populateNumSprints(long numSprints) {
        if (numSprints == -1){
            numSprintsViewNotEmpty.setVisibility(View.GONE);
            numSprintsView.setVisibility(View.VISIBLE);
            numSprintsView.setText("Unable to retrieve number of sprints data");
            return;
        }
        else if (numSprints == 0){
            numSprintsViewNotEmpty.setVisibility(View.GONE);
            numSprintsView.setVisibility(View.VISIBLE);
            numSprintsView.setText("You currently have no sprints in this project");
            return;
        }
        else if(numSprints == 1){
            numSprintsView.setVisibility(View.GONE);
            numSprintsViewNotEmpty.setText(String.valueOf(numSprints));
            numSprintsViewNotEmpty.setVisibility(View.VISIBLE);
            return;
        }

        numSprintsView.setVisibility(View.GONE);
        numSprintsViewNotEmpty.setText(String.valueOf(numSprints));
        numSprintsViewNotEmpty.setVisibility(View.VISIBLE);

    }

    @Override
    public void populateNumUserStories(long total, long completed) {

        if (completed != 0) {
            numUserStoriesView.setVisibility(View.GONE);
            numUserStoriesViewNotEmpty.setText(String.valueOf(completed) + "/" + String.valueOf(total));
            numUserStoriesViewNotEmpty.setVisibility(View.VISIBLE);
        } else if (completed <= 0 || total <= 0) {
            numUserStoriesViewNotEmpty.setVisibility(View.GONE);
            numUserStoriesView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void populateProjectCreationDate(String date) {
        if (date.equals("")){
            projectCreationDateViewNotEmpty.setVisibility(View.GONE);
            projectCreationDateView.setVisibility(View.VISIBLE);
            projectCreationDateView.setText("Unable to retrieve project creation date");
            return;
        }

        projectCreationDateView.setVisibility(View.GONE);
        projectCreationDateViewNotEmpty.setText(date);
        projectCreationDateViewNotEmpty.setVisibility(View.VISIBLE);
    }
}
