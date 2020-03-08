package com.example.waiter;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.R;
import com.example.main.Url;
import com.example.manager.table.Table;
import com.example.socket.SocketMessage;
import com.example.task.CommonTask;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WaiterFragment extends Fragment {
    private static final String TAG = "TAG_SelectFragment";
    private Activity activity;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MySelectAdapter pagerAdapter;
    private LocalBroadcastManager broadcastManager;

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
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerSocketReceiver();
        pagerAdapter = new MySelectAdapter(activity,getChildFragmentManager());
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.clear_service);
        if (showServiceIcon()) {
            tabLayout.getTabAt(1).setIcon(R.drawable.service);
        } else {
            tabLayout.getTabAt(1).setIcon(R.drawable.clear_service);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TabLayout.Tab tableTab = tabLayout.getTabAt(1);
                if (tab.equals(tableTab)) {
                    tabLayout.getTabAt(1).setIcon(R.drawable.clear_service);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void registerSocketReceiver() {
        IntentFilter filter = new IntentFilter("service");
        broadcastManager.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SocketMessage socketMessage = (SocketMessage) intent.getSerializableExtra("socketMessage");
            if (socketMessage.getReceiver().equals("waiter") && tabLayout.getSelectedTabPosition() != 1) {
                tabLayout.getTabAt(1).setIcon(R.drawable.service);
            }
        }
    };

    private boolean showServiceIcon() {
        boolean showIcon = false;
        String url = Url.URL_SERVER + "TableServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getAllOrdId");
        try {
            String result = new CommonTask(url, jsonObject.toString()).execute().get();
            Type listType = new TypeToken<List<Table>>(){}.getType();
            List<Table> tables = new Gson().fromJson(result, listType);
            showIcon = tables.stream().anyMatch(Table::isStatus);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return showIcon;
    }
}
