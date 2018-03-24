package ca.mvp.scrumtious.scrumtious.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ListenerInt;

// Class to help with setting up and removing deletion listeners
// For example, a screen sprint details needs to know if the project or the sprint no longer exists,
// and go back as necessary. This class handles such things
public class ListenerHelper {

    public static ValueEventListener setupProjectDeletedListener(final ListenerInt context, String pid){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("projects");
        ValueEventListener projectListener = databaseReference.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If project no longer exists, exit this screen and go back
                if (!dataSnapshot.exists()){
                    context.onProjectDeleted();
                }

                else{
                    // Check if I'm no longer a member through my uid
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    if(!dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())){
                        context.onProjectDeleted();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return projectListener;
    }

    public static void removeProjectDeletedListener(ValueEventListener listener, String pid){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("projects").child(pid);
        databaseReference.removeEventListener(listener);
    }

    public static ValueEventListener setupSprintDeletedListener(final ListenerInt context, String pid, String sid){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("projects").child(pid).child("sprints");
        ValueEventListener sprintListener = databaseReference.child(sid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If sprint no longer exists, exit this screen and go back
                if (!dataSnapshot.exists()){
                    context.onSprintDeleted();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return sprintListener;
    }

    public static void removeSprintDeletedListener(ValueEventListener listener, String pid, String sid){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("projects").child(pid).child("sprints").child(sid);
        databaseReference.removeEventListener(listener);
    }

    public static ValueEventListener setupUserStoryDeletedListener(final ListenerInt context, String pid, String usid){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("projects").child(pid).child("user_stories").child(usid);
                ValueEventListener userStoryListener = databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // If user story no longer exists, exit the screen and go back
                        if (!dataSnapshot.exists()){
                            context.onUserStoryDeleted();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return userStoryListener;
    }

    public static void removeUserStoryDeletedListener(ValueEventListener listener, String pid, String usid){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("projects").child(pid).child("user_stories").child(usid);
        databaseReference.removeEventListener(listener);
    }
}
