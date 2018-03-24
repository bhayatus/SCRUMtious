package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;

    private CreateSprintViewInt createSprintView;
    
    private final String PROJECT_ID;

    private boolean dateConflictExists = false;


    public CreateSprintPresenter(CreateSprintViewInt createSprintView, String pid) {
        this.createSprintView = createSprintView;
        this.PROJECT_ID = pid;
    }

    private void addSprintToDatabase(final String sprintName, final String sprintDesc, final long sprintStartDate, final long sprintEndDate) {
         firebaseDatabase = FirebaseDatabase.getInstance();
         firebaseRootReference = firebaseDatabase.getReference().child("projects").child(this.PROJECT_ID);

         final String sprintId = firebaseRootReference.push().getKey(); // generates unique key for sprint

        firebaseRootReference.child("numSprints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numSprints = (long) dataSnapshot.getValue();
                numSprints++;

                Map sprintMap = new HashMap<>();

                // All must happen to ensure atomicity
                sprintMap.put("/sprints/" + sprintId + "/" + "sprintName", sprintName);
                sprintMap.put("/sprints/" + sprintId + "/" + "sprintDesc", sprintDesc);
                sprintMap.put("/sprints/" + sprintId + "/" + "sprintStartDate", sprintStartDate);
                sprintMap.put("/sprints/" + sprintId + "/" + "sprintEndDate", sprintEndDate);
                sprintMap.put("/" + "numSprints", numSprints);

                firebaseRootReference.updateChildren(sprintMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            createSprintView.onSuccessfulCreateSprint();
                        } else {
                            createSprintView.showMessage("An error occurred, failed to create the sprint.", false);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void checkConflictingSprintDates(final String sprintName, final String sprintDesc,
                                              final long sprintStartDate, final long sprintEndDate) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        firebaseRootReference.child("projects").child(this.PROJECT_ID).child("sprints").addListenerForSingleValueEvent(
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

                            // POSSIBLE SCENARIOS FOR OVERLAP //

                            //(my time range)               ///////
                            //(other time range)        //////

                            if (startDateTimestamp.after(snapshotStartDateTimestamp) &&
                                    startDateTimestamp.before(snapshotEndDateTimestamp)){
                                dateConflictExists = true;
                            }

                            //(my time range)           //////
                            //(other time range)           //////

                            if (endDateTimestamp.after(snapshotStartDateTimestamp) &&
                                    endDateTimestamp.before(snapshotEndDateTimestamp)){
                                dateConflictExists = true;
                            }


                            //(my time range)         ///////////
                            //(other time range)        /////

                            if (startDateTimestamp.before(snapshotStartDateTimestamp) &&
                                    endDateTimestamp.after(snapshotEndDateTimestamp)){
                                dateConflictExists = true;
                            }

                            //(my time range)       //////
                            //(old time range)    //////////     is handled by both of the above


                            //(my time range)     ////           ////    /////
                            //(old time range)       /////    ////       /////     are all handled below

                            if (startDateTimestamp.equals(snapshotEndDateTimestamp) || endDateTimestamp.equals(snapshotStartDateTimestamp)
                                    || startDateTimestamp.equals(snapshotStartDateTimestamp) || endDateTimestamp.equals(snapshotEndDateTimestamp)){
                                dateConflictExists = true;
                            }
                        }

                        if (!dateConflictExists) { // if no conflicts exist then add to DB
                            addSprintToDatabase(sprintName, sprintDesc, sprintStartDate, sprintEndDate);
                        } else {
                            // an error occurred, notify the calling activity
                            createSprintView.showMessage("Dates for sprint overlap with another sprint, please select a different interval.", false);
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
