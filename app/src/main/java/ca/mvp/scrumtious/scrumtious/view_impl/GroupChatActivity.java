package ca.mvp.scrumtious.scrumtious.view_impl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.GroupChatPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.GroupChatViewInt;
import ca.mvp.scrumtious.scrumtious.model.Message;
import ca.mvp.scrumtious.scrumtious.presenter_impl.GroupChatPresenter;
import ca.mvp.scrumtious.scrumtious.utils.AuthenticationHelper;
import ca.mvp.scrumtious.scrumtious.utils.ListenerHelper;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class GroupChatActivity extends AppCompatActivity implements GroupChatViewInt {

    private GroupChatPresenterInt groupChatPresenter;
    private FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> groupChatAdapter;
    private String pid;
    private ValueEventListener projectListener;

    private boolean projectAlreadyDeleted;

    private RecyclerView groupChatRecyclerView;
    private DrawerLayout groupChatDrawerLayout;
    private NavigationView groupChatNavigationView;
    private ImageButton groupChatSendImageButton, groupChatLogoutImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        groupChatPresenter = new GroupChatPresenter(this, pid);

        this.projectAlreadyDeleted = false;

        groupChatLogoutImageButton = findViewById(R.id.groupChatLogoutImageButton);
        groupChatLogoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationHelper.logout(GroupChatActivity.this);
            }
        });

        // The following sets up the navigation drawer
        groupChatDrawerLayout = findViewById(R.id.groupChatDrawerLayout);
        groupChatNavigationView = findViewById(R.id.groupChatNavigationView);

        // By default, should highlight chat option to indicate that is where the user is
        groupChatNavigationView.setCheckedItem(R.id.nav_chat);
        groupChatNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        groupChatDrawerLayout.closeDrawers();
                        int item = menuItem.getItemId();
                        switch(item){
                            // User chooses project overview in menu, do nothing as we are already there
                            case R.id.nav_overview:
                                // Allow nav drawer to close smoothly before switching activities
                                Handler handler = new Handler();
                                int delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(GroupChatActivity.this, IndividualProjectActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;
                            // User chooses product backlog, go to it
                            case R.id.nav_product_backlog:
                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(GroupChatActivity.this, ProductBacklogActivity.class);
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
                                        Intent intent = new Intent(GroupChatActivity.this, SprintListActivity.class);
                                        intent.putExtra("projectId", pid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },delayMilliseconds);

                                break;

                            // User chooses to go to chat room, do nothing as already there
                            case R.id.nav_chat:

                                break;

                            // User chooses to go to project stats
                            case R.id.nav_stats:
                                // Allow nav drawer to close smoothly before switching activities
                                handler = new Handler();
                                delayMilliseconds = 250;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(GroupChatActivity.this, ProjectStatsActivity.class);
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


        Toolbar groupChatToolbar = findViewById(R.id.groupChatToolbar);
        setSupportActionBar(groupChatToolbar);
        // Sets icon for menu on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupChatRecyclerView = findViewById(R.id.groupChatRecyclerView);
        setupRecyclerView();
        groupChatSendImageButton = findViewById(R.id.groupChatSendImageButton);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // User clicks on the menu icon on the top left
            case android.R.id.home:
                groupChatDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Setup listeners
    @Override
    protected void onResume() {
        projectListener = ListenerHelper.setupProjectDeletedListener(this, pid);
        scrollToBottom();
        super.onResume();
    }

    // Remove listeners
    @Override
    protected void onPause() {
        ListenerHelper.removeProjectDeletedListener(projectListener, pid);
        super.onPause();
    }


    //TODO
    @Override
    public void onProjectDeleted() {
        // DELETED NORMALLY FLAG PREVENTS THIS FROM TRIGGERING AGAIN AFTER ALREADY BEING DELETED
        if (!projectAlreadyDeleted) {
            projectAlreadyDeleted = true;
            // Return to project list screen and make sure we can't go back by clearing the task stack
            Intent intent = new Intent(GroupChatActivity.this, ProjectTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSprintDeleted() {

    }

    @Override
    public void onUserStoryDeleted() {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GroupChatActivity.this, IndividualProjectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("projectId", pid);
        startActivity(intent);
        finish();
    }

    private void setupRecyclerView(){

        groupChatRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        groupChatAdapter = groupChatPresenter.setupMessageAdapter();
        groupChatRecyclerView.setAdapter(groupChatAdapter);
    }

    public void onClickSendMessage(View view){

        EditText groupChatMessageInputEditText = findViewById(R.id.groupChatMessageInputEditText);
        String messageInput = groupChatMessageInputEditText.getText().toString();
        if (isValidMessage(messageInput)){
            groupChatPresenter.addMessagesToDatabase(messageInput);
        }
        else{
            //cannot send an empty message
        }

    }

    //Check if message is empty
    public boolean isValidMessage(String message){
        return (!message.trim().isEmpty());
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder{

        private static final int MESSAGE_FADE_IN_DURATION_SEC = 100;
        View mView;

        TextView groupChatMessageRowContentRightTextView, groupChatMessageRowContentLeftTextView, groupChatMessageRowTimestampRightTextView, groupChatMessageRowTimestampLeftTextView, groupChatMessageRowSentByLeftTextView;
        LinearLayout groupChatMessageRowContainerLeft;
        RelativeLayout groupChatMessageRowContainerRight;
        LinearLayout groupChatMessageRowLeftDetailsContent;
        AutoTransition autoTransition;

        public MessagesViewHolder(View itemView){

            super(itemView);
            this.mView = itemView;

            groupChatMessageRowContentRightTextView = mView.findViewById(R.id.groupChatMessageRowContentRightTextView);
            groupChatMessageRowContentLeftTextView = mView.findViewById(R.id.groupChatMessageRowContentLeftTextView);
            groupChatMessageRowTimestampRightTextView = mView.findViewById(R.id.groupChatMessageRowTimestampRightTextView);
            groupChatMessageRowTimestampLeftTextView = mView.findViewById(R.id.groupChatMessageRowTimestampLeftTextView);
            groupChatMessageRowSentByLeftTextView = mView.findViewById(R.id.groupChatMessageRowSentByLeftTextView);
            groupChatMessageRowContainerLeft = mView.findViewById(R.id.groupChatMessageRowContainerLeft);
            groupChatMessageRowContainerRight = mView.findViewById(R.id.groupChatMessageRowContainerRight);
            groupChatMessageRowLeftDetailsContent = mView.findViewById(R.id.groupChatMessageRowLeftDetailsContent);

            autoTransition = new AutoTransition();
            autoTransition.setDuration(MESSAGE_FADE_IN_DURATION_SEC);

            // Don't display details by default
            hideLeftDetails();
            hideRightDetails();

        }

        public void showLeftDetails(){
            TransitionManager.beginDelayedTransition(groupChatMessageRowContainerLeft, autoTransition);
            groupChatMessageRowLeftDetailsContent.setVisibility(View.VISIBLE);
        }


        public void hideLeftDetails(){
            TransitionManager.beginDelayedTransition(groupChatMessageRowContainerLeft, autoTransition);
            groupChatMessageRowLeftDetailsContent.setVisibility(View.INVISIBLE);
        }

        public void showRightDetails(){
            TransitionManager.beginDelayedTransition(groupChatMessageRowContainerRight, autoTransition);
            groupChatMessageRowTimestampRightTextView.setVisibility(View.VISIBLE);
        }

        public void hideRightDetails(){
            TransitionManager.beginDelayedTransition(groupChatMessageRowContainerRight, autoTransition);
            groupChatMessageRowTimestampRightTextView.setVisibility(View.INVISIBLE);
        }


        // Current user matches sender of current message
        public void showLeftSide(){
            groupChatMessageRowContainerLeft.setVisibility(View.VISIBLE);
            groupChatMessageRowContainerRight.setVisibility(View.GONE);
            groupChatMessageRowSentByLeftTextView.setVisibility(View.VISIBLE);
        }

        // Current user does not match current sender of message
        public void showRightSide(){
            groupChatMessageRowContainerLeft.setVisibility(View.GONE);
            groupChatMessageRowContainerRight.setVisibility(View.VISIBLE);
        }


        public void setDetails(String messageText, long timeStamp, String senderEmail){
            groupChatMessageRowContentLeftTextView.setText(messageText);
            final String dateFormatted = "Sent: " + DateFormat.format("MM/dd/yyyy", timeStamp).toString() +
                    " @ " + DateFormat.format("KK:mm a", timeStamp).toString();
            groupChatMessageRowTimestampLeftTextView.setText(dateFormatted);
            groupChatMessageRowSentByLeftTextView.setText(senderEmail);
            groupChatMessageRowContentRightTextView.setText(messageText);
            groupChatMessageRowTimestampRightTextView.setText(dateFormatted);

        }

        public TextView getGroupChatMessageRowContentRightTextView() {
            return groupChatMessageRowContentRightTextView;
        }

        public TextView getGroupChatMessageRowContentLeftTextView() {
            return groupChatMessageRowContentLeftTextView;
        }


        public TextView getGroupChatMessageRowTimestampRightTextView() {
            return groupChatMessageRowTimestampRightTextView;
        }

        public TextView getGroupChatMessageRowTimestampLeftTextView() {
            return groupChatMessageRowTimestampLeftTextView;
        }

    }

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

    @Override
    public void onSuccessfulSent() {
        EditText groupChatMessageInputEditText = findViewById(R.id.groupChatMessageInputEditText);
        groupChatMessageInputEditText.setText("");
    }

    @Override
    public void scrollToBottom() {
        groupChatRecyclerView.smoothScrollToPosition(groupChatAdapter.getItemCount());
    }


}
