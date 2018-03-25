package ca.mvp.scrumtious.scrumtious.view_impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.model.Sprint;

public class SendToFragment extends DialogFragment {

    private String pid, usid;
    private FirebaseRecyclerAdapter<Sprint, SendToSprintViewHolder> sendToListAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query mQuery;

    private TextView sendToFragmentSendToProductBacklogTextView;
    private BacklogFragment backlogView;
    private RecyclerView sendToFragmentRecyclerView;

    public SendToFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pid = getArguments().getString("projectId");
        usid = getArguments().getString("userStoryId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send_to, container, false);

        sendToFragmentRecyclerView = view.findViewById(R.id.sendToFragmentRecyclerView);
        sendToFragmentSendToProductBacklogTextView = view.findViewById(R.id.sendToFragmentSendToProductBacklogTextView);

        sendToFragmentSendToProductBacklogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assigning user story back to product backlog
                assignUserStory("null", "");
            }
        });
        setupRecyclerView();

        return view;
    }

    // Constructs a new instance of this dialog, passing in important information needed
    public static SendToFragment newInstance(String projectId, String userStoryId){
        SendToFragment sendToFragment = new SendToFragment();
        Bundle bundle = new Bundle();
        bundle.putString("projectId", projectId);
        bundle.putString("userStoryId", userStoryId);
        sendToFragment.setArguments(bundle);
        return sendToFragment;
    }


    // Necessary to call this after newInstance, sets up the backlog view
    public void setBacklogView(BacklogFragment backlogView){
        this.backlogView = backlogView;
    }


    private void setupRecyclerView(){

        sendToFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(backlogView.getContext()));

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mQuery = databaseReference.child("projects").child(pid).child("sprints").orderByChild("completed");

        // Generates the adapter to grab the list of sprints we can assign the user story to
        sendToListAdapter = new FirebaseRecyclerAdapter<Sprint, SendToSprintViewHolder>(
                Sprint.class,
                R.layout.sendto_row,
                SendToSprintViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(SendToSprintViewHolder viewHolder, Sprint model, int position) {
                final String sid = getRef(position).getKey();
                final String name = model.getSprintName();

                viewHolder.setDetails(model.getSprintName());

                // If sprint is selected, assign the user story to it
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assignUserStory(sid, name);
                    }
                });
            }
            @Override
            public void onDataChanged() {

                // Don't bother showing recycler view if no items
                if (getItemCount() == 0){
                    sendToFragmentRecyclerView.setVisibility(View.GONE);
                }
                // Items to show, display recycler view
                else{
                    sendToFragmentRecyclerView.setVisibility(View.VISIBLE);
                }

            }
        };

        sendToFragmentRecyclerView.setAdapter(sendToListAdapter);
    }


    // Takes in sprint id and name of sprint
    // If assigning to the product backlog, sid is "null", and name is ""
    private void assignUserStory(final String sid, final String name){

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Grab the completed status of the user story
        databaseReference.child("projects").child(pid).child("user_stories").child(usid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String completed = dataSnapshot.child("completed").getValue().toString();
                    String assignedTo_completed = sid + "_" + completed;
                    Map sendToMap = new HashMap();

                    sendToMap.put("/projects/" + pid + "/user_stories/" + usid + "/" + "assignedTo", sid);
                    sendToMap.put("/projects/" + pid + "/user_stories/" + usid + "/" + "assignedToName", name);
                    sendToMap.put("/projects/" + pid + "/user_stories/" + usid + "/" + "assignedTo_completed", assignedTo_completed);

                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference();
                    databaseReference.updateChildren(sendToMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()){
                                backlogView.showMessage("Successfully assigned the user story.", false);
                            }

                            else{
                                backlogView.showMessage("An error occurred, failed to assign the user story.", false);
                            }
                        }
                    });



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Viewholder class to display sprints that user can assign a user story to
    public static class SendToSprintViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView nameView;

        public SendToSprintViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            nameView = mView.findViewById(R.id.sendToRowNameTextView);
        }

        // Populates each row of the recycler view with the sprint name
        public void setDetails(String name){
            nameView.setText(name);
        }

    }

}
