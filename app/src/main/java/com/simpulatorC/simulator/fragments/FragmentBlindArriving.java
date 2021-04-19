package com.simpulatorC.simulator.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import com.simpulatorC.simulator.FileStream;
import com.simpulatorC.simulator.R;
import com.simpulatorC.simulator.activities.ActivitySupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Random;

public class FragmentBlindArriving extends Fragment {

    private static String STATE_FILE = "State",
        TIME_FILE = "Time",
        DIRECTION_FILE = "Direction",
        PREV_DELAY_FILE = "PrevDelay",
        COMMANDS_FILE = "Commands",
        METERS_FILE = "Meters",
        FLASHLIGHTS_FILE = "Flashlight",
        AUTO_FILE = "AutoState";

    private String[] moves;

    private EditText commandLine;
    private Button turnFlashlights;
    private ImageButton moveForward, moveRight,
        moveBack, moveLeft, sleep_mode, pincer_up, pincer_down,
        clockwise, not_clockwise;
    private TextView state;
    private Animation fadeIn_state, fadeOut_state;
    private boolean isMoving = false;
    private Drawable generalDrawable;
    private ImageButton selectedDirection;
    private ListView stack_of_commands;
    private ArrayAdapter<String> adapter_commands;
    private long currentDelay;
    private Thread thread;
    private boolean isAutoMode = false;

    private void ShowToastMessage(String text) { Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();}

