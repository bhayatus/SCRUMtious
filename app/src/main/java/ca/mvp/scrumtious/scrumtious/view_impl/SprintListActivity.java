package ca.mvp.scrumtious.scrumtious.view_impl;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SprintListPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SprintListViewInt;
import ca.mvp.scrumtious.scrumtious.model.Sprint;
import ca.mvp.scrumtious.scrumtious.presenter_impl.SprintListPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;
import ca.mvp.scrumtious.scrumtious.utils.StringHelper;

public class SprintListActivity extends AppCompatActivity implements SprintListViewInt {

    private SprintListPresenterInt sprintListPresenter;
    private String pid;
    private ValueEventListener projectListener;
    private FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> sprintListNameAdapter;
    private FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> sprintListStartDateAdapter;

    private DrawerLayout sprintListDrawerLayout;
    private NavigationView sprintListNavigationView;
    private ImageButton sprintListLogoutImageButton, sprintListSortImageButton;
    private LinearLayout sprintListActivityNoSprintsEmptyStateView;
    private RecyclerView sprintListRecyclerView;

    private boolean projectAlreadyDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_list);

        projectAlreadyDeleted = false; // Project is not deleted at this point

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        sprintListPresenter = new SprintListPresenter(this, pid);

        sprintListLogoutImageButton = findViewById(R.id.sprintListLogoutImageButton);
        sprintListLogoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(SprintListActivity.this);
            }
        });

        sprintListSortImageButton = (findViewById(R.id.sprintListSortImageButton));
        sprintListSortImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open up menu with two choices to sort by either date or name
                PopupMenu popup = new PopupMenu(SprintListActivity.this, sprintListSortImageButton);
                MenuInflater inflate = popup.getMenuInflater();
                inflate.inflate(R.menu.sprint_sort_view, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            // User wants to sort sprints by name
                            case R.id.sprint_sort_name:
                                sprintListRecyclerView.setAdapter(sprintListNameAdapter);
                                return true;

                            // User wants to sort sprints by start date
                            case R.id.sprint_sort_start_date:
                                sprintListRecyclerView.setAdapter(sprintListStartDateAdapter);
                                return true;
                        }

                        return false;
                    }
                });

                popup.show();

            }
        });

        sprintListActivityNoSprintsEmptyStateView = findViewById(R.id.sprintListActivityNoSprintsEmptyStateView);

        // The following sets up the navigation drawer
        sprintListDrawerLayout = findViewById(R.id.sprintListDrawerLayout);
        sprintListNavigationView = findViewById(R.id.sprintListNavigationView);

        // By default, should highlight sprint list option to indicate that is where the user is
        sprintListNavigationView.setCheckedItem(R.id.nav_sprints);
        sprintListNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        sprintListDrawerLayout.closeDrawers();
                        int item = menuItem.getItemId();
                        switch(item){

                            // User chooses project overview in menu, go there
                            case R.id.nav_overview:
                                // Allow nav drawer to close smoothly before switching activities
                                Handler handler = new Handler();
                                int delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SprintListActivity.this, IndividualProjectActivity.class);
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
                                        Intent intent = new Intent(SprintListActivity.this, ProductBacklogActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses to view sprints, do nothing as we are already there
                            case R.id.nav_sprints:
                               break;

                            // User chooses to go to chat room
                            case R.id.nav_chat:
                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SprintListActivity.this, GroupChatActivity.class);
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
                                        Intent intent = new Intent(SprintListActivity.this, ProjectStatsActivity.class);
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

        Toolbar sprintListToolbar = findViewById(R.id.sprintListToolbar);
        setSupportActionBar(sprintListToolbar);
        // Sets icon for menu on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sprintListRecyclerView = findViewById(R.id.sprintListRecyclerView);

        setupRecyclerView();


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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // User clicks on the menu icon on the top left
            case android.R.id.home:
                sprintListDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SprintListActivity.this, IndividualProjectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("projectId", pid);
        startActivity(intent);
        finish();
    }

    // Project was deleted by another user, or user was removed from the project
    @Override
    public void onProjectDeleted() {

        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;

            // Return to project list screen, and clear the task stack so we can't go back
            Intent intent = new Intent(SprintListActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onSprintDeleted() {
        // Needs to be here even if not implemented
    }

    @Override
    public void onUserStoryDeleted() {
        // Needs to be here even if not implemented
    }

    private void setupRecyclerView(){

        sprintListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sets up the two adapters, which sort by name and start date respectively
        sprintListNameAdapter = sprintListPresenter.setupSprintListAdapter("sprintName");
        sprintListStartDateAdapter = sprintListPresenter.setupSprintListAdapter("sprintStartDate");

        sprintListRecyclerView.setAdapter(sprintListStartDateAdapter);
    }

    // If no sprints to display, should show empty view
    @Override
    public void setEmptyStateView(){
        if (sprintListNameAdapter.getItemCount() == 0){
            sprintListActivityNoSprintsEmptyStateView.setVisibility(View.VISIBLE);
            sprintListRecyclerView.setVisibility(View.GONE);
            sprintListSortImageButton.setVisibility(View.GONE);
        }
        else{
            sprintListActivityNoSprintsEmptyStateView.setVisibility(View.GONE);
            sprintListRecyclerView.setVisibility(View.VISIBLE);
            sprintListSortImageButton.setVisibility(View.VISIBLE);
        }
    }

    // User clicks on add sprint button, take them to the screen
    public void onClickAddSprint(View view){
        Intent intent = new Intent(SprintListActivity.this, CreateSprintActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
    }

    // User clicks on a specific sprint
    @Override
    public void goToSprintScreen(String sid) {
        Intent intent = new Intent(SprintListActivity.this, IndividualSprintActivity.class);
        intent.putExtra("projectId", pid);
        intent.putExtra("sprintId", sid);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message, boolean showAsToast) {

        // Show message in toast so it persists across activity transitions
        if (showAsToast){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        else {
            // Call the utils class method to handle making the snackbar
            SnackbarHelper.showSnackbar(this, message);
        }

    }

    // Viewholder class to display list of sprints
    public static class SprintsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView sprintRowSprintNameTextView, sprintRowSprintDescTextView, sprintRowStartToEnd, sprintRowCurrentSprint;

        ImageButton sprintRowMoreIcon;

        // Don't show whole description by default
        boolean showFull = false;

        public SprintsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            sprintRowSprintNameTextView = mView.findViewById(R.id.sprintRowSprintNameTextView);
            sprintRowSprintDescTextView = mView.findViewById(R.id.sprintRowSprintDescTextView);
            sprintRowStartToEnd = mView.findViewById(R.id.sprintRowStartToEnd);
            sprintRowCurrentSprint = mView.findViewById(R.id.sprintRowCurrentSprint);

            sprintRowMoreIcon = mView.findViewById(R.id.sprintRowMoreIcon);

        }

        // Display message indicating that the current date falls within a sprint
        public void setCurrentSprintViewVisible(){
            sprintRowCurrentSprint.setVisibility(View.VISIBLE);
        }


        // Populates each row of the recycler view with the sprint details
        public void setDetails(String name, String description, String startToEndDate){

            // Show whole description by default
            String displayDesc = description;


            // Shorten the description
            if (!showFull){
                displayDesc = StringHelper.shortenDescription(description);
            }

            // Description is showing entirely, hide show more icon
            if (displayDesc.trim().equals(description)){
                showOrHideMoreIcon(true);
            }
            // Description has been shortened, don't show more icon
            else{
                showOrHideMoreIcon(false);
            }

            sprintRowSprintNameTextView.setText(name);
            sprintRowSprintDescTextView.setText(displayDesc);
            sprintRowStartToEnd.setText(startToEndDate);
        }

        // Either show or hide the more icon
        public void showOrHideMoreIcon(boolean hide){
            if (hide){
                sprintRowMoreIcon.setVisibility(View.GONE);
            }
            else{
                sprintRowMoreIcon.setVisibility(View.VISIBLE);
            }
        }

        public ImageButton getSprintRowMoreIcon(){
            return sprintRowMoreIcon;
        }

        // User clicked on the show more icon, switch boolean state and reset description
        public void switchShowFull(String description){
            showFull = !showFull;

            // Show whole description by default
            String displayDesc = description;

            // Shorten the description
            if (!showFull){
                displayDesc = StringHelper.shortenDescription(description);
            }

            // Description is showing entirely, hide show more icon
            if (displayDesc.trim().equals(description)){
                showOrHideMoreIcon(true);
            }
            // Description has been shortened, don't show more icon
            else{
                showOrHideMoreIcon(false);
            }

            // Reset the description
            sprintRowSprintDescTextView.setText(displayDesc);
        }


    }
}
