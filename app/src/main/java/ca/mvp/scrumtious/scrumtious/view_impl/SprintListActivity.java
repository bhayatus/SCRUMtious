package ca.mvp.scrumtious.scrumtious.view_impl;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SprintListPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SprintListViewInt;
import ca.mvp.scrumtious.scrumtious.model.Sprint;
import ca.mvp.scrumtious.scrumtious.presenter_impl.SprintListPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;

public class SprintListActivity extends AppCompatActivity implements SprintListViewInt {

    private SprintListPresenterInt sprintListPresenter;
    private String pid;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private boolean alreadyDeleted;

    private ImageButton logoutBtn;

    private RecyclerView sprintList;
    private FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> sprintListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_list);

        alreadyDeleted = false; // Project is not deleted at this point

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        this.sprintListPresenter = new SprintListPresenter(this, pid);

        // In case project is deleted, the user has to be taken back to project list screen
        sprintListPresenter.setupProjectDeletedListener();

        logoutBtn = findViewById(R.id.sprintListLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(SprintListActivity.this);
            }
        });


        // The following sets up the navigation drawer
        mDrawerLayout = findViewById(R.id.sprintListNavDrawer);
        navigationView = findViewById(R.id.sprintListNavView);

        // By default, should highlight product backlog option to indicate that is where the user is
        navigationView.setCheckedItem(R.id.nav_sprints);
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
                                        Intent intent = new Intent(SprintListActivity.this, IndividualProjectActivity.class);
                                        intent.putExtra("projectId", pid);
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
                                        Intent intent = new Intent(SprintListActivity.this, ProductBacklogActivity.class);
                                        intent.putExtra("projectId", pid);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses to view sprints, do nothing as we are already there
                            case R.id.nav_sprints:
                               break;
                        }

                        return true;
                    }
                });

        sprintList = (RecyclerView) findViewById(R.id.sprintListRecyclerView);

        setupRecyclerView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.sprintListToolbar);
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

    public void onClickAddSprint(View view){
        Intent intent = new Intent(SprintListActivity.this, CreateSprintActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
    }

    private void setupRecyclerView(){

        sprintList.setLayoutManager(new LinearLayoutManager(this));

        sprintListAdapter = sprintListPresenter.setupSprintListAdapter(sprintList);

        sprintList.setAdapter(sprintListAdapter);
    }

    @Override
    public void goToSprintScreen(String sid) {
        Intent intent = new Intent(SprintListActivity.this, IndividualSprintActivity.class);
        intent.putExtra("projectId", pid);
        intent.putExtra("sprintId", sid);
        startActivity(intent);
    }

    // If project no longer exists while we are on this screen, must return to the project list screen
    @Override
    public void onProjectDeleted() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!alreadyDeleted) {
            alreadyDeleted = true;

            // Return to project list screen, and clear the task stack so we can't go back
            Intent intent = new Intent(SprintListActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SprintListActivity.this, ProjectTabsActivity.class);
        startActivity(intent);
        finish();
    }

    public static class SprintsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView nameView, descriptionView, startToEndDateView;

        public SprintsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            nameView = (TextView) mView.findViewById(R.id.sprintRowName);
            descriptionView = (TextView) mView.findViewById(R.id.sprintRowDescription);
            startToEndDateView = (TextView) mView.findViewById(R.id.sprintRowStartToEnd);
        }


        // Populates each row of the recycler view with the sprint details
        public void setDetails(String name, String description, String startToEndDate){
            // Shorten description for viewing purposes
            String displayDesc = "";
            if (!description.contains("\n")){
                displayDesc = description;
            }
            else {
                String[] parts = description.split("\n");
                // Only one line break
                if (parts.length == 2) {
                    displayDesc = parts[0] + "\n" + parts[1];
                }
                // At least three lines
                else{

                    int numberOfNewLines = 0;
                    int index = 0;
                    int length = description.length();
                    while(numberOfNewLines <= 2 && index < length){
                        if (description.charAt(index) == '\n'){
                            numberOfNewLines++;
                            displayDesc += "\n";
                        }
                        else{
                            displayDesc += description.charAt(index);
                        }

                        index++;
                    }
                    displayDesc += "...";
                }

            }

            nameView.setText(name);
            descriptionView.setText(displayDesc);
            startToEndDateView.setText(startToEndDate);
        }


    }
}
