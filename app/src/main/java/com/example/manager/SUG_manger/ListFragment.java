package com.example.manager.SUG_manger;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.Common;
import com.example.g1.R;



public class ListFragment extends Fragment {
    private static final String TAG = "TAG_BoxListFragment";
    private FragmentActivity activity;
    private TextView tvTopic;
    private EditText etReply;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTopic = view.findViewById(R.id.tvTopic);
        etReply = view.findViewById(R.id.etReply);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getSerializable("box") != null){
            Box box = (Box)bundle.getSerializable("box");
            String topic = box.getTopic();
            String reply = box.getReply();

            String url = Common.URL_SERVER + "/BoxServlet";
            tvTopic.setText(topic);
            etReply.setText(reply);
        }
    }
}
