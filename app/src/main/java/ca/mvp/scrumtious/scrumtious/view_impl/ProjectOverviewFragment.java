package ca.mvp.scrumtious.scrumtious.view_impl;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ca.mvp.scrumtious.scrumtious.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectOverviewFragment extends Fragment {


    private String pid;
    private Button productBacklogBtn;

    public ProjectOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pid = getArguments().getString("projectId");
    }


    public void onClickProductBacklogBtn(View view) {
        Intent intent = new Intent(getActivity(), ProductBacklogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("projectId", pid);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_overview, container, false);

        productBacklogBtn = view.findViewById(R.id.btn_product_backlog);

        productBacklogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickProductBacklogBtn(view);
            }
        });

        return view;
    }

}
