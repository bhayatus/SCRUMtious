package ca.mvp.scrumtious.scrumtious.view_impl;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.mvp.scrumtious.scrumtious.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PBInProgressFragment extends Fragment {


    public PBInProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pid = getArguments().getString("projectId");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pbin_progress, container, false);

        return view;
    }

}
