package com.example.manager.table;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Common;
import com.example.g1.R;


public class DetailFragment extends Fragment {
    private FragmentActivity activity;
    private TextView tvaTableNo, tvaPeople;
    private Table table;
    private final static String TAG = "TAG_DetailFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvaTableNo = view.findViewById(R.id.tvaTableNo);
        tvaPeople = view.findViewById(R.id.tvaPeople);
        Bundle bundle = getArguments();
        if (bundle != null) {
            table = (Table) bundle.getSerializable("table");
            showTable();
        }
    }

    private void showTable() {
        String url = Common.URL_SERVER + "TableServlet";
        String id = table.getTableId();
        tvaTableNo.setText(table.getTableId());
        tvaPeople.setText(table.getTablePeople());

    }
}
