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

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.task.CommonTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;


public class LoginFragment extends Fragment {
        private static final String TAG = "TAG_LoginFragment";
        private Activity activity;
        private NavController navController;
        private EditText edAcc,edPass;
        private CommonTask loginTask;
        private String textAcc,textPass;
        private BottomNavigationView bottomNavigationView;

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
        Common.disconnectSocketServer();
        navController = Navigation.findNavController(view);
        bottomNavigationView = activity.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setVisibility(View.GONE);
        edAcc = view.findViewById(R.id.edAcc);
        edPass = view.findViewById(R.id.edPass);

        edAcc.setOnFocusChangeListener((v, hasFocus) -> {
        if (!hasFocus){
            textAcc = edAcc.getText().toString().trim().toLowerCase();
            edAcc.setText(textAcc);
            if (textAcc.isEmpty()){
                edAcc.setError(getString(R.string.textInputEmail));
            }

          }
        });

        edPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                textPass = edPass.getText().toString().trim();
                if (textPass.isEmpty()){
                    edPass.setError(getString(R.string.textInputPassword));
                }
            }
        });

        Button btLogin = view.findViewById(R.id.btLogin);
        btLogin.setOnClickListener(v -> {
            textAcc = edAcc.getText().toString().trim().toLowerCase();
            textPass = edPass.getText().toString().trim();
            String url = Url.URL_SERVER + "/EmployeeServlet";
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
                        bottomNavigationView.getMenu().clear();
                        bottomNavigationView.inflateMenu(R.menu.manager_menu);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        navController.navigate(R.id.mainFragment);
                    }else if(employeeId == 1){
                        Common.connectSocketServer(activity, "kitchen");
                        navController.navigate(R.id.action_loginFragment_to_kitchenFragment);
                    }else if(employeeId == 2 ){
                        Common.connectSocketServer(activity, "waiter");
                        bottomNavigationView.getMenu().clear();
                        bottomNavigationView.inflateMenu(R.menu.waiter_menu);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        navController.navigate(R.id.action_loginFragment_to_waiterFragment);
                    }
                }else {
                    Common.showToast(activity,R.string.textLoginfail);


                }
            }catch (Exception e){
                Log.e(TAG,e.toString());

            }


        });
    }



}
