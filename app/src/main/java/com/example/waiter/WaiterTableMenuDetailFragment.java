package com.example.waiter;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class WaiterTableMenuDetailFragment extends Fragment {
    private static final String TAG = "TAG_WaiterMenuDetailFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private RecyclerView rvMd;
    private List<MenuDetail> menuDetails;
    private CommonTask OrderGetAllTask;
    private ImageTask OrderTask;
    private int bkId;
    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_waiter_table_menu_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerSocketReceiver();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvMd = view.findViewById(R.id.rvMd);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                menuDetails = getMenuDetail();
                showmenudetail(menuDetails);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getInt("bkId") == 0) {
            Common.showToast(activity, R.string.textNoOrder);
            navController.popBackStack();
            return;
        }
        bkId = bundle.getInt("bkId");

        rvMd.setLayoutManager(new LinearLayoutManager(activity));
        menuDetails = getMenuDetail();
        showmenudetail(menuDetails);
    }

    private void registerSocketReceiver() {
        IntentFilter filter = new IntentFilter("menuDetail");
        broadcastManager.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SocketMessage socketMessage =
                    (SocketMessage) intent.getSerializableExtra("socketMessage");
            String message = socketMessage.getMessage();
            if (socketMessage.getReceiver().equals("waiter") && message != null && !message.isEmpty()) {
                Type listType = new TypeToken<List<MenuDetail>>(){}.getType();
                List<MenuDetail> socketMenuDetails = new Gson().fromJson(message, listType);
                menuDetails.removeAll(socketMenuDetails);
                menuDetails.addAll(socketMenuDetails);
                Comparator<MenuDetail> cmp = Comparator.comparing(MenuDetail::isFOOD_STATUS).reversed();
                menuDetails = menuDetails.stream().sorted(cmp).collect(Collectors.toList());
                showmenudetail(menuDetails);
            }
        }
    };

    private List<MenuDetail> getMenuDetail() {
        List<MenuDetail> menuDetails = null;
        if (Common.networkConnected(activity)) {
            String url = Url.URL_SERVER + "MenuDetailServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllByBkId");
            jsonObject.addProperty("bkId", bkId);
            String jsonOut = jsonObject.toString();
            OrderGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = OrderGetAllTask.execute().get();
                Type listType = new TypeToken<List<MenuDetail>>() {
                }.getType();
                menuDetails = Common.gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return menuDetails;
    }

    private void showmenudetail(List<MenuDetail> menuDetailrs) {
        if (menuDetailrs == null || menuDetailrs.isEmpty()) {
            Common.showToast(activity, R.string.textNoOrder);
        }
        MenuDetailAdapter orderAdapter = (MenuDetailAdapter) rvMd.getAdapter();

        if (orderAdapter == null) {
            rvMd.setAdapter(new MenuDetailAdapter(activity, menuDetails));
        } else {
            orderAdapter.setOrders(menuDetails);
            orderAdapter.notifyDataSetChanged();
        }
    }

    private class MenuDetailAdapter extends RecyclerView.Adapter<MenuDetailAdapter.OrderViewHolder> {
        private LayoutInflater layoutInflater;
        private List<MenuDetail> menuDetails;

        MenuDetailAdapter(Context context, List<MenuDetail> menuDetails) {
            layoutInflater = LayoutInflater.from(context);
            this.menuDetails = menuDetails;
        }

        void setOrders(List<MenuDetail> menuDetails) {
            this.menuDetails = menuDetails;
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvAmount;
            Button btStatus;

            OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvFoodName);
                tvAmount = itemView.findViewById(R.id.tvFoodAmount);
                btStatus = itemView.findViewById(R.id.btStatus);
            }
        }

        @Override
        public int getItemCount() {
            return menuDetails.size();
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_waiter_menudetail, parent, false);
            return new OrderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            final MenuDetail menuDetail = menuDetails.get(position);
            holder.tvName.setText(menuDetail.getFOOD_NAME());
            holder.tvAmount.setText(String.valueOf(menuDetail.getFOOD_AMOUNT()));
            if (!menuDetail.isFOOD_STATUS()) {
                holder.btStatus.setEnabled(false);
                holder.btStatus.setText(R.string.textMaking);
                holder.btStatus.setTextColor(getResources().getColor(R.color.normalText, activity.getTheme()));
            } else if (!menuDetail.isFOOD_ARRIVAL()) {
                holder.btStatus.setEnabled(true);
                holder.btStatus.setText(R.string.textSending);
                holder.btStatus.setTextColor(getResources().getColor(R.color.colorSecondary, activity.getTheme()));
            } else {
                holder.btStatus.setEnabled(false);
                holder.btStatus.setText(R.string.textComplete);
                holder.btStatus.setBackgroundColor(getResources().getColor(R.color.cardBackground, activity.getTheme()));
            }
            holder.btStatus.setOnClickListener(v -> {
                menuDetail.setFOOD_ARRIVAL(true);
                if (Common.networkConnected(activity)) {
                    String url = Url.URL_SERVER + "MenuDetailServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "update");
                    jsonObject.addProperty("menuDetail", new Gson().toJson(menuDetail));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count != 0) {
                        SocketMessage socketMessage = new SocketMessage("menuDetail",
                                "member" + menuDetail.getMemberId(),
                                new Gson().toJson(menuDetail));
                        Common.eZeatsWebSocketClient.send(new Gson().toJson(socketMessage));
                        Common.showToast(getActivity(), R.string.textUpdateSuccess);
                        holder.btStatus.setEnabled(false);
                        holder.btStatus.setText(R.string.textComplete);
                        holder.btStatus.setBackgroundColor(getResources().getColor(R.color.cardBackground, activity.getTheme()));
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
        if (OrderTask != null) {
            OrderTask.cancel(true);
            OrderTask = null;
        }
        if (OrderGetAllTask != null) {
            OrderGetAllTask.cancel(true);
            OrderGetAllTask = null;
        }
    }
}
