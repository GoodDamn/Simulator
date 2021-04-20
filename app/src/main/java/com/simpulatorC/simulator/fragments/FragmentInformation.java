package com.simpulatorC.simulator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simpulatorC.simulator.FileStream;
import com.simpulatorC.simulator.R;

import java.io.FileNotFoundException;

public class FragmentInformation extends Fragment {

    private TextView distance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_information, container,false);

        distance = v.findViewById(R.id.information_distance);
        distance.setText(getString(R.string.distance_traveled) + " 0m");

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            distance.setText(getString(R.string.distance_traveled) + " " +
                    new FileStream().ReadFile(getActivity().openFileInput("Distance")) + "m");
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }
}
