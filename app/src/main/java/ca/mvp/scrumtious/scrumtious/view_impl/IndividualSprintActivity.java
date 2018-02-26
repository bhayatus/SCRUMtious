package ca.mvp.scrumtious.scrumtious.view_impl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ca.mvp.scrumtious.scrumtious.R;

public class IndividualSprintActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String pid, sid;
    private TabLayout tabs;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private boolean alreadyDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_sprint);
        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        sid = data.getString("sprintId");

        tabs = (TabLayout) findViewById(R.id.individualSprintTabs);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.individualSprintNavDrawer);
        navigationView = (NavigationView) findViewById(R.id.individualSprintNavView);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.individualSprintViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        navigationView.setCheckedItem(R.id.nav_product_backlog);
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
                            // User chooses Project Overview in menu, go there
                            case R.id.nav_overview:
                                // Allow nav drawer to close smoothly before switching activities
                                Handler handler = new Handler();
                                int delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IndividualSprintActivity.this, IndividualProjectActivity.class);
                                        intent.putExtra("projectId", pid);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                            // User chooses product backlog, do nothing as we are already there
                            case R.id.nav_product_backlog:
                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IndividualSprintActivity.this, ProductBacklogActivity.class);
                                        intent.putExtra("projectId", pid);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            case R.id.nav_sprints:
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IndividualSprintActivity.this, SprintListActivity.class);
                                        intent.putExtra("projectId", pid);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);
                                break;
                        }

                        return true;
                    }
                });


        Toolbar toolbar = (Toolbar) findViewById(R.id.individualSprintToolbar);
        setSupportActionBar(toolbar);
        // Sets icon for menu on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){

                case 0:
                    SprintOverviewFragment sprintOverviewFragment = new SprintOverviewFragment();
                    return sprintOverviewFragment;

                case 1:
                    Bundle data = new Bundle();
                    data.putString("projectId", pid);
                    data.putString("sprintId", sid);

                    // This fragment will display info for product backlog in progress user stories
                    data.putString("type", "SPRINT_IN_PROGRESS");

                    // Passes in null, to tell fragment and presenter that this is a pb user story
                    data.putString("sprintId", sid);

                    BacklogFragment pbInProgressFragment = new BacklogFragment();
                    pbInProgressFragment.setArguments(data);
                    return pbInProgressFragment;
                case 2:
                    data = new Bundle();
                    data.putString("projectId", pid);
                    data.putString("sprintId", sid);

                    // This fragment will display info for product backlog completed user stories
                    data.putString("type", "SPRINT_COMPLETED");

                    // Passes in null, to tell fragment and presenter that this is a pb user story
                    data.putString("sprintId", sid);

                    BacklogFragment pbCompletedFragment = new BacklogFragment();
                    pbCompletedFragment.setArguments(data);
                    return pbCompletedFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "OVERVIEW";

                case 1:
                    return "IN PROGRESS";

                case 2:
                    return "COMPLETED";

                default:
                    return null;
            }
        }
    }

}
