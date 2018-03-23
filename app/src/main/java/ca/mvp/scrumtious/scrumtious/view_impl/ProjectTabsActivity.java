package ca.mvp.scrumtious.scrumtious.view_impl;

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
    private ViewPager projectTabsViewPager;
    private ImageButton projectTabsLogoutImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_tabs);

        projectTabsLogoutImageButton = findViewById(R.id.projectTabsLogoutImageButton);
        projectTabsLogoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(ProjectTabsActivity.this);
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        projectTabsViewPager = findViewById(R.id.projectTabsViewPager);
        projectTabsViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout projectTabsTabLayout = findViewById(R.id.projectTabsTabLayout);
        projectTabsTabLayout.setupWithViewPager(projectTabsViewPager);

    }

    // Should ask to logout user
    @Override
    public void onBackPressed() {
        AuthenticationHelper.logout(ProjectTabsActivity.this);
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
                // Project List Tab
                case 0:
                    ProjectListFragment projectListFragment = new ProjectListFragment();
                    return projectListFragment;

                    // Invitations Tab
                case 1:
                    InvitationsFragment invitationsFragment = new InvitationsFragment();
                    return invitationsFragment;

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
                    return "MY PROJECTS";

                case 1:
                    return "INVITATIONS";

                default:
                    return null;
            }
        }
    }
}
