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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.IndividualProjectScreenPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.IndividualProjectScreenViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.IndividualProjectScreenPresenter;

public class IndividualProjectScreenActivity extends AppCompatActivity implements IndividualProjectScreenViewInt{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageButton deleteBtn;

    private IndividualProjectScreenPresenterInt individualProjectScreenPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Project");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_project_screen);

        Bundle data = getIntent().getExtras();
        final String pid = data.getString("projectId");

        individualProjectScreenPresenter = new IndividualProjectScreenPresenter(this, pid);
        individualProjectScreenPresenter.setupProjectDeleteListener();
        individualProjectScreenPresenter.checkIfOwner();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.hideOverflowMenu();

        deleteBtn = findViewById(R.id.delete_project_img_btn);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_individual_project_screen, menu);
        return true;
    }

    public void onSuccessfulDeletion() {
        Intent intent = new Intent(this, ProjectTabsScreenActivity.class);
        startActivity(intent);
        finish();
    }

    public void setDeleteInvisible(){
        deleteBtn.setVisibility(View.GONE);
    }


    public void onClickDelete(View view) {
        LayoutInflater inflater = (this).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_delete_project, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this project?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password and delete project
                        EditText passwordET = (EditText) alertView.findViewById(R.id.alert_dialogue_delete_password_text_field);
                        String password = passwordET.getText().toString().trim();
                        individualProjectScreenPresenter.validatePassword(password);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .create().show();

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new ProjectOverviewFragment();

                case 1:
                    return new ProjectMembersFragment();

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
}