    private void ChangeState(String stateName, long meters, String direction)
    {
        FileStream fileStream = new FileStream();
        try
        {
            fileStream.SaveFile(stateName, getActivity().openFileOutput(STATE_FILE, Context.MODE_PRIVATE)); // Save state
            fileStream.SaveFile(String.valueOf(currentDelay),getActivity().openFileOutput(PREV_DELAY_FILE, Context.MODE_PRIVATE)); // Save distance
            fileStream.SaveFile(String.valueOf(meters), getActivity().openFileOutput(METERS_FILE, Context.MODE_PRIVATE));
            fileStream.SaveFile(direction, getActivity().openFileOutput(DIRECTION_FILE, Context.MODE_PRIVATE)); // Save direction of moving
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    private void ReturnToGeneralState() // When handler is posted delayed.
    {
        if(thread != null) thread.interrupt(); // Close thread
        currentDelay = 0;
        ChangeState("",0,"nowhere");
        isMoving = false;
        state.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadein));
        state.post(new Runnable() {
            @Override
            public void run() {
                state.setText(getString(R.string.state));
            }
        });
        selectedDirection.setBackgroundResource(R.drawable.style_button_rounded_black);
        selectedDirection.setImageDrawable(generalDrawable);
        if (!adapter_commands.isEmpty())
        {
            final String nextCommand = adapter_commands.getItem(0);
            ExecuteCommand(nextCommand);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter_commands.remove(nextCommand);
                }
            });
        }
    }

    private Drawable getDrawable(int id) { // Method, which return picture from project's resources.
        return getResources().getDrawable(id);
    }

    private void TurnOnOffFlashlight(String state,int textColour, String text, int drawableId) // Method, which enable/disable flashlights
    {
        try { // Save state of flashlights (on/off)
            new FileStream().SaveFile(state, getActivity().openFileOutput(FLASHLIGHTS_FILE, Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) { e.printStackTrace(); }
        turnFlashlights.setBackgroundResource(drawableId);
        turnFlashlights.setText(text);
        turnFlashlights.setTextColor(textColour);
    }

    private void Move(final String direction, final ImageButton button, final long meters, final long delay) // MOVE
    {
        if (!isMoving)
        {
            Log.d("123456", " " + meters + " " + delay);
            generalDrawable = button.getDrawable();
            // True, if robot isn't moving.
            isMoving = true;
            // Change and save state
            ChangeState("Move", meters, direction);
            selectedDirection = button;
            selectedDirection.setImageResource(R.drawable.ic_cancel_execution);
            selectedDirection.setBackgroundResource(R.drawable.moving_active);

            state.startAnimation(fadeOut_state);

            currentDelay = delay;

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (currentDelay > 0)
                    {
                        try
                        {
                            Thread.sleep(1);
                            state.post(new Runnable() {
                                @Override
                                public void run() {
                                    long hours = currentDelay / 1000 / 3600,
                                            minutes = currentDelay / 1000/ 60 % 60,
                                            seconds = ((currentDelay/1000) - (hours * minutes * 60)) % 60;
                                    try {
                                        state.setText(getString(R.string.state) + " " +
                                                getString(R.string.robot) + " " + direction + " " +
                                                getString(R.string.on) + " " + meters + " " +
                                                getString(R.string.meters) + " (" + hours + "h : "
                                                + minutes + "m : " + seconds + "sec.)");
                                    }catch (Exception ignored) {}
                                    currentDelay--;
                                }
                            });
                        }
                        catch (InterruptedException e)
                        { e.printStackTrace(); }
                    }
                    ReturnToGeneralState();
                }
            });

            thread.start();
        }
    }

    private void UIState(boolean isEnabled)
    {
        moveBack.setEnabled(isEnabled);
        moveForward.setEnabled(isEnabled);
        moveLeft.setEnabled(isEnabled);
        moveRight.setEnabled(isEnabled);
        turnFlashlights.setEnabled(isEnabled);
        clockwise.setEnabled(isEnabled);
        not_clockwise.setEnabled(isEnabled);
        pincer_down.setEnabled(isEnabled);
        pincer_up.setEnabled(isEnabled);
        stack_of_commands.setEnabled(isEnabled);
    }

    private void SleepMode(boolean enabled, Animation animation, String textState, int idDrawable) // Method, which disable/enable sleep mode
    {
        sleep_mode.setBackgroundResource(idDrawable);
        state.setText(getString(R.string.state) +" "+ textState);
        state.startAnimation(animation);
        //Disable all components on the screen.
        UIState(false);
        commandLine.setEnabled(enabled);
    }

    private long getNumberFromCommand(String command) // Get number, if command has numbers
    {
        String number = "";
        for (char ch : command.toCharArray()) {
            if (ch == '1' || ch=='2' || ch=='3' ||
                ch == '4' || ch=='5' || ch=='6' ||
                ch == '7' || ch=='8' || ch=='9' || ch=='0')
                number += ch;
        }
        return Long.parseLong(number);
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

    private void PincersActions(ImageButton button, MotionEvent motionEvent) // Method, which execute, when user touched
            // on button with pincer's icon
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            button.setBackgroundResource(R.drawable.style_button_rounded_pink);
        else button.setBackgroundResource(R.drawable.style_button_rounded_black);
    }

    private void SetOnTouchListenerPincers(final ImageButton pincer)
    {
        pincer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                PincersActions(pincer, motionEvent);
                return false;
            }
        });
    }

    private void SetOnClickListenerMoving(final ImageButton button, final int idStringDirection)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (button.getBackground().getConstantState().equals(getDrawable(R.drawable.moving_active).getConstantState()))
                    ReturnToGeneralState();
                else Move(getString(idStringDirection), button, 2, 1500);
            }
        });
    }

    private void ExecuteCommand(String command) //Method, which execute command from command line
    {
        // String to lower case and remove whitespaces.
        long numSteps = 0;
        if ((command.contains(getString(R.string.moveforward))) || (command.contains(getString(R.string.moveback))) ||
                (command.contains(getString(R.string.moveright))) || (command.contains(getString(R.string.moveleft)))) {
            numSteps = getNumberFromCommand(command);
            command = getCommand(command);
        }
        switch (command) {
            case "автовкл": case "autoon": // Auto mode is enabled
                isAutoMode = true;
                UIState(false);
                Random random = new Random();
                for (int i = 0; i < random.nextInt(10) + 1; i++)
                {
                    String text = moves[random.nextInt(4)] + random.nextInt(5000);
                    if (isMoving) adapter_commands.add(text);
                    else ExecuteCommand(text);
                }
                SaveState();
                StartAutoMode();
                break;
            case "автовыкл": case "autooff": // Auto mode is disabled
                isAutoMode = false;
                UIState(true);
                SaveState();
            break;
            case "turnonflashlights": case "вклфары": // Turn on flashlights
                command = "Flashlights have been turned on";
                TurnOnOffFlashlight("on",Color.BLACK, getString(R.string.turn_off_flashlights), R.drawable.style_button_rounded_yellow);
            break;
            case "turnoffflashlights": case "выклфары": // Turn off flashlights
                 command = "Flashlights have been turned off";
                 TurnOnOffFlashlight("off",Color.WHITE, getString(R.string.turn_on_flashlights), R.drawable.style_button_rounded_black);
            break;
            case "sleep": case "спать": // Command - Sleep
                SleepMode(false, fadeIn_state, getString(R.string.robot_sleep), R.drawable.style_button_rounded_blue);
            break;
            case "moveforward": case "движениевперёд":
                Move(getString(R.string.moveForward), moveForward, numSteps, 750 * numSteps);
            break; // Move forward
            case "moveback": case "движениеназад":
                Move(getString(R.string.moveBack), moveBack, numSteps, 750 * numSteps);
            break; // Move back
            case "moveright": case "движениевправо":
                Move(getString(R.string.moveRight), moveRight, numSteps, 750 * numSteps);
                break; // move right
            case "moveleft": case "движениевлево":
                Move(getString(R.string.moveLeft), moveLeft, numSteps, 750 * numSteps);
            break; // move left
            default: // If user typed other command.
                command = getString(R.string.dont_find_command);
             break;
        }
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_main_fragment_blind, container, false);

        state = v.findViewById(R.id.textView_state);
        moveBack = v.findViewById(R.id.Ibutton_move_back); // Find button with down arrow
        moveForward = v.findViewById(R.id.Ibutton_move_forward); // Find button with upward arrow
        moveLeft = v.findViewById(R.id.Ibutton_move_Left); // Find button with left arrow
        moveRight = v.findViewById(R.id.Ibutton_move_right); // Find button with right arrow
        stack_of_commands = v.findViewById(R.id.listView_stack_of_commands); // Find ListView
        adapter_commands = new ArrayAdapter<>(getContext(), R.layout.list_view_row); // Initialize adapter for listView

        moves = new String[]{
                getString(R.string.moveforward),
                getString(R.string.moveright),
                getString(R.string.moveback),
                getString(R.string.moveleft)
        };

        clockwise = v.findViewById(R.id.Ibutton_clockwise);
        pincer_up = v.findViewById(R.id.Ibutton_pincer_up);
        pincer_down = v.findViewById(R.id.Ibutton_pincer_down);
        not_clockwise = v.findViewById(R.id.Ibutton_not_clockwise);

        SetOnClickListenerMoving(moveForward, R.string.moveForward);
        SetOnClickListenerMoving(moveBack, R.string.moveBack);
        SetOnClickListenerMoving(moveRight, R.string.moveRight);
        SetOnClickListenerMoving(moveLeft, R.string.moveLeft);

        SetOnTouchListenerPincers(clockwise);
        SetOnTouchListenerPincers(pincer_up);
        SetOnTouchListenerPincers(pincer_down);
        SetOnTouchListenerPincers(not_clockwise);

        stack_of_commands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { // Listen, when user clicked on listView's item.
                DialogRemoveItem dialogRemoveItem = new DialogRemoveItem(adapter_commands.getItem(i), adapter_commands);
                dialogRemoveItem.show(getFragmentManager(), "dialog");
            }
        });

        //Initialize Animations
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
                    if (isMoving) ReturnToGeneralState();
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

        ImageView imageView = v.findViewById(R.id.ImageView_commands); // Button, which is started ActivitySupport class.
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Listener
                Intent intent = new Intent(getContext(), ActivitySupport.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.enter_from_top_to_bottom, R.anim.exit_from_top_to_bottom);
                startActivity(intent);
                SaveState();
            }
        });

        commandLine = v.findViewById(R.id.edittext_commandLine); // EditText "Command line"
        commandLine.setOnEditorActionListener(new TextView.OnEditorActionListener() { // Action, when user clicked "Enter" on soft keyboard.
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) { /* Listen, if user pressed on button
            on soft keyboard and focused on EditText "Command Line".*/
                String command = commandLine.getText().toString().toLowerCase().replaceAll("\\s+", "");
                if (isMoving)
                {
                    if (command.equals("autooff") || command.equals("autoon"))
                        ExecuteCommand(command);
                    else {
                        adapter_commands.add(command);
                        stack_of_commands.setAdapter(adapter_commands);
                    }
                }
                else ExecuteCommand(command);
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
                    TurnOnOffFlashlight("on",Color.BLACK, getString(R.string.turn_off_flashlights),
                            R.drawable.style_button_rounded_yellow); // Change state of button on "Turn on"
                else TurnOnOffFlashlight("off",Color.WHITE, getString(R.string.turn_on_flashlights),
                        R.drawable.style_button_rounded_black); // Change state of button on "Turn off"

            }
        });

        // Load current state from text file from device storage.
        FileStream fileStream = new FileStream();

        // Check flashlights state
        try
        {
            if ("on".equals(fileStream.ReadFile(getActivity().openFileInput(FLASHLIGHTS_FILE))))
                TurnOnOffFlashlight("on", Color.BLACK, getString(R.string.turn_off_flashlights), R.drawable.style_button_rounded_yellow);
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        // Check auto mode (true/false)
        try {
            isAutoMode = Boolean.parseBoolean(fileStream.ReadFile(getActivity().openFileInput(AUTO_FILE)));
            UIState(!isAutoMode);
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        // Check main state (move/sleep)
        String text = null;
        try {
            text = new FileStream().ReadFile(getActivity().openFileInput(STATE_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (text != null) // If file not empty
        {
            switch (text) // Select current state
            {
                case "Sleep": // If robot was sleeping last time
                    SleepMode(false, fadeIn_state, "Robot is sleeping", R.drawable.style_button_rounded_blue);
                    break;
                case "Move": // If robot was moving last time.
                    try
                    {
                        String commands = fileStream.ReadFile(getActivity().openFileInput(COMMANDS_FILE)), // Open file with saving commands.
                            com = "";
                        for (char ch : commands.toCharArray()) // Analyze data from file.
                        {
                            if (ch != ' ') com += ch;
                            else {
                                Log.d("123456", com);
                                adapter_commands.add(com); // Add Command to listView
                                com = "";
                            }
                        }
                        stack_of_commands.setAdapter(adapter_commands); // Show commands in listView.
                        long previousTime = Long.parseLong(fileStream.ReadFile(getActivity().openFileInput(TIME_FILE))),
                            currentTime = Calendar.getInstance().getTimeInMillis();
                        long prevDelay = Long.parseLong(fileStream.ReadFile(getActivity().openFileInput(PREV_DELAY_FILE))),
                            meters = Long.parseLong(fileStream.ReadFile(getActivity().openFileInput(METERS_FILE)));
                        long delay = prevDelay - (currentTime - previousTime);
                        if (delay > 0)
                        {
                            switch (fileStream.ReadFile(getActivity().openFileInput(DIRECTION_FILE))) {
                                case "forward":
                                    Move(getString(R.string.moveForward), moveForward, meters, delay);
                                    break;
                                case "left":
                                    Move(getString(R.string.moveLeft), moveLeft, meters, delay);
                                    break;
                                case "right":
                                    Move(getString(R.string.moveRight), moveRight, meters, delay);
                                    break;
                                case "back":
                                    Move(getString(R.string.moveBack), moveBack, meters, delay);
                                    break;
                            }
                        } else Move("forward", moveForward,0,0);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        StartAutoMode();
    }

    private void StartAutoMode()
    {
        final Random random = new Random();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isAutoMode) // Check auto mode
                {
                    try
                    {
                        if (isAutoMode)
                        {
                            if (adapter_commands.getCount() <= 12)
                            {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override public void run()
                                    {
                                        final String text = moves[random.nextInt(4)] + random.nextInt(5000);
                                        if (isMoving) adapter_commands.add(text);
                                        else ExecuteCommand(text);

                                        if (random.nextBoolean()) TurnOnOffFlashlight("on", Color.BLACK,
                                                getString(R.string.turn_on_flashlights), R.drawable.style_button_rounded_yellow);
                                        else TurnOnOffFlashlight("off", Color.WHITE,
                                                getString(R.string.turn_off_flashlights), R.drawable.style_button_rounded_black);
                                    }
                                });
                            }
                        }
                        Thread.sleep(random.nextInt(15000));
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        });
        thread.start();
    }

    public void SaveState() // Method, which save current state of robot in files.
    {
        if (isMoving) // if robot is moving
        {
            FileStream fileStream = new FileStream();
            try { // Save time, stack of commands, autoMode, if user quited from app.
                fileStream.SaveFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), getActivity().openFileOutput(TIME_FILE, Context.MODE_PRIVATE));
                fileStream.SaveFile(String.valueOf(currentDelay), getActivity().openFileOutput(PREV_DELAY_FILE, Context.MODE_PRIVATE));
                fileStream.SaveFile(String.valueOf(isAutoMode), getActivity().openFileOutput(AUTO_FILE, Context.MODE_PRIVATE));
                String strCommands = "";
                for (int i = 0; i < adapter_commands.getCount(); i++)
                {
                    strCommands += adapter_commands.getItem(i);
                    strCommands += " ";
                }
                fileStream.SaveFile(strCommands, getActivity().openFileOutput(COMMANDS_FILE, Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    // Call Dialog window, when the user wants to remove command from list.
    public static class DialogRemoveItem extends AppCompatDialogFragment {

        private String commandName;
        private ArrayAdapter<String> arrayAdapter;

        public DialogRemoveItem(String commandName, ArrayAdapter<String> arrayAdapter)
        {
            this.commandName = commandName;
            this.arrayAdapter = arrayAdapter;
        }

        @NonNull @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.removeItem))
                    .setMessage(getString(R.string.removeCommand) + " (" + commandName + ") ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() { // If clicked "OK".
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            arrayAdapter.remove(commandName);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null);
            return builder.create();
        }
    }
}