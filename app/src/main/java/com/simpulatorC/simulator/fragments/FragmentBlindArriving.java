package com.simpulatorC.simulator.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simpulatorC.simulator.R;

public class FragmentBlindArriving extends Fragment {

    private Drawable getDrawable(int id) { // Method, which return picture from project's resources.
        return getResources().getDrawable(id);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_main_fragment_blind, container, false);

        final Button turnFlashlights  = v.findViewById(R.id.button_turnOnOff_Flashlights);
        turnFlashlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (turnFlashlights.getBackground().getConstantState().equals(getDrawable(R.drawable.style_button_turned_off_flashlight).getConstantState()))
                {
                    turnFlashlights.setBackgroundResource(R.drawable.style_button_turned_on_flashlight);
                    turnFlashlights.setText(getString(R.string.turn_on_flashlights));
                }
                else
                {
                    turnFlashlights.setBackgroundResource(R.drawable.style_button_turned_off_flashlight);
                    turnFlashlights.setText(getString(R.string.turn_off_flashlights));
                }
            }
        });

        return v;
    }
}
