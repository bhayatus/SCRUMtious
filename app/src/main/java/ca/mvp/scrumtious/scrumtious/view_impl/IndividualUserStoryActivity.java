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
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualUserStoryViewInt;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;

public class IndividualUserStoryActivity extends AppCompatActivity implements IndividualUserStoryViewInt {

    private String pid, usid;
    private ValueEventListener projectListener, userStoryListener;

    private DrawerLayout individualUserStoryDrawerLayout;
    private NavigationView individualUserStoryNavigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager individualUserStoryViewPager;
    private TabLayout individualUserStoryTabLayout;
    private ImageButton individualUserStoryLogoutImageButton, individualUserStoryHelpImageButton;

    private boolean projectAlreadyDeleted;
    private boolean userStoryAlreadyDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_user_story);
        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        usid = data.getString("userStoryId");

        projectAlreadyDeleted = false; // Project isn't deleted yet
        userStoryAlreadyDeleted = false; // User story isn't deleted yet

        individualUserStoryLogoutImageButton = findViewById(R.id.individualUserStoryLogoutImageButton);
        individualUserStoryLogoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(IndividualUserStoryActivity.this);
            }
        });

        // Displays a help popup
        individualUserStoryHelpImageButton = findViewById(R.id.individualUserStoryHelpImageButton);
        individualUserStoryHelpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(new ContextThemeWrapper(IndividualUserStoryActivity.this, R.style.LoginAlertDialog))
                        .setTitle("Need Help?")
                        .setMessage("Welcome to the task board! You can start by creating a task below. Once you have done that," +
                                " you will be able to see the task in the \"Not Started\" tab. " + "\n" +
                                "From there, you can assign a task to a " +
                                " team member by holding down on it for a few seconds. A dialog will then pop up, allowing you " +
                                "to assign the task to a member, or no one." + "\n" +
                                "You can also switch the status of the task by clicking on the toolbar icon, or delete the task" +
                                " entirely by selecting the trash can icon.")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        individualUserStoryTabLayout = findViewById(R.id.individualUserStoryTabLayout);
        individualUserStoryDrawerLayout = findViewById(R.id.individualUserStoryDrawerLayout);
        individualUserStoryNavigationView = findViewById(R.id.individualUserStoryNavigationView);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        individualUserStoryViewPager = findViewById(R.id.individualUserStoryViewPager);
        individualUserStoryViewPager.setAdapter(mSectionsPagerAdapter);

        individualUserStoryTabLayout.setupWithViewPager(individualUserStoryViewPager);
        individualUserStoryViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(individualUserStoryTabLayout));
        individualUserStoryTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(individualUserStoryViewPager));

        individualUserStoryNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        individualUserStoryDrawerLayout.closeDrawers();
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
                                        Intent intent = new Intent(IndividualUserStoryActivity.this, IndividualProjectActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                            // User chooses product backlog, go there
                            case R.id.nav_product_backlog:
                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IndividualUserStoryActivity.this, ProductBacklogActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses sprint list, go there
                            case R.id.nav_sprints:
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IndividualUserStoryActivity.this, SprintListActivity.class);
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
                                        Intent intent = new Intent(IndividualUserStoryActivity.this, GroupChatActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses to go to project stats
                            case R.id.nav_stats:
                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IndividualUserStoryActivity.this, ProjectStatsActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                        }

                        return true;
                    }
                });


        Toolbar individualUserStoryToolbar = findViewById(R.id.individualUserStoryToolbar);
        setSupportActionBar(individualUserStoryToolbar);
        // Sets icon for menu on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        userStoryListener = ListenerHelper.setupUserStoryDeletedListener(this, pid, usid);
        super.onResume();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        ListenerHelper.removeUserStoryDeletedListener(userStoryListener, pid, usid);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // User clicks on the menu icon on the top left
            case android.R.id.home:
                individualUserStoryDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Project was deleted by another user, or user was removed from the project
    @Override
    public void onProjectDeleted() {

        if (!projectAlreadyDeleted){
            projectAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(IndividualUserStoryActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSprintDeleted() {

    }

    // User story was deleted
    @Override
    public void onUserStoryDeleted() {
        if (!userStoryAlreadyDeleted){
            userStoryAlreadyDeleted = true;

            // Return to product backlog
            Intent intent = new Intent(IndividualUserStoryActivity.this, ProductBacklogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("projectId", pid);
            startActivity(intent);
            finish();
        }
    }

    // Go to create task screen
    public void onClickAddTask(View view){
        Intent intent = new Intent(IndividualUserStoryActivity.this, CreateTaskActivity.class);
        intent.putExtra("projectId", pid);
        intent.putExtra("userStoryId", usid);
        startActivity(intent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){

                // Not Started Tab
                case 0:
                    Bundle data = new Bundle();
                    data.putString("projectId", pid);
                    data.putString("userStoryId", usid);
                    data.putString("type", "not_started");
                    TaskBoardFragment notStartedFragment = new TaskBoardFragment();
                    notStartedFragment.setArguments(data);

                    return notStartedFragment;

                // In Progress Tab
                case 1:
                    data = new Bundle();
                    data.putString("projectId", pid);
                    data.putString("userStoryId", usid);
                    data.putString("type", "in_progress");
                    TaskBoardFragment inProgressFragment = new TaskBoardFragment();
                    inProgressFragment.setArguments(data);
                    return inProgressFragment;

                // Completed Tab
                case 2:
                    data = new Bundle();
                    data.putString("projectId", pid);
                    data.putString("userStoryId", usid);
                    data.putString("type", "completed");
                    TaskBoardFragment completedFragment = new TaskBoardFragment();
                    completedFragment.setArguments(data);
                    return completedFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total tabs
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "NOT STARTED";

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
