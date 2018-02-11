package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateProjectScreenPresenterInt;

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

    // In case for whatever reason the user is logged out.
    @Override
    public void setupAuthenticationListener() {

//        firebaseAuth = FirebaseAuth.getInstance();
//        if (firebaseAuth.getCurrentUser() == null) createProjectScreenView.goToLogin();

    }

    @Override
    public void addProjectToDatabase(String projectTitle, String projectDesc) {

        final String projectOwnerUid = firebaseAuth.getCurrentUser().getUid();
        String projectOwnerEmail = firebaseAuth.getCurrentUser().getEmail();

        HashMap<String, String> projectMap = new HashMap<>();
        projectMap.put("projectTitle", projectTitle);
        projectMap.put("projectDesc", projectDesc);
        projectMap.put("projectOwnerUid", projectOwnerUid);
        projectMap.put("projectOwnerEmail", projectOwnerEmail);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("projects");
        final String projectId = mRef.push().getKey();

        mRef.child(projectId).setValue(projectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mRef.child(projectId).child(projectOwnerUid).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        createProjectScreenView.onSuccessfulCreateProject();
                    }
                });
            }
        });

    }
}
