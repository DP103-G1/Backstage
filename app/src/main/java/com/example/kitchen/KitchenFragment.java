package com.example.kitchen;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.main.Common;
import com.example.main.MenuDetail;
import com.example.main.R;
import com.example.main.Url;
import com.example.socket.SocketMessage;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class KitchenFragment extends Fragment {
    private static final String TAG = "TAG_KitchFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvKitch;
    private Activity activity;
    private CommonTask kitchGetAllTask;
    private List<MenuDetail> menuDetails;
    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_kitchen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerSocketReceiver();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvKitch = view.findViewById(R.id.rvKitch);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            menuDetails = getMenuDetail();
            showMenuDetail(menuDetails);
            swipeRefreshLayout.setRefreshing(false);
        });

        rvKitch.setLayoutManager(new LinearLayoutManager(activity));
        menuDetails = getMenuDetail();
        showMenuDetail(menuDetails);
    }

    private List<MenuDetail> getMenuDetail() {
        List<MenuDetail> menuDetails = null;
        if (Common.networkConnected(activity)) {
            String url = Url.URL_SERVER + "MenuDetailServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("type", "kitchen");
            String jsonOut = jsonObject.toString();
            kitchGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = kitchGetAllTask.execute().get();
                Type listType = new TypeToken<List<MenuDetail>>() {
                }.getType();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                menuDetails = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return menuDetails;
    }

    private void showMenuDetail(List<MenuDetail> menuDetails) {
        if (menuDetails == null || menuDetails.isEmpty()) {
            Common.showToast(activity, R.string.textNOMenu);
        }
        MenuDetailAdapter menuDetailAdapter = (MenuDetailAdapter) rvKitch.getAdapter();

        if (menuDetailAdapter == null) {
            rvKitch.setAdapter(new MenuDetailAdapter(activity, menuDetails));
        } else {
            menuDetailAdapter.setMenuDetail(menuDetails);
            menuDetailAdapter.notifyDataSetChanged();
        }
    }

    private void registerSocketReceiver() {
        IntentFilter filter = new IntentFilter("menuDetail");
        broadcastManager.registerReceiver(menuDetailReceiver, filter);
    }

    private BroadcastReceiver menuDetailReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SocketMessage socketMessage =
                    (SocketMessage) intent.getSerializableExtra("socketMessage");
            if (socketMessage.getReceiver().equals("kitchen") && socketMessage.getMenuDetail().isPresent()) {
                MenuDetail menuDetail = socketMessage.getMenuDetail().get();
                menuDetails.add(menuDetail);
                showMenuDetail(menuDetails);
            }
        }
    };

    private class MenuDetailAdapter extends RecyclerView.Adapter<MenuDetailAdapter.MyviewHolder> {
        private LayoutInflater layoutInflater;
        private List<MenuDetail> menuDetails;

        MenuDetailAdapter(Context context, List<MenuDetail> menuDetails) {
            layoutInflater = LayoutInflater.from(context);
            this.menuDetails = menuDetails;
        }

        void setMenuDetail(List<MenuDetail> menuDetails) {
            this.menuDetails = menuDetails;
        }

        class MyviewHolder extends RecyclerView.ViewHolder {
            TextView tvTableId, tvFoodName, tvFoodAmount;
            Button btStatus;

            MyviewHolder(@NonNull View itemView) {
                super(itemView);
                tvTableId = itemView.findViewById(R.id.tvTableId);
                tvFoodName = itemView.findViewById(R.id.tvFoodName);
                tvFoodAmount = itemView.findViewById(R.id.tvFoodAmount);
                btStatus = itemView.findViewById(R.id.btStatus);
            }
        }

        @Override
        public int getItemCount() {
            return menuDetails.size();
        }

        @NonNull
        @Override
        public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_kitch, parent, false);
            return new MyviewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
            final MenuDetail menuDetail = menuDetails.get(position);
            holder.tvFoodName.setText(menuDetail.getFOOD_NAME());
            holder.tvTableId.setText(String.valueOf(menuDetail.getTABLE_ID()));
            holder.tvFoodAmount.setText(String.valueOf(menuDetail.getFOOD_AMOUNT()));
            holder.btStatus.setOnClickListener(v -> {
                menuDetail.setFOOD_STATUS(true);
                if (Common.networkConnected(activity)) {
                    String url1 = Url.URL_SERVER + "MenuDetailServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "update");
                    jsonObject.addProperty("menuDetail", new Gson().toJson(menuDetail));
                    int count = 0;
                    try {
                        String result = new CommonTask(url1, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count != 0){
                        SocketMessage socketMessage = new SocketMessage("menuDetail", "member" + menuDetail.getMemberId(), menuDetail);
                        Common.eZeatsWebSocketClient.send(new Gson().toJson(socketMessage));
                        socketMessage.setReceiver("waiter");
                        Common.eZeatsWebSocketClient.send(new Gson().toJson(socketMessage));
                        menuDetails.remove(position);
                        notifyDataSetChanged();
                        Common.showToast(getActivity(), R.string.textUpdateSuccess);
                    } else {
                        Common.showToast(getActivity(), R.string.textUpdateFail);
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (kitchGetAllTask != null) {
            kitchGetAllTask.cancel(true);
            kitchGetAllTask = null;
        }
    }
}

