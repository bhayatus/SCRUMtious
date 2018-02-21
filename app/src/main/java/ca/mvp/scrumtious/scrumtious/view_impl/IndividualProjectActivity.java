package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualProjectViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.IndividualProjectPresenter;

public class IndividualProjectActivity extends AppCompatActivity implements IndividualProjectViewInt {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageButton deleteBtn;
    private String pid;

    private IndividualProjectPresenterInt individualProjectPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_project);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");

        individualProjectPresenter = new IndividualProjectPresenter(this, pid);
        individualProjectPresenter.setupProjectDeletedListener();
        individualProjectPresenter.checkIfOwner();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.hideOverflowMenu();

        deleteBtn = findViewById(R.id.delete_project_img_btn);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.projectOverviewTabs);
        tabs.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_individual_project_screen, menu);
        return true;
    }

    // Project no longer exists, go back
    public void onSuccessfulDeletion() {
        // Return to project list screen and make sure we can't go back by clearing the task stack
        Intent intent = new Intent(IndividualProjectActivity.this, ProjectTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void setDeleteInvisible(){
        deleteBtn.setVisibility(View.GONE);
    }

    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
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

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(IndividualProjectActivity.this, ProjectTabsActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
