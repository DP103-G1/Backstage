package com.example.waiter;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.g1.R;
import com.google.android.material.tabs.TabLayout;

public class WaiterFragment extends Fragment {
    private static final String TAG = "TAG_SelectFragment";
    private Activity activity;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MySelectAdapter pagerAdapter;

//    private ImageView ivBooking,ivOrderMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        activity.setTitle("查詢");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pagerAdapter = new MySelectAdapter(activity,getChildFragmentManager());
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);








//        ivBooking = view.findViewById(R.id.ivBooking);
//        ivOrderMenu = view.findViewById(R.id.ivOrderMenu);
//
//        ivBooking.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_waiterFragment_to_waiterSelectFragment);
//            }
//        });
//
//        ivOrderMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_waiterFragment_to_waiterTableFragment);
//            }
//        });
    }
}
