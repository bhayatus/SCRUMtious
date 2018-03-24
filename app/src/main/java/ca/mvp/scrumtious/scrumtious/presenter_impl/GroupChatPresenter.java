package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.HashMap;
import java.util.Map;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.GroupChatPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.GroupChatViewInt;
import ca.mvp.scrumtious.scrumtious.model.Message;
import ca.mvp.scrumtious.scrumtious.view_impl.GroupChatActivity;


public class GroupChatPresenter extends AppCompatActivity implements GroupChatPresenterInt {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;
    private FirebaseAuth firebaseAuth;
    private Query databaseQuery;

    private GroupChatViewInt groupChatView;

    private final String PROJECT_ID;

    public GroupChatPresenter(GroupChatViewInt groupChatView, String pid) {
        this.groupChatView = groupChatView;
        this.PROJECT_ID = pid;
    }

    public void addMessagesToDatabase(final String messageText) {
        firebaseAuth = FirebaseAuth.getInstance();
        String senderEmail = firebaseAuth.getCurrentUser().getEmail();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference().child("projects").child(PROJECT_ID).child("messages");

        final String messageId = firebaseRootReference.push().getKey();

        // Get the current date long timeStamp = System.currentTimeMillis();
        long timeStamp = System.currentTimeMillis();

        Map messagesMap = new HashMap<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        //All changes made in one go, ensure atomicity
        messagesMap.put("/projects/" + PROJECT_ID + "/messages/" + messageId + "/messageText", messageText);
        messagesMap.put("/projects/" + PROJECT_ID + "/messages/" + messageId + "/senderEmail", senderEmail);
        messagesMap.put("/projects/" + PROJECT_ID + "/messages/" + messageId + "/timeStamp", timeStamp);

        firebaseRootReference.updateChildren(messagesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    groupChatView.onSuccessfulSent();
                } else {
                    groupChatView.showMessage("An error occurred, failed to send the message.", false);
                }
            }
        });
    }


    public FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> setupMessageAdapter() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseRootReference = FirebaseDatabase.getInstance().getReference();

        databaseQuery = firebaseRootReference.child("projects").child(PROJECT_ID).child("messages").orderByChild("timeStamp");

        FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> messageListAdapter
                = new FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder>(
                        Message.class,
                        R.layout.group_chat_message_row,
                        GroupChatActivity.MessagesViewHolder.class,
                databaseQuery
        ){

            @Override
            protected void populateViewHolder(final GroupChatActivity.MessagesViewHolder viewHolder, Message model, int position) {
                final GroupChatActivity.MessagesViewHolder mViewHolder = viewHolder;

                long timeStamp = model.getTimeStamp();
                String messageContent = model.getMessageText();
                String sender = model.getSenderEmail();

                firebaseAuth = FirebaseAuth.getInstance();
                String userEmail = firebaseAuth.getCurrentUser().getEmail();

                viewHolder.setDetails(messageContent, timeStamp, sender);

                // Current message was from current user
                if(sender.equals(userEmail)){
                    mViewHolder.showRightSide();
                    // Set click listener for right side message
                    viewHolder.getGroupChatMessageRowContentRightTextView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Details are shown currently, hide them
                            if (mViewHolder.getGroupChatMessageRowTimestampRightTextView().isShown()){
                                mViewHolder.hideRightDetails();
                            }
                            // Details are not shown currently, show them
                            else{
                                mViewHolder.showRightDetails();
                            }
                        }
                    });

                    // Remove left side listener
                    viewHolder.getGroupChatMessageRowContentLeftTextView().setOnClickListener(null);
                }
                // Current message was not from current user
                else{
                    mViewHolder.showLeftSide();
                    // Set click listener for left side message
                    viewHolder.getGroupChatMessageRowContentLeftTextView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Details are shown currently, hide them
                            if (mViewHolder.getGroupChatMessageRowTimestampLeftTextView().isShown()){
                                mViewHolder.hideLeftDetails();
                            }
                            // Details are not shown currently, show them
                            else{
                                mViewHolder.showLeftDetails();
                            }
                        }
                    });
                    // Remove right side listener
                    viewHolder.getGroupChatMessageRowContentRightTextView().setOnClickListener(null);

                }

                viewHolder.setDetails(messageContent, timeStamp, sender);

            }

            @Override
            public void onDataChanged() {
                // If changes made to view, scroll to bottom
                groupChatView.scrollToBottom();

            }
        };



        return messageListAdapter;
    }
}

