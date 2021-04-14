package com.simpulatorC.simulator.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

public class FragmentBlindArriving extends Fragment {

    private static String STATE_FILE = "State",
        TIME_FILE = "Time",
        DIRECTION_FILE = "Direction",
        METERS_FILE = "Meters";

    private EditText commandLine;
    private Button turnFlashlights;
    private ImageButton moveForward, moveRight,
        moveBack, moveLeft, sleep_mode;
    private TextView state;
    private Animation fadeIn_state, fadeOut_state;
    private Handler handler;
    private boolean isMoving = false;
    private Drawable generalDrawable;
    private ImageButton selectedDirection;

    private void ShowToastMessage(String text) { Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();}

    private void ReturnToGeneralState() // When handler is posted delayed.
    {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        isMoving = false;
        state.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadein));
        state.setText(getString(R.string.state));
        selectedDirection.setBackgroundResource(R.drawable.style_button_rounded_black);
        selectedDirection.setImageDrawable(generalDrawable);
        ShowToastMessage(getString(R.string.cancelled));
    }

    private Drawable getDrawable(int id) { // Method, which return picture from project's resources.
        return getResources().getDrawable(id);
    }

    private void TurnOnOffFlashlight(int textColour, String text, int drawableId) // Method, which enable/disable flashlights
    {
        turnFlashlights.setBackgroundResource(drawableId);
        turnFlashlights.setText(text);
        turnFlashlights.setTextColor(textColour);
    }

    private void Move(String direction, final ImageButton button, int meters, long delay) // MOVE
    {
        if (!isMoving)
        {
            handler = new Handler();
            generalDrawable = button.getDrawable();
            // True, if robot isn't moving.
            isMoving = true;
            // Save state
            FileStream fileStream = new FileStream();
            try
            {
                fileStream.SaveFile("Move", getActivity().openFileOutput(STATE_FILE, Context.MODE_PRIVATE)); // Save state
                fileStream.SaveFile(String.valueOf(meters),getActivity().openFileOutput(METERS_FILE, Context.MODE_PRIVATE)); // Save distance
                fileStream.SaveFile(direction, getActivity().openFileOutput(DIRECTION_FILE, Context.MODE_PRIVATE)); // Save direction of moving
            } catch (FileNotFoundException e) { e.printStackTrace(); }

            selectedDirection = button;
            selectedDirection.setImageResource(R.drawable.ic_cancel_execution);
            selectedDirection.setBackgroundResource(R.drawable.moving_active);

            state.setText(getString(R.string.state) +" "+
                    getString(R.string.robot) +" "+  direction +" "+
                    getString(R.string.on) +" "+ meters +" "+ getString(R.string.meters) + " (" + delay/1000/3600 + ":" + delay/1000/60 + ")");
            state.startAnimation(fadeOut_state);

            handler.postDelayed(new Runnable() { // Delaying independently number of meters.
                @Override
                public void run() {
                    ReturnToGeneralState();
                }
            }, delay);// Delay
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

    private int getNumberFromCommand(String command) // Get number, if command has numbers
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
            case "sleep": SleepMode(false, fadeIn_state, getString(R.string.robot_sleep), R.drawable.style_button_rounded_blue); break;
            case "moveforward": Move(getString(R.string.moveForward), moveForward, numSteps, 750 * numSteps);break; // Move forward
            case "moveback": Move(getString(R.string.moveBack), moveBack, numSteps, 750 * numSteps);break; // Move back
            case "moveright": Move(getString(R.string.moveRight), moveRight, numSteps, 750 * numSteps);break; // move right
            case "moveleft": Move(getString(R.string.moveLeft), moveLeft, numSteps, 750 * numSteps);break; // move left
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
            @Override public void onClick(View view) { // Sleep mode
                if (sleep_mode.getBackground().getConstantState().equals(getDrawable(R.drawable.style_button_rounded_black).getConstantState()))
                {
                    if (handler != null) // Disable postDelayed method, when robot is moving.
                        ReturnToGeneralState();
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
            @Override public void onClick(View view) {
                if (moveForward.getBackground().getConstantState().equals(getDrawable(R.drawable.moving_active).getConstantState()))
                    ReturnToGeneralState();
                else Move(getString(R.string.moveForward), moveForward, 2, 1500);
            }
        });

        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (moveBack.getBackground().getConstantState().equals(getDrawable(R.drawable.moving_active).getConstantState()))
                    ReturnToGeneralState();
                else Move(getString(R.string.moveBack), moveBack, 2,1500);
            }});
        moveRight.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (moveRight.getBackground().getConstantState().equals(getDrawable(R.drawable.moving_active).getConstantState()))
                    ReturnToGeneralState();
                else Move(getString(R.string.moveRight), moveRight, 2,1500);
            }
        });
        moveLeft.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (moveLeft.getBackground().getConstantState().equals(getDrawable(R.drawable.moving_active).getConstantState()))
                    ReturnToGeneralState();
                else Move(getString(R.string.moveLeft), moveLeft, 2,1500);
            }
        });

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
                case "Move":
                    try
                    {
                        FileStream fileStream = new FileStream();
                        long previousTime = Long.parseLong(fileStream.ReadFile(getActivity().openFileInput(TIME_FILE))),
                            currentTime = Calendar.getInstance().getTimeInMillis();
                        int meters = Integer.parseInt(fileStream.ReadFile(getActivity().openFileInput(METERS_FILE)));
                        long currentDelay = 750 * meters - (currentTime - previousTime);
                        Log.d("123456", fileStream.ReadFile(getActivity().openFileInput(DIRECTION_FILE)));
                        switch (fileStream.ReadFile(getActivity().openFileInput(DIRECTION_FILE)))
                        {
                            case "forward":
                                Move(getString(R.string.moveForward), moveForward, meters, currentDelay);
                                break;
                            case "left":
                                Move(getString(R.string.moveLeft), moveLeft, meters, currentDelay);
                                break;
                            case "right":
                                Move(getString(R.string.moveRight), moveRight, meters, currentDelay);
                                break;
                            case "back":
                                Move(getString(R.string.moveBack), moveBack, meters, currentDelay);
                                break;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isMoving)
        {
            try { // Save time, if user quited from app.
                new FileStream().SaveFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), getActivity().openFileOutput(TIME_FILE, Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
