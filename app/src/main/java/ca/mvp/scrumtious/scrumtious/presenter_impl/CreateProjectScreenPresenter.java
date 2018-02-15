package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateProjectScreenPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateProjectScreenViewInt;

/**
 * Created by Nensi on 2018-02-11.
 */

public class CreateProjectScreenPresenter implements CreateProjectScreenPresenterInt {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private CreateProjectScreenViewInt createProjectScreenView;

    public CreateProjectScreenPresenter(CreateProjectScreenViewInt createProjectScreenView) {
        this.createProjectScreenView = createProjectScreenView;
    }


    @Override
    public void addProjectToDatabase(String projectTitle, String projectDesc) {
        firebaseAuth = FirebaseAuth.getInstance();

        final String projectOwnerUid = firebaseAuth.getCurrentUser().getUid();
        String projectOwnerEmail = firebaseAuth.getCurrentUser().getEmail();

        HashMap<String, String> projectMap = new HashMap<>();
        projectMap.put("projectTitle", projectTitle);
        projectMap.put("projectDesc", projectDesc);
        projectMap.put("projectOwnerUid", projectOwnerUid);
        projectMap.put("projectOwnerEmail", projectOwnerEmail);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        final String projectId = mRef.push().getKey();

        mRef.child("projects").child(projectId).setValue(projectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                mRef.child("projects").child(projectId).child(projectOwnerUid).setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        mRef.child("users").child(projectOwnerUid).child(projectId).setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                createProjectScreenView.onSuccessfulCreateProject();
                            }
                        });

                    }
                });
            }
        });

    }
}
