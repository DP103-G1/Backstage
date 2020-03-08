package com.example.manager.table;



import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class UpdateFragment extends Fragment {

    private final static String TAG = "TAG_UpdateFragment";
    private FragmentActivity activity;
    private EditText etPeople,etTableNo;
    private Table table;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPeople = view.findViewById(R.id.etPeople);
        etTableNo = view.findViewById(R.id.etTableNo);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("table") == null){
            Common.showToast(activity,R.string.textNoTableFound);
            navController.popBackStack();
            return;
        }
        table =(Table) bundle.getSerializable("table");
        showTable();

        Button btUpdate = view.findViewById(R.id.btUpdate);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String people = etPeople.getText().toString().trim();
                if (people.length()<=0){
                    Common.showToast(activity,R.string.textPeopleInvalid);
                    return;
                }
                int tableId = Integer.parseInt(etTableNo.getText().toString().trim());
                if (tableId==0){
                    Common.showToast(activity,R.string.textTableIdIsInvalid);
                    return;
                }
                if (Common.networkConnected(activity)){
                    String url = Url.URL_SERVER + "TableServlet";
                    Table table = new Table(tableId, people);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","tableUpdate");
                    jsonObject.addProperty("table",new Gson().toJson(table));
                    int count = 0 ;
                    try {
                        String result = new CommonTask(url,jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    }catch (Exception e ){
                        Log.e(TAG,e.toString());
                    }
                    if (count == 0){
                        Common.showToast(activity,R.string.textUpdateFail);
                    }else {
                        Common.showToast(activity,R.string.textUpdateSuccess);
                    }
                }else {
                    Common.showToast(activity,R.string.textNoNetwork);
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

    private void showTable() {
        etPeople.setText(table.getTablePeople());
        etTableNo.setText(String.valueOf(table.getTableId()));
    }
}
