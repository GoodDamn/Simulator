package com.simpulatorC.simulator.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.simpulatorC.simulator.R;

public class FragmentBlindArriving extends Fragment {

    private EditText commandLine;
    private Button turnFlashlights;
    private ImageButton moveForward, moveRight,
        moveBack, moveLeft;
    private TextView state;
    private Animation fadeIn_state, fadeOut_state;

    private Drawable getDrawable(int id) { // Method, which return picture from project's resources.
        return getResources().getDrawable(id);
    }

    private void TurnOnOffFlashlight(int textColour, String text, int drawableId)
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
            isMoving = true;
            button.setBackgroundResource(R.drawable.moving_active);
            state.setText(getString(R.string.state) +" "+
                    getString(R.string.robot) +" "+  side +" "+
                    getString(R.string.on) +" "+ meters +" "+ getString(R.string.meters));
            state.startAnimation(fadeOut_state);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isMoving = false;
                    state.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadein));
                    state.setText(getString(R.string.state));
                    button.setBackgroundResource(R.drawable.style_button_turned_off_flashlight);
                }
            }, 750 * meters);
        }
    }

    private int getNumberFromCommand(String command)
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

    private String getCommand(String command)
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
            case "turnonflashlights":
                command = "Flashlights have been turned on";
                TurnOnOffFlashlight(Color.BLACK, getString(R.string.turn_on_flashlights),
                        R.drawable.style_button_turned_on_flashlight);
                break;
            case "turnoffflashlights":
                command = "Flashlights have been turned off";
                TurnOnOffFlashlight(Color.WHITE, getString(R.string.turn_off_flashlights),
                        R.drawable.style_button_turned_off_flashlight);
                break;
            case "moveforward": Move(getString(R.string.moveForward), moveForward, numSteps);break;
            case "moveback": Move(getString(R.string.moveBack), moveBack, numSteps);break;
            case "moveright": Move(getString(R.string.moveRight), moveRight, numSteps);break;
            case "moveleft": Move(getString(R.string.moveLeft), moveLeft, numSteps);break;
            default:
                command = getString(R.string.dont_find_command);
                break;
        }
        Toast.makeText(getContext(), command, Toast.LENGTH_SHORT).show();
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

        moveForward.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveForward), moveForward, 2); }});
        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveBack), moveBack, 2); }});
        moveRight.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveRight), moveRight,2); }});
        moveLeft.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { Move(getString(R.string.moveLeft), moveLeft,2); }});

        commandLine = v.findViewById(R.id.edittext_commandLine);
        commandLine.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                        .equals(getDrawable(R.drawable.style_button_turned_off_flashlight).getConstantState()))
                    TurnOnOffFlashlight(Color.BLACK, getString(R.string.turn_on_flashlights),
                            R.drawable.style_button_turned_on_flashlight); // Change state of button on "Turn on"
                else TurnOnOffFlashlight(Color.WHITE, getString(R.string.turn_off_flashlights),
                        R.drawable.style_button_turned_off_flashlight); // Change state of button on "Turn off"

            }
        });

        return v;
    }
}
