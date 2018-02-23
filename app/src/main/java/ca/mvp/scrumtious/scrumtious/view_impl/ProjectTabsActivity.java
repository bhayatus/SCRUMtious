package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;

public class ProjectTabsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ImageButton logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_tabs);
        logoutBtn = findViewById(R.id.projectTabsLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(ProjectTabsActivity.this);
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.projectTabsViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout mainTabs = (TabLayout) findViewById(R.id.projectTabsMainTabs);
        mainTabs.setupWithViewPager(mViewPager);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch(position){
                case 0:
                    ProjectListFragment projectListFragment = new ProjectListFragment();
                    return projectListFragment;

                case 1:
                    InvitationsFragment invitationsFragment = new InvitationsFragment();
                    return invitationsFragment;

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
                    return "MY PROJECTS";

                case 1:
                    return "INVITATIONS";

                default:
                    return null;
            }
        }
    }

    // Should ask to log user out
    @Override
    public void onBackPressed() {
        AuthenticationHelper.logout(ProjectTabsActivity.this);
    }

}
