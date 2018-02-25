package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateSprintPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateSprintViewInt;

public class CreateSprintPresenter extends AppCompatActivity implements CreateSprintPresenterInt {

    private CreateSprintViewInt createSprintView;
    private final String pid;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    private boolean dateConflictExists = false;

    public CreateSprintPresenter(CreateSprintViewInt createSprintView, String pid) {
        this.createSprintView = createSprintView;
        this.pid = pid;
    }

    // In case the project no longer exists or user was removed, user must be returned to project list screen
    @Override
    public void setupProjectDeletedListener(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects");
        mRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // If project no longer exists, exit this screen and go back
                if (!dataSnapshot.exists()){
                    createSprintView.onProjectDeleted();
                }

                else{
                    // Check if I'm no longer a member through my uid
                    mAuth = FirebaseAuth.getInstance();
                    if(!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        createSprintView.onProjectDeleted();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addSprintToDatabase(String sprintName, String sprintDesc, long sprintStartDate, long sprintEndDate) {
         mDatabase = FirebaseDatabase.getInstance();

         mRef = mDatabase.getReference()
                 .child("projects")
                 .child(this.pid);

         final String sprintId = mRef.push().getKey(); //generates unique key for sprint

        Map sprintMap = new HashMap<>();
        sprintMap.put("/sprints/" + sprintId + "/" + "sprintName", sprintName);
        sprintMap.put("/sprints/" + sprintId + "/" + "sprintDesc", sprintDesc);
        sprintMap.put("/sprints/" + sprintId + "/" + "sprintStartDate", sprintStartDate);
        sprintMap.put("/sprints/" + sprintId + "/" + "sprintEndDate", sprintEndDate);

        mRef.updateChildren(sprintMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    createSprintView.onSuccessfulCreateSprint();
                } else {
                    createSprintView.showMessage("An error occurred, failed to create sprint.");
                }
            }
        });

    }

    @Override
    public void onCheckConflictingSprintDates(final String sprintName, final String sprintDesc,
                                              final long sprintStartDate, final long sprintEndDate) {

        mRef = mDatabase.getReference();
        mRef.child("projects").child(this.pid).child("sprints").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Timestamp startDateTimestamp = new Timestamp(sprintStartDate);
                        Timestamp endDateTimestamp = new Timestamp(sprintEndDate);

                        for (DataSnapshot d: dataSnapshot.getChildren()) {

                            // converts the longs to timestamp for the snapshot
                            Timestamp snapshotStartDateTimestamp = new Timestamp((long) d.child("sprintStartDate")
                                    .getValue());
                            Timestamp snapshotEndDateTimestamp = new Timestamp((long) d.child("sprintEndDate")
                                    .getValue());

                            /*
                            If the user defined dates are contained within a sprint that already exists, then raise an
                            error
                             */
                            if ((startDateTimestamp.after(snapshotStartDateTimestamp) &&
                                    endDateTimestamp.before(snapshotEndDateTimestamp))
                                    || (!startDateTimestamp.after(snapshotEndDateTimestamp) &&
                                    !snapshotStartDateTimestamp.after(endDateTimestamp))) {
                                dateConflictExists = true;
                            }
                        }

                        if (dateConflictExists == false) { // if not conflicts exist then add to DB
                            addSprintToDatabase(sprintName, sprintDesc, sprintStartDate, sprintEndDate);
                        } else {
                            // an error occurred, notfiy the calling activity
                            createSprintView.showMessage("Dates for sprint overlap with another sprint, please change " +
                                    "the date.");
                            dateConflictExists = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

}
