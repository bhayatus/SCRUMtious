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

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Query mQuery;

    private TextView pbView;
    private BacklogFragment backlogView;

    private RecyclerView sendToList;
    private FirebaseRecyclerAdapter<Sprint, SendToSprintViewHolder> sendToListAdapter;

    private String pid, usid;
    public SendToFragment() {
        // Required empty public constructor
    }

    // Constructs a new instance of this dialog, passing in important information
    public static SendToFragment newInstance(String projectId, String userStoryId){
        SendToFragment sendToFragment = new SendToFragment();
        Bundle bundle = new Bundle();
        bundle.putString("projectId", projectId);
        bundle.putString("userStoryId", userStoryId);
        sendToFragment.setArguments(bundle);
        return sendToFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pid = getArguments().getString("projectId");
        usid = getArguments().getString("userStoryId");
    }

    // Necessary to call this after newInstance, sets up the backlog view
    public void setBacklogView(BacklogFragment backlogView){
        this.backlogView = backlogView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send_to, container, false);

        sendToList = (RecyclerView) view.findViewById(R.id.sendToRecyclerView);
        pbView = view.findViewById(R.id.sendToPB);

        pbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assigning user story back to product backlog
                assignUserStory("null", "");
            }
        });
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView(){

        sendToList.setLayoutManager(new LinearLayoutManager(backlogView.getContext()));

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mQuery = mRef.child("projects").child(pid).child("sprints").orderByChild("completed");

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
                    sendToList.setVisibility(View.GONE);
                }
                else{
                    sendToList.setVisibility(View.VISIBLE);
                }

            }
        };

        sendToList.setAdapter(sendToListAdapter);
    }


    // Takes in sprint id and name of sprint
    // If assigning to the product backlog, sid is "null", and name is ""
    private void assignUserStory(final String sid, final String name){

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        // Grab the completed status of the user story
        mRef.child("projects").child(pid).child("user_stories").child(usid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String completed = dataSnapshot.child("completed").getValue().toString();
                    String assignedTo_completed = sid + "_" + completed;
                    Map sendToMap = new HashMap();

                    sendToMap.put("/projects/" + pid + "/user_stories/" + usid + "/" + "assignedTo", sid);
                    sendToMap.put("/projects/" + pid + "/user_stories/" + usid + "/" + "assignedToName", name);
                    sendToMap.put("/projects/" + pid + "/user_stories/" + usid + "/" + "assignedTo_completed", assignedTo_completed);

                    mDatabase = FirebaseDatabase.getInstance();
                    mRef = mDatabase.getReference();
                    mRef.updateChildren(sendToMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()){
                                backlogView.showMessage("Successfully assigned user story.");
                            }

                            else{
                                backlogView.showMessage("An error occurred, failed to move the user story.");
                            }
                        }
                    });



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                backlogView.showMessage(databaseError.getMessage());
            }
        });
    }

    public static class SendToSprintViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView nameView;

        public SendToSprintViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            nameView = (TextView) mView.findViewById(R.id.sendToRowName);
        }


        // Populates each row of the recycler view with the sprint name
        public void setDetails(String name){
            nameView.setText(name);
        }


    }

}
