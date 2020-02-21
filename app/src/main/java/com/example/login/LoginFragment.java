package com.example.login;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.Common;
import com.example.g1.R;
import com.example.task.CommonTask;
import com.google.gson.JsonObject;


public class LoginFragment extends Fragment {
        private static final String TAG = "TAG_LoginFragment";
        private Activity activity;
        private NavController navController;
        private EditText edAcc,edPass;
        private CommonTask loginTask;
        private String textAcc,textPass;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        edAcc = view.findViewById(R.id.edAcc);
        edPass = view.findViewById(R.id.edPass);

        edAcc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus){
                textAcc = edAcc.getText().toString().trim().toLowerCase();
                edAcc.setText(textAcc);
                if (textAcc.isEmpty()){
                    edAcc.setError(getString(R.string.textInputEmail));
                }

              }
            }
        });

        edPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    textPass = edPass.getText().toString().trim();
                    if (textPass.isEmpty()){
                        edPass.setError(getString(R.string.textInputPassword));
                    }
                }
            }
        });

        Button btLogin = view.findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAcc = edAcc.getText().toString().trim().toLowerCase();
                textPass = edPass.getText().toString().trim();
                String url = Common.URL_SERVER + "/EmployeeServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action","login");
                jsonObject.addProperty("account",textAcc);
                jsonObject.addProperty("password",textPass);
                loginTask = new CommonTask(url,jsonObject.toString());
                try {
                    String result = loginTask.execute().get();
                    if (!result.equals("null")){
                        int employeeId = Integer.valueOf(result);
                        SharedPreferences pref = activity.getSharedPreferences(Common.EMPLOYEE_PREFRENCE, Context.MODE_PRIVATE);
                        pref.edit().putString("account",textAcc).putString("password",textPass).putInt("employeeId",employeeId).apply();
//                        navController.popBackStack(R.id.homeFragment,false);
                        Common.showToast(activity,getString(R.string.textLoginSuccess));
                        if(employeeId == 0) {
                            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homeFragment);
                        }else if(employeeId == 1){
                            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_kitchenFragment);
                        }else if(employeeId == 2 ){
                            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_waiterFragment);
                        }
                    }else {
                        Common.showToast(activity,R.string.textLoginfail);


                    }
                }catch (Exception e){
                    Log.e(TAG,e.toString());

                }


            }
        });
    }



}
