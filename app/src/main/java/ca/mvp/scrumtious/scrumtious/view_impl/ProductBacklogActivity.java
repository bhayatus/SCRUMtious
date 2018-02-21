package ca.mvp.scrumtious.scrumtious.view_impl;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_backlog);
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

    }

    public void onClickAddUserStory(View view){
        Intent intent = new Intent(ProductBacklogActivity.this, CreateUserStoryActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
    }

    // If project no longer exists while we are on this screen, must return to the project list screen
    @Override
    public void onProjectDeleted() {
        // Return to project list screen, and clear the task stack so we can't go back
        Intent intent = new Intent(ProductBacklogActivity.this, ProjectTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
}
