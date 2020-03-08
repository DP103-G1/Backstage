package com.example.waiter;


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
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */
public class WaiterMenuDetailFragment extends Fragment {
    private static final String TAG = "TAG_WaiterMenuDetailFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvMenuDetail;
    private Activity activity;
    private CommonTask waiterGetAllTask;
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
        return inflater.inflate(R.layout.fragment_waiter_menu_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerSocketReceiver();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvMenuDetail = view.findViewById(R.id.rvMenuDetail);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            menuDetails = getMenuDetail();
            showMenuDetail(menuDetails);
            swipeRefreshLayout.setRefreshing(false);
        });

        rvMenuDetail.setLayoutManager(new LinearLayoutManager(activity));
        menuDetails = getMenuDetail();
        showMenuDetail(menuDetails);
    }

    private void registerSocketReceiver() {
        IntentFilter filter = new IntentFilter("menuDetail");
        broadcastManager.registerReceiver(socketReceiver, filter);
    }

    private BroadcastReceiver socketReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SocketMessage socketMessage =
                    (SocketMessage) intent.getSerializableExtra("socketMessage");
            String message = socketMessage.getMessage();
            if (socketMessage.getReceiver().equals("waiter") && message != null && !message.isEmpty()) {
                Type listType = new TypeToken<List<MenuDetail>>(){}.getType();
                List<MenuDetail> addMenuDetails = new Gson().fromJson(message, listType);
                menuDetails.removeAll(addMenuDetails);
                menuDetails.addAll(addMenuDetails);
                Comparator<MenuDetail> cmp = Comparator.comparing(MenuDetail::isFOOD_STATUS).reversed();
                menuDetails = menuDetails.stream().sorted(cmp).collect(Collectors.toList());
                showMenuDetail(menuDetails);
            }
        }
    };

    private List<MenuDetail> getMenuDetail() {
        List<MenuDetail> menuDetails = null;
        if (Common.networkConnected(activity)) {
            String url = Url.URL_SERVER + "MenuDetailServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("type", "waiter");
            String jsonOut = jsonObject.toString();
            waiterGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = waiterGetAllTask.execute().get();
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
        MenuDetailAdapter menuDetailAdapter = (MenuDetailAdapter) rvMenuDetail.getAdapter();

        if (menuDetailAdapter == null) {
            rvMenuDetail.setAdapter(new MenuDetailAdapter(activity, menuDetails));
        } else {
            menuDetailAdapter.setMenuDetail(menuDetails);
            menuDetailAdapter.notifyDataSetChanged();
        }
    }

    private class MenuDetailAdapter extends RecyclerView.Adapter<MenuDetailAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<MenuDetail> menuDetails;

        MenuDetailAdapter(Context context, List<MenuDetail> menuDetails) {
            layoutInflater = LayoutInflater.from(context);
            this.menuDetails = menuDetails;
        }

        void setMenuDetail(List<MenuDetail> menuDetails) {
            this.menuDetails = menuDetails;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvTableId, tvFoodName, tvFoodAmount;
            Button btStatus;

            MyViewHolder(@NonNull View itemView) {
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
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_kitch, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final MenuDetail menuDetail = menuDetails.get(position);
            holder.tvFoodName.setText(menuDetail.getFOOD_NAME());
            holder.tvTableId.setText(String.valueOf(menuDetail.getTABLE_ID()));
            holder.tvFoodAmount.setText(String.valueOf(menuDetail.getFOOD_AMOUNT()));
            if (menuDetail.isFOOD_STATUS()) {
                holder.btStatus.setEnabled(true);
                holder.btStatus.setText(R.string.textSending);
                holder.btStatus.setTextColor(getResources().getColor(R.color.colorSecondary, activity.getTheme()));
            } else {
                holder.btStatus.setEnabled(false);
                holder.btStatus.setText(R.string.textMaking);
                holder.btStatus.setTextColor(getResources().getColor(R.color.normalText, activity.getTheme()));
            }
            holder.btStatus.setOnClickListener(v -> {
                menuDetail.setFOOD_ARRIVAL(true);
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
                        Common.showToast(getActivity(), R.string.textUpdateSuccess);
                        List<MenuDetail> socketMenuDetails = new ArrayList<>();
                        socketMenuDetails.add(menuDetail);
                        SocketMessage socketMessage = new SocketMessage("menuDetail",
                                "member" + menuDetail.getMemberId(),
                                new Gson().toJson(socketMenuDetails));
                        Common.eZeatsWebSocketClient.send(new Gson().toJson(socketMessage));
                        menuDetails.remove(position);
                        notifyDataSetChanged();
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
        broadcastManager.unregisterReceiver(socketReceiver);
        if (waiterGetAllTask != null) {
            waiterGetAllTask.cancel(true);
            waiterGetAllTask = null;
        }
    }
}
