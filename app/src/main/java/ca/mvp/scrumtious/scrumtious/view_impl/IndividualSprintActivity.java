package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualSprintPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualSprintViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.IndividualSprintPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class IndividualSprintActivity extends AppCompatActivity implements IndividualSprintViewInt{

    private IndividualSprintPresenterInt individualSprintPresenter;
    private String pid, sid;
    private ValueEventListener projectListener, sprintListener;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ImageButton logoutBtn;
    private ImageButton deleteBtn;
    private ProgressDialog deleteSprintProgressDialog;

    private boolean projectAlreadyDeleted;
    private boolean sprintAlreadyDeleted;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_sprint);
        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        sid = data.getString("sprintId");

        this.individualSprintPresenter = new IndividualSprintPresenter(this, pid, sid);
        individualSprintPresenter.checkIfOwner();

        projectAlreadyDeleted = false; // Project isn't deleted yet
        sprintAlreadyDeleted = false; // Sprint isn't deleted yet

        deleteBtn = findViewById(R.id.individualSprintDeleteBtn);
        logoutBtn = findViewById(R.id.individualSprintLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(IndividualSprintActivity.this);
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.individualSprintTabs);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.individualSprintNavDrawer);
        navigationView = (NavigationView) findViewById(R.id.individualSprintNavView);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.individualSprintViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

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
                                        Intent intent = new Intent(IndividualSprintActivity.this, ProductBacklogActivity.class);
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
                                        Intent intent = new Intent(IndividualSprintActivity.this, SprintListActivity.class);
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


        Toolbar toolbar = (Toolbar) findViewById(R.id.individualSprintToolbar);
        setSupportActionBar(toolbar);
        // Sets icon for menu on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        sprintListener = ListenerHelper.setupSprintDeletedListener(this, pid, sid);
        super.onResume();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        ListenerHelper.removeSprintDeletedListener(sprintListener, pid, sid);
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
        Intent intent = new Intent(IndividualSprintActivity.this, SprintListActivity.class);
        intent.putExtra("projectId", pid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Project was deleted by another user, or user was removed from the project
    @Override
    public void onProjectDeleted() {

        if (deleteSprintProgressDialog != null  && deleteSprintProgressDialog.isShowing()) {
            deleteSprintProgressDialog.dismiss();
        }

        if (!projectAlreadyDeleted){
            projectAlreadyDeleted = true;

            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(IndividualSprintActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    // Sprint was deleted by another user
    @Override
    public void onSprintDeleted() {

        if (deleteSprintProgressDialog != null  && deleteSprintProgressDialog.isShowing()) {
            deleteSprintProgressDialog.dismiss();
        }

        if (!sprintAlreadyDeleted){
            sprintAlreadyDeleted = true;

            // Return to sprint list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(IndividualSprintActivity.this, SprintListActivity.class);
            intent.putExtra("projectId", pid);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onUserStoryDeleted() {
        // Needs to be here even if not implemented
    }

    @Override
    public void onTaskDeleted() {
        // Needs to be here even if not implemented
    }

    // Delete sprint button is clicked
    public void onClickDelete(View view) {
        LayoutInflater inflater = (this).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_delete_project, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Sprint?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this sprint? Enter your password below to confirm.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password before deleting sprint
                        EditText passwordET = (EditText) alertView.findViewById(R.id.alert_dialogue_delete_password_text_field);
                        String password = passwordET.getText().toString().trim();

                        // Cannot send null password
                        if(password == null){
                            showMessage("Password incorrect, could not delete the sprint.", false);
                        }
                        else {
                            // Cannot send empty string
                            if(password.length() == 0){
                                showMessage("Password incorrect, could not delete the sprint.", false);
                            }
                            else {

                                // Creates a dialog that appears to tell the user that sprint deletion is occurring
                                deleteSprintProgressDialog = new ProgressDialog(IndividualSprintActivity.this);
                                deleteSprintProgressDialog.setTitle("Delete Sprint");
                                deleteSprintProgressDialog.setCancelable(false);
                                deleteSprintProgressDialog.setMessage("Attempting to delete sprint...");
                                deleteSprintProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                deleteSprintProgressDialog.show();

                                // Password is of valid type, send it to the backend to validate
                                individualSprintPresenter.validatePassword(password);
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    // Only project owner can view the sprint delete button
    @Override
    public void setDeleteInvisible() {
        deleteBtn.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message, boolean showAsToast) {

        if (deleteSprintProgressDialog != null && deleteSprintProgressDialog.isShowing()){
            deleteSprintProgressDialog.dismiss();
        }

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

                // Overview Tab
                case 0:
                    Bundle data = new Bundle();
                    data.putString("projectId", pid);
                    data.putString("sprintId", sid);

                    SprintOverviewFragment sprintOverviewFragment = new SprintOverviewFragment();
                    sprintOverviewFragment.setArguments(data);
                    return sprintOverviewFragment;

                // In Progress Tab
                case 1:
                    data = new Bundle();
                    data.putString("projectId", pid);
                    // This fragment will display info for product backlog in progress user stories
                    data.putString("type", "SPRINT_IN_PROGRESS");

                    // Passes in null, to tell fragment and presenter that this is a pb user story
                    data.putString("sprintId", sid);

                    BacklogFragment pbInProgressFragment = new BacklogFragment();
                    pbInProgressFragment.setArguments(data);
                    return pbInProgressFragment;

                    // Completed Tab
                case 2:
                    data = new Bundle();
                    data.putString("projectId", pid);
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
            // Show 3 total tabs
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
