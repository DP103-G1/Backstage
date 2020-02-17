package com.example.Employee;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.g1.R;



public class EmployeeFragment extends Fragment {
    private final static String TAG = "TAG_EmployeeFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvEmployee;
    private Activity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout =view.findViewById(R.id.swipeRefreshLayout);
        rvEmployee = view.findViewById(R.id.rvWaiterSelect);
        rvEmployee.setLayoutManager(new LinearLayoutManager(activity));


    }
}
