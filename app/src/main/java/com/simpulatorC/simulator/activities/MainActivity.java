package com.simpulatorC.simulator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simpulatorC.simulator.R;
import com.simpulatorC.simulator.fragmentAdapters.FragmentAdapterMain;
import com.simpulatorC.simulator.fragments.FragmentBlindArriving;
import com.simpulatorC.simulator.fragments.FragmentCamera;

public class MainActivity extends AppCompatActivity {

    private void HideKeyboard() // Method, which hide soft keyboard.
    {
        View view = getCurrentFocus();
        if (view != null)
        {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = findViewById(R.id.fragment_container); // Find view pager on layout.
        final BottomNavigationView bottomNavigationView = findViewById(R.id.nav_botBar); // Find Bottom navigation bar on layout.

        viewPager.setAdapter(new FragmentAdapterMain(getSupportFragmentManager())); // Set Fragments for view pager
        viewPager.setCurrentItem(0); // Set "Visual Odometry" page. (Default)

        viewPager.setOnTouchListener(new View.OnTouchListener() { // Listen, when user touched down.
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    HideKeyboard(); // Hide keyboard, if user used EditText.
                return false;
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // Listen, when user scrolled view pager;
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override public void onPageSelected(int position) {
                int selectedPage = -1;
                switch (viewPager.getCurrentItem())
                {
                    case 0: selectedPage = R.id.nav_camera; break;  // Page "Visual Odometry"
                    case 1: selectedPage = R.id.nav_blind_arriving; break; // Page "Blind Arriving"
                }
                bottomNavigationView.setSelectedItemId(selectedPage);
            }
            @Override public void onPageScrollStateChanged(int state) { }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { // Listen, when user selected a page.
                switch (menuItem.getItemId())
                {
                    case R.id.nav_camera: // Page "Visual Odometry"
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_blind_arriving: // Page "Blind Arriving"
                        viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

    }
}
