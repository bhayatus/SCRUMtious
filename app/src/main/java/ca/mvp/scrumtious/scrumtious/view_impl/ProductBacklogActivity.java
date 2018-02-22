package ca.mvp.scrumtious.scrumtious.view_impl;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProductBacklogPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProductBacklogViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProductBacklogPresenter;

public class ProductBacklogActivity extends AppCompatActivity implements ProductBacklogViewInt {

    private ProductBacklogPresenterInt productBacklogPresenter;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String pid;
    private ViewPager mViewPager;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private boolean alreadyDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_backlog);

        alreadyDeleted = false; // Project is not deleted at this point

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        this.productBacklogPresenter = new ProductBacklogPresenter(this, pid);

        // In case project is deleted, the user has to be taken back to project list screen
        productBacklogPresenter.setupProjectDeletedListener();

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
                            // User chooses Project Overview in menu, go there
                            case R.id.nav_overview:
                                // Allow nav drawer to close smoothly before switching activities
                                Handler handler = new Handler();
                                int delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ProductBacklogActivity.this, IndividualProjectActivity.class);
                                        intent.putExtra("projectId", pid);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                            // User chooses product backlog, do nothing as we are already there
                            case R.id.nav_product_backlog:
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

    public void onClickAddUserStory(View view){
        Intent intent = new Intent(ProductBacklogActivity.this, CreateUserStoryActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
    }

    // If project no longer exists while we are on this screen, must return to the project list screen
    @Override
    public void onProjectDeleted() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!alreadyDeleted) {
            alreadyDeleted = true;

            // Return to project list screen, and clear the task stack so we can't go back
            Intent intent = new Intent(ProductBacklogActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
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
                    PBInProgressFragment pbInProgressFragment = new PBInProgressFragment();
                    pbInProgressFragment.setArguments(data);
                    return pbInProgressFragment;
                case 1:
                    data = new Bundle();
                    data.putString("projectId", pid);
                    PBCompletedFragment pbCompletedFragment = new PBCompletedFragment();
                    pbCompletedFragment.setArguments(data);
                    return pbCompletedFragment;
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
                    return "IN PROGRESS";

                case 1:
                    return "COMPLETED";

                default:
                    return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductBacklogActivity.this, ProjectTabsActivity.class);
        startActivity(intent);
        finish();
    }
}
