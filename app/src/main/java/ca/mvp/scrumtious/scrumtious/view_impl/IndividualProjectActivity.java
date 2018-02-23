package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualProjectViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.IndividualProjectPresenter;

public class IndividualProjectActivity extends AppCompatActivity implements IndividualProjectViewInt {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageButton deleteBtn;
    private String pid;
    private boolean alreadyDeleted;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;


    private IndividualProjectPresenterInt individualProjectPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_project);

        alreadyDeleted = false; // Project hasn't been deleted yet

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        individualProjectPresenter = new IndividualProjectPresenter(this, pid);
        individualProjectPresenter.setupProjectDeletedListener();
        individualProjectPresenter.checkIfOwner();

        deleteBtn = findViewById(R.id.individualProjectDeleteBtn);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.individualProjectViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.individualProjectTabs);
        tabs.setupWithViewPager(mViewPager);

        // The following sets up the navigation drawer
        mDrawerLayout = findViewById(R.id.individualProjectNavDrawer);
        navigationView = findViewById(R.id.individualProjectNavView);

        // By default, should highlight project overview option to indicate that is where the user is
        navigationView.setCheckedItem(R.id.nav_overview);
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
                            // User chooses Project Overview in menu, do nothing as we are already there
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
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                        }

                        return true;
                    }
                });


        Toolbar toolbar = (Toolbar) findViewById(R.id.individualProjectToolbar);
        setSupportActionBar(toolbar);
        // Sets icon for menu on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // Used when the menu icon is clicked to open the navigation drawer
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


    // Project no longer exists, go back
    public void onSuccessfulDeletion() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!alreadyDeleted) {
            alreadyDeleted = true;
            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(IndividualProjectActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void setDeleteInvisible(){
        deleteBtn.setVisibility(View.GONE);
    }

    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        // Dismisses automatically
                    }
                }).show();
    }

    // Delete button on top right is clicked
    public void onClickDelete(View view) {
        LayoutInflater inflater = (this).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_delete_project, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Project?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this project? Enter your password below to confirm.") 
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password and delete project
                        EditText passwordET = (EditText) alertView.findViewById(R.id.alert_dialogue_delete_password_text_field);
                        String password = passwordET.getText().toString().trim();

                        // Cannot send null password
                        if(password == null){
                            showMessage("Password incorrect, could not delete project.");
                        }
                        else {
                            // Cannot send empty string
                            if(password.length() == 0){
                                showMessage("Password incorrect, could not delete project.");
                            }
                            else {
                                // Password is of valid type, send it
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
                case 0:
                    Bundle data = new Bundle();
                    data.putString("projectId", pid);
                    ProjectOverviewFragment projectOverviewFragment = new ProjectOverviewFragment();
                    projectOverviewFragment.setArguments(data);
                    return projectOverviewFragment;

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
            // Show 2 total pages.
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(IndividualProjectActivity.this, ProjectTabsActivity.class);
        startActivity(intent);
        finish();
    }
}
