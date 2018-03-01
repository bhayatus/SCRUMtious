package ca.mvp.scrumtious.scrumtious.view_impl;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.ValueEventListener;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.SprintOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.SprintOverviewViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.SprintOverviewPresenter;

public class SprintOverviewFragment extends Fragment implements SprintOverviewViewInt {

    private SprintOverviewPresenterInt sprintOverviewPresenter;
    private String pid, sid;
    private TextView sprintTitle, sprintDescription;

    private ValueEventListener sprintDetailsListener;

    public SprintOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pid = getArguments().getString("projectId");
        sid = getArguments().getString("sprintId");
        this.sprintOverviewPresenter = new SprintOverviewPresenter(this, pid, sid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sprint_overview, container, false);

        sprintTitle = view.findViewById(R.id.sprintOverviewTitle);
        sprintDescription = view.findViewById(R.id.sprintOverviewDesc);

        return view;
    }

    @Override
    public void onResume() {
        sprintDetailsListener = sprintOverviewPresenter.getSprintDetailsListener();
        super.onResume();
    }

    @Override
    public void onPause() {
        sprintOverviewPresenter.removeSprintDetailsListener(sprintDetailsListener);
        super.onPause();
    }


    // Set the details into the respective views
    @Override
    public void setSprintDetails(String titleViewText, String descriptionViewText) {
        sprintTitle.setText(titleViewText);
        sprintDescription.setText(descriptionViewText);
    }
}
