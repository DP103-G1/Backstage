package com.example.kitchen;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.g1.R;
import com.example.task.CommonTask;


public class KitchenFragment extends Fragment {
    private static final String TAG = "TAG_KitchFragment";
    private RecyclerView rvKitch;
    private Activity activity;
    private CommonTask kitchGetAllTask;
//    private List<menu>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kitchen, container, false);
    }

}
