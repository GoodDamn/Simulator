package com.simpulatorC.simulator.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simpulatorC.simulator.FileStream;
import com.simpulatorC.simulator.R;

import java.io.FileNotFoundException;

public class FragmentBlindArriving extends Fragment {

    private static String STATE_FILE = "State";

    private EditText commandLine;
    private Button turnFlashlights;
    private ImageButton moveForward, moveRight,
        moveBack, moveLeft, sleep_mode;
    private TextView state;
    private Animation fadeIn_state, fadeOut_state;

    private Drawable getDrawable(int id) { // Method, which return picture from project's resources.
        return getResources().getDrawable(id);
    }

    private void TurnOnOffFlashlight(int textColour, String text, int drawableId) // Method, which enable/disable flashlights
    {
        turnFlashlights.setBackgroundResource(drawableId);
        turnFlashlights.setText(text);
        turnFlashlights.setTextColor(textColour);
    }

    private boolean isMoving = false;
    private void Move(String side, final ImageButton button, int meters)
    {
        if (!isMoving)
        {
            // True, if robot isn't moving.
            isMoving = true;
            button.setBackgroundResource(R.drawable.moving_active);
            state.setText(getString(R.string.state) +" "+
                    getString(R.string.robot) +" "+  side +" "+
                    getString(R.string.on) +" "+ meters +" "+ getString(R.string.meters));
            state.startAnimation(fadeOut_state);
            new Handler().postDelayed(new Runnable() { // Delaying independently number of meters.
                @Override
                public void run() {
                    isMoving = false;
                    state.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadein));
                    state.setText(getString(R.string.state));
                    button.setBackgroundResource(R.drawable.style_button_rounded_black);
                }
            }, 750 * meters);// Delay
        }
    }

    private void SleepMode(boolean enabled, Animation animation, String textState, int idDrawable) // Method, which disable/enable sleep mode
    {
        sleep_mode.setBackgroundResource(idDrawable);
        state.setText(getString(R.string.state) +" "+ textState);
        state.startAnimation(animation);
        //Disable all components on the screen.
        moveBack.setEnabled(enabled);
        moveLeft.setEnabled(enabled);
        moveRight.setEnabled(enabled);
        moveForward.setEnabled(enabled);
        turnFlashlights.setEnabled(enabled);
        commandLine.setEnabled(enabled);
    }

    private int getNumberFromCommand(String command) // Get nuumber, if command has numbers
    {
        String number = "";
        for (char ch : command.toCharArray()) {
            if (ch == '1' || ch=='2' || ch=='3' ||
                ch == '4' || ch=='5' || ch=='6' ||
                ch == '7' || ch=='8' || ch=='9' || ch=='0')
                number += ch;
        }
        return Integer.parseInt(number);
    }

    private String getCommand(String command) // Get command name from command line with numbers
    {
        String com = "";
        for (char ch : command.toCharArray()) {
            if (!(ch == '1' || ch=='2' || ch=='3' ||
                    ch == '4' || ch=='5' || ch=='6' ||
                    ch == '7' || ch=='8' || ch=='9'))
                com += ch;
            else break;
        }
        return com;
    }

    private void ExecuteCommand() //Method, which execute command from command line
    {
        // String to lower case and remove whitespaces.
        String command = commandLine.getText().toString().toLowerCase().replaceAll("\\s+", "");
        int numSteps = 0;
        if ( (command.indexOf("moveforward") != -1) || (command.indexOf("moveback") != -1) ||
                (command.indexOf("moveright") != -1) || (command.indexOf("moveleft") != -1))
        {
            numSteps = getNumberFromCommand(command);
            command = getCommand(command);
        }
        switch (command)
        {
            case "turnonflashlights": // Turn on flashlights
                command = "Flashlights have been turned on";
                TurnOnOffFlashlight(Color.BLACK, getString(R.string.turn_on_flashlights),
                        R.drawable.style_button_rounded_yellow);
                break;
            case "turnoffflashlights": // Turn off flashlights
                command = "Flashlights have been turned off";
                TurnOnOffFlashlight(Color.WHITE, getString(R.string.turn_off_flashlights),
                        R.drawable.style_button_rounded_black);
                break;
            case "moveforward": Move(getString(R.string.moveForward), moveForward, numSteps);break; // Move forward
            case "moveback": Move(getString(R.string.moveBack), moveBack, numSteps);break; // Move back
            case "moveright": Move(getString(R.string.moveRight), moveRight, numSteps);break; // move right
            case "moveleft": Move(getString(R.string.moveLeft), moveLeft, numSteps);break; // move left
            default: // If user typed other command.
                command = getString(R.string.dont_find_command);
                break;
        }
        Toast.makeText(getContext(), command, Toast.LENGTH_SHORT).show(); // Show message to user.
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_main_fragment_blind, container, false);

        state = v.findViewById(R.id.textView_state);
        moveBack = v.findViewById(R.id.Ibutton_move_back); // Find button with down arrow
        moveForward = v.findViewById(R.id.Ibutton_move_forward); // Find button with upward arrow
        moveLeft = v.findViewById(R.id.Ibutton_move_Left); // Find button with left arrow
        moveRight = v.findViewById(R.id.Ibutton_move_right); // Find button with right arrow

        fadeIn_state = AnimationUtils.loadAnimation(getContext(),R.anim.fadein); // Initialize fade in animation for state
        fadeOut_state = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout); // Initialize fade out animation for state
        fadeIn_state.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationEnd(Animation animation) {
                state.startAnimation(fadeOut_state);
            }
            @Override public void onAnimationRepeat(Animation animation) { }
        });
        fadeOut_state.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationEnd(Animation animation) {
                state.startAnimation(fadeIn_state);
            }
            @Override public void onAnimationRepeat(Animation animation) { }
        });

        sleep_mode = v.findViewById(R.id.Ibutton_sleepMode);
        sleep_mode.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (sleep_mode.getBackground().getConstantState().equals(getDrawable(R.drawable.style_button_rounded_black).getConstantState()))
                {
                    // Enable sleep mode
                    SleepMode(false, fadeIn_state, "Robot is sleeping", R.drawable.style_button_rounded_blue);
                    try {
                        new FileStream().SaveFile("Sleep", getActivity().openFileOutput(STATE_FILE, Context.MODE_PRIVATE));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //Disable sleep mode
                    state.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fadein));
                    SleepMode(true,AnimationUtils.loadAnimation(getContext(),R.anim.fadein),
                            "", R.drawable.style_button_rounded_black);
                    try {
                        new FileStream().SaveFile("", getActivity().openFileOutput(STATE_FILE, Context.MODE_PRIVATE));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        moveForward.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveForward), moveForward, 2); }});
        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveBack), moveBack, 2); }});
        moveRight.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveRight), moveRight,2); }});
        moveLeft.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveLeft), moveLeft,2); }});

        commandLine = v.findViewById(R.id.edittext_commandLine);
        commandLine.setOnEditorActionListener(new TextView.OnEditorActionListener() { // Action, when user clicked "Enter" on soft keyboard.
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                ExecuteCommand();
                return false;
            }
        });

        turnFlashlights  = v.findViewById(R.id.button_turnOnOff_Flashlights); // Find "Flashlights" button.
        turnFlashlights.setOnClickListener(new View.OnClickListener() { // Listen, when user clicked.
            @Override
            public void onClick(View view) {
                // Check current background of button.
                // If button had the state "Turned off"
                if (turnFlashlights.getBackground()
                        .getConstantState()
                        .equals(getDrawable(R.drawable.style_button_rounded_black).getConstantState()))
                    TurnOnOffFlashlight(Color.BLACK, getString(R.string.turn_on_flashlights),
                            R.drawable.style_button_rounded_yellow); // Change state of button on "Turn on"
                else TurnOnOffFlashlight(Color.WHITE, getString(R.string.turn_off_flashlights),
                        R.drawable.style_button_rounded_black); // Change state of button on "Turn off"

            }
        });

        // Load current state from text file from device storage.
        String text = null;
        try {
            text = new FileStream().ReadFile(getActivity().openFileInput(STATE_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (text != null)
        {
            switch (text)
            {
                case "Sleep":
                    SleepMode(false, fadeIn_state, "Robot is sleeping", R.drawable.style_button_rounded_blue);
                    break;
            }
        }

        return v;
    }
}
