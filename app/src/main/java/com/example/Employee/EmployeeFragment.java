package com.example.Employee;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;


public class EmployeeFragment extends Fragment {
    private final static String TAG = "TAG_EmployeeFragment";
    private Activity  activity;
    private Employee employee;
    private EditText[] etPasswords = new EditText[3];
    private Button[] btUpdates = new Button[3];
    private CommonTask employeeGetAllTask;
    private List<Employee> employeeList;
    private View employeeFragmentView;
    private int[] editTextsId = {R.id.etManagerPassWord, R.id.etKitchenPassWord, R.id.etWaiterPassWord};
    private int[] buttonsId = {R.id.btUpdate1, R.id.btUpdate2, R.id.btUpdate3};


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
        employeeFragmentView = view;
        employeeList = getEmployees();
        for (int i = 0; i < etPasswords.length; i++) {
            btUpdates[i] = view.findViewById(buttonsId[i]);
            btUpdates[i].setOnClickListener(this::update);
            etPasswords[i] = view.findViewById(editTextsId[i]);
            etPasswords[i].setText(employeeList.get(i).getPassword());
        }
    }

    private void update(View v) {
        for (int index = 0; index < btUpdates.length; index++) {
            if (v.getId() == buttonsId[index]) {
                Button bt = (Button) v;
                EditText et = employeeFragmentView.findViewById(editTextsId[index]);
                if (bt.getText().equals("修改")) {
                    bt.setText("確定");
                    et.setFocusableInTouchMode(true);
                    et.setFocusable(true);
                    bt.setBackgroundColor(Color.WHITE);
                } else {
                    bt.setText("修改");
                    et.setFocusableInTouchMode(false);
                    et.setFocusable(false);
                    String password = et.getText().toString().trim();
                    bt.setBackgroundColor(Color.rgb(34,34,34));
                    updatePassword(index, password);
                }
                break;
            }
        }
    }

    private List<Employee> getEmployees(){
       List<Employee> employees = null;
        if (Common.networkConnected(activity)){
            String url = Url.URL_SERVER + "/EmployeeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            employeeGetAllTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = employeeGetAllTask.execute().get();
                Type listType = new TypeToken<List<Employee>>(){

                }.getType();
                employees = Common.gson.fromJson(jsonIn,listType);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }else {
            Common.showToast(activity,R.string.textNoNetwork);
        }
        return employees;
    }



    private Employee updatePassword(int id, String password){
        Employee employees = null;
        if (Common.networkConnected(activity)){
            String url = Url.URL_SERVER + "/EmployeeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","updatePassword");
            jsonObject.addProperty("employee_Id",id);
            jsonObject.addProperty("password", password);
            String jsonOut = jsonObject.toString();
            employeeGetAllTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = employeeGetAllTask.execute().get();
                Type listType = new TypeToken<List<Employee>>(){

                }.getType();
                employees = Common.gson.fromJson(jsonIn,listType);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }else {
            Common.showToast(activity,R.string.textNoNetwork);
        }
        return employees;

    }


}
