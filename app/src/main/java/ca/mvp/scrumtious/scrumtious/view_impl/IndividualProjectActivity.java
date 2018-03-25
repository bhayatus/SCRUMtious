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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualProjectViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.IndividualProjectPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class IndividualProjectActivity extends AppCompatActivity implements IndividualProjectViewInt {

    private IndividualProjectPresenterInt individualProjectPresenter;
    private String pid;
    private ValueEventListener projectListener;

    private DrawerLayout individualProjectDrawerLayout;
    private NavigationView individualProjectNavigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager individualProjectViewPager;
    private ImageButton individualProjectDeleteImageButton, individualProjectLogoutImageButton;
    private ProgressDialog deleteProjectProgressDialog;

    private boolean projectAlreadyDeleted;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_project);

         projectAlreadyDeleted = false; // Project hasn't been deleted yet

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        individualProjectPresenter = new IndividualProjectPresenter(this, pid);
        individualProjectPresenter.checkIfOwner();

        individualProjectDeleteImageButton = findViewById(R.id.individualProjectDeleteImageButton);
        individualProjectLogoutImageButton = findViewById(R.id.individualProjectLogoutImageButton);
        individualProjectLogoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(IndividualProjectActivity.this);
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        individualProjectViewPager = findViewById(R.id.individualProjectViewPager);
        individualProjectViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout individualProjectTabLayout = findViewById(R.id.individualProjectTabLayout);
        individualProjectTabLayout.setupWithViewPager(individualProjectViewPager);

        // The following sets up the navigation drawer
        individualProjectDrawerLayout = findViewById(R.id.individualProjectDrawerLayout);
        individualProjectNavigationView = findViewById(R.id.individualProjectNavigationView);

        // By default, should highlight project overview option to indicate that is where the user is
        individualProjectNavigationView.setCheckedItem(R.id.nav_overview);
        individualProjectNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        individualProjectDrawerLayout.closeDrawers();
                        int item = menuItem.getItemId();
                        switch(item){
                            // User chooses project overview in menu, do nothing as we are already there
                            case R.id.nav_overview:
                                break;
                            // User chooses product backlog, go to it
                            case R.id.nav_product_backlog:
                                // Allow nav drawer to close smoothly before switching activities
                                Handler handler = new Handler();
                                int delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(IndividualProjectActivity.this, ProductBacklogActivity.class);
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
                                        Intent intent = new Intent(IndividualProjectActivity.this, SprintListActivity.class);
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
                                        Intent intent = new Intent(IndividualProjectActivity.this, GroupChatActivity.class);
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
                                        Intent intent = new Intent(IndividualProjectActivity.this, ProjectStatsActivity.class);
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


        Toolbar individualProjectToolbar = findViewById(R.id.individualProjectToolbar);
        setSupportActionBar(individualProjectToolbar);
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
    public void onBackPressed() {
        Intent intent = new Intent(IndividualProjectActivity.this, ProjectTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // User clicks on the menu icon on the top left
            case android.R.id.home:
                individualProjectDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Project was deleted by another user, or user was removed from the project
    @Override
    public void onProjectDeleted() {
        onSuccessfulDeletion();
    }

    @Override
    public void onSprintDeleted() {
        // Needs to be here even if not implemented
    }

    @Override
    public void onUserStoryDeleted() {
        // Needs to be here even if not implemented
    }


    // Project no longer exists due to user deleting it normally, go back
    public void onSuccessfulDeletion() {

        if (deleteProjectProgressDialog != null && deleteProjectProgressDialog.isShowing()){
            deleteProjectProgressDialog.dismiss();
        }

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;
            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(IndividualProjectActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    // Called when current user isn't the owner of the project, they should not be
    // able to see the delete project button
    public void setDeleteInvisible(){
        individualProjectDeleteImageButton.setVisibility(View.GONE);
    }

    public void showMessage(String message, boolean showAsToast) {

        if (deleteProjectProgressDialog != null && deleteProjectProgressDialog.isShowing()){
            deleteProjectProgressDialog.dismiss();
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

    // Delete project button is clicked
    public void onClickDelete(View view) {
        LayoutInflater inflater = (this).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_delete_project, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.LoginAlertDialog));
        builder.setTitle("Delete Project?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this project? Enter your password below to confirm.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password before deleting
                        EditText passwordET = alertView.findViewById(R.id.alertDialogueDeletePasswordEditText);
                        String password = passwordET.getText().toString().trim();

                        // Cannot send null password
                        if(password == null){
                            showMessage("Incorrect password, could not delete the project.", false);
                        }
                        else {
                            // Cannot send empty string
                            if(password.length() == 0){
                                showMessage("Incorrect password, could not delete the project.", false);
                            }
                            else {

                                // Creates a dialog that appears to tell the user that deleting a user is still occurring
                                deleteProjectProgressDialog = new ProgressDialog(IndividualProjectActivity.this, R.style.AppCompatAlertDialogStyle);;
                                deleteProjectProgressDialog.setTitle("Delete Project");
                                deleteProjectProgressDialog.setCancelable(false);
                                deleteProjectProgressDialog.setMessage("Attempting to delete project...");
                                deleteProjectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                deleteProjectProgressDialog.show();

                                // Password is of valid type, send it to backend to validate
                                individualProjectPresenter.validatePassword(password);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                // Project Overview Tab
                case 0:
                    Bundle data = new Bundle();
                    data.putString("projectId", pid);
                    ProjectOverviewFragment projectOverviewFragment = new ProjectOverviewFragment();
                    projectOverviewFragment.setArguments(data);
                    return projectOverviewFragment;

                    // Members Tab
                case 1:
                    data = new Bundle();
                    data.putString("projectId", pid);
                    ProjectMembersFragment projectMembersFragment = new ProjectMembersFragment();
                    projectMembersFragment.setArguments(data);
                    return projectMembersFragment;
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
                    return "OVERVIEW";

                case 1:
                    return "MEMBERS";

                default:
                    return null;
            }
        }
    }
}
