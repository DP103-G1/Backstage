package com.example.manager.table;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class InsertFragment extends Fragment {
    private final static String TAG = "TAG_InsertFragment";
    private FragmentActivity activity;
    private EditText etPeople,etTableId;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        etPeople = view.findViewById(R.id.etPeople);
        etTableId = view.findViewById(R.id.etTableId);

        Button btInsert = view.findViewById(R.id.btInsert);
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tableId = Integer.parseInt(etTableId.getText().toString().trim());
                if (tableId == 0){
                    Common.showToast(getActivity(),R.string.textTableIdIsInvalid);
                    return;
                }
                String people = etPeople.getText().toString().trim();
                if (people.length()<=0){
                    Common.showToast(getActivity(),R.string.textPeopleInvalid);
                }
                if (Common.networkConnected(activity)){
                    String url = Url.URL_SERVER + "TableServlet";
                    Table table = new Table(tableId,people,0);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","tableInsert");
                    jsonObject.addProperty("table",new Gson().toJson(table));
                    int count = 0;
                    try {
                        String result = new CommonTask(url,jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    }catch (Exception e){
                        Log.e(TAG,e.toString());
                    }
                    if (count == 0){
                        Common.showToast(getActivity(),R.string.textInsertFail);
                    }else {
                        Common.showToast(getActivity(),R.string.textInsertSuccess);
                    }
                }else {
                    Common.showToast(getActivity(),R.string.textNoNetwork);
                }
                navController.popBackStack();
            }
        });
        Button btCancel = view.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });
    }
}
