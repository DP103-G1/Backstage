package com.example.waiter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.g1.R;

public class MySelectAdapter extends FragmentStatePagerAdapter {
    private static final int[] TAB_TITLES = new int[]{R.string.textSelectBooking,R.string.textSelectOrder};

    private Context mContext;

    public MySelectAdapter(@NonNull Context context, FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new WaiterSelectFragment();
            case 1:
                return new WaiterTableFragment ();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}
