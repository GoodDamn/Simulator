package com.simpulatorC.simulator.fragmentAdapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.simpulatorC.simulator.fragments.FragmentBlindArriving;
import com.simpulatorC.simulator.fragments.FragmentCamera;

public class FragmentAdapterMain extends FragmentPagerAdapter {

    private Fragment[] fragments = {
            new FragmentCamera(),
            new FragmentBlindArriving()
    };

    public FragmentAdapterMain(@NonNull FragmentManager fm) { super(fm); }
    @NonNull @Override public Fragment getItem(int position) { return fragments[position]; }
    @Override public int getCount() { return fragments.length; }
}
