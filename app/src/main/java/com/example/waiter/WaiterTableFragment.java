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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.manager.table.Table;
import com.example.socket.SocketMessage;
import com.example.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;


public class WaiterTableFragment extends Fragment {
    private static final String TAG = "TAF_WaiterTableFragment";
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvTable;
    private List<Table> tables;
    private CommonTask tableGetAllTask;
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
        return inflater.inflate(R.layout.fragment_waiter_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerSocketReceiver();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvTable = view.findViewById(R.id.rvMd);
        rvTable.setLayoutManager(new LinearLayoutManager(activity));
        tables = getTable();
        showTables(tables);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            tables = getTable();
            showTables(tables);
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void registerSocketReceiver() {
        IntentFilter filter = new IntentFilter("service");
        filter.addAction("seat");
        broadcastManager.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SocketMessage socketMessage = (SocketMessage) intent.getSerializableExtra("socketMessage");
            String message = socketMessage.getMessage();
            if (socketMessage.getReceiver().equals("waiter") && message != null && !message.isEmpty()) {
                Table table = new Gson().fromJson(message, Table.class);
                tables.remove(table);
                tables.add(table);
                Comparator<Table> cmp = Comparator.comparing(Table::getTableId);
                tables = tables.stream().sorted(cmp).collect(Collectors.toList());
                showTables(tables);
            }
        }
    };

    private List<Table> getTable() {
        List<Table> tables = null;
        if (Common.networkConnected(activity)) {
            String url = Url.URL_SERVER + "/TableServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllOrdId");
            String jsonOut = jsonObject.toString();
            tableGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = tableGetAllTask.execute().get();
                Type listType = new TypeToken<List<Table>>(){}.getType();
                tables = Common.gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return tables;
    }

    private void showTables(List<Table> tables) {
        if (tables == null || tables.isEmpty()) {
            Common.showToast(activity, R.string.textNoTablesFound);
        }
        TableAdapter tableAdapter = (TableAdapter) rvTable.getAdapter();
        if (tableAdapter == null) {
            rvTable.setAdapter(new TableAdapter(activity, tables));
        } else {
            tableAdapter.setTables(tables);
            tableAdapter.notifyDataSetChanged();
        }
    }

    private class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableHolder> {
        private LayoutInflater layoutInflater;
        private List<Table> tables;

        TableAdapter(Context context, List<Table> tables) {
            layoutInflater = LayoutInflater.from(context);
            this.tables = tables;
        }

        void setTables(List<Table> tables) {
            this.tables = tables;
        }

        class TableHolder extends RecyclerView.ViewHolder {
            TextView tvTableNo;

            TableHolder(@NonNull View itemView) {
                super(itemView);
                tvTableNo = itemView.findViewById(R.id.tvTableNo);
            }
        }

        @Override
        public int getItemCount() {
            return tables.size();
        }

        @NonNull
        @Override
        public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.item_view_table, parent, false);
            return new TableHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TableHolder holder, int position) {
            final Table table = tables.get(position);
            int bkId = table.getORD_ID();
            holder.tvTableNo.setText(String.valueOf(table.getTableId()));
            if (table.isStatus()) {
                holder.itemView.setBackgroundColor(Color.parseColor("#FF0000"));
            } else if (bkId > 0){
                holder.itemView.setBackgroundColor(Color.parseColor("#879E33"));
            } else {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.cardBackground, activity.getTheme()));
            }
            holder.itemView.setOnClickListener(v -> {
                if (table.isStatus()) {
                    table.setStatus(false);
                    String url = Url.URL_SERVER + "TableServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "updateStatus");
                    jsonObject.addProperty("table", new Gson().toJson(table));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count != 0) {
                        SocketMessage socketMessage = new SocketMessage("service", "member" + count, "");
                        Common.eZeatsWebSocketClient.send(new Gson().toJson(socketMessage));
                        holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        Common.showToast(activity, R.string.textUpdateSuccess);
                    } else {
                        Common.showToast(activity, R.string.textUpdateFail);
                    }
                } else if (bkId > 0){
                    Bundle bundle = new Bundle();
                    bundle.putInt("bkId", bkId);
                    Navigation.findNavController(v).navigate(R.id.waiterMenuDetailFragment, bundle);
                } else {
                    Common.showToast(activity, R.string.textNoUser);
                }
            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        broadcastManager.unregisterReceiver(receiver);
        if (tableGetAllTask != null){
            tableGetAllTask.cancel(true);
            tableGetAllTask = null;
        }
    }
}
