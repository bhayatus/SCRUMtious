package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProductBacklogPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProductBacklogViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProductBacklogPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class ProductBacklogActivity extends AppCompatActivity implements ProductBacklogViewInt {

    private ProductBacklogPresenterInt productBacklogPresenter;
    private String pid;
    private ValueEventListener projectListener;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageButton logoutBtn, helpBtn;

    private boolean projectAlreadyDeleted;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_backlog);

        projectAlreadyDeleted = false; // Project is not deleted at this point

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        this.productBacklogPresenter = new ProductBacklogPresenter(this, pid);

        logoutBtn = findViewById(R.id.productBacklogLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(ProductBacklogActivity.this);
            }
        });

        // Displays a help popup
        helpBtn = findViewById(R.id.productBacklogHelpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProductBacklogActivity.this)
                        .setTitle("Need Help?")
                        .setMessage("To get started, you can add a new user story to the product backlog with the button " +
                                "below. " + "\n" +
                                "User stories can then be deleted, or marked as completed/in progress with their respective " +
                                "buttons. " + "\n" +
                                "To assign a user story to a sprint or the product backlog, click down it " +
                                "for a few seconds. A dialog will pop up, allowing you to make your selection.")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.productBacklogViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.productBacklogTabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // The following sets up the navigation drawer
        mDrawerLayout = findViewById(R.id.productBacklogNavDrawer);
        navigationView = findViewById(R.id.productBacklogNavView);

        // By default, should highlight product backlog option to indicate that is where the user is
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
                            // User chooses project overview in menu, go there
                            case R.id.nav_overview:
                                // Allow nav drawer to close smoothly before switching activities
                                Handler handler = new Handler();
                                int delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ProductBacklogActivity.this, IndividualProjectActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                            // User chooses product backlog, do nothing as we are already there
                            case R.id.nav_product_backlog:
                                break;

                            // User chooses to view sprints, go there
                            case R.id.nav_sprints:
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ProductBacklogActivity.this, SprintListActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);
                                break;

                            // TODO
                            case R.id.nav_stats:
                                break;
                        }

                        return true;
                    }
                });


        Toolbar toolbar = (Toolbar) findViewById(R.id.productBacklogToolbar);
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
    public void onBackPressed() {
        Intent intent = new Intent(ProductBacklogActivity.this, ProjectTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Project was deleted by another user, or user was removed from the project
    @Override
    public void onProjectDeleted() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;

            // Return to project list screen, and clear the task stack so we can't go back
            Intent intent = new Intent(ProductBacklogActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSprintDeleted() {
        // Needs to be here even if not implemented
    }

    @Override
    public void onUserStoryDeleted() {
        // Needs to be here even if not implemented
    }

    @Override
    public void onTaskDeleted() {
        // Needs to be here even if not implemented
    }

    // User clicked the add new user story button, go to that screen
    public void onClickAddUserStory(View view){
        Intent intent = new Intent(ProductBacklogActivity.this, CreateUserStoryActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message, boolean showAsToast) {

        // Show message in toast so it persists across activity transitions
        if (showAsToast){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        else {
            // Call the utils class method to handle making the snackbar
            SnackbarHelper.showSnackbar(this, message);
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                // In Progress Tab
                case 0:
                    Bundle data = new Bundle();
                    data.putString("projectId", pid);

                    // This fragment will display info for product backlog in progress user stories
                    data.putString("type", "PB_IN_PROGRESS");

                    // Passes in null, to tell fragment and presenter that this is a pb user story
                    data.putString("sprintId", "null");

                    BacklogFragment pbInProgressFragment = new BacklogFragment();
                    pbInProgressFragment.setArguments(data);
                    return pbInProgressFragment;

                // Completed Tab
                case 1:
                    data = new Bundle();
                    data.putString("projectId", pid);

                    // This fragment will display info for product backlog completed user stories
                    data.putString("type", "PB_COMPLETED");

                    // Passes in null, to tell fragment and presenter that this is a pb user story
                    data.putString("sprintId", "null");

                    BacklogFragment pbCompletedFragment = new BacklogFragment();
                    pbCompletedFragment.setArguments(data);
                    return pbCompletedFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total tabs
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "IN PROGRESS";

                case 1:
                    return "COMPLETED";

                default:
                    return null;
            }
        }
    }
}
