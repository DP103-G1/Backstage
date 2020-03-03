package com.example.waiter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Common;
import com.example.g1.R;
import com.example.manager.table.Table;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class WaiterTableFragment extends Fragment {
    private static final String TAG = "TAF_WaiterTableFragment";
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvTable;
    private List<Table> tables;
    private CommonTask tableGetAllTask;
    private ImageTask tableTask;

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
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvTable = view.findViewById(R.id.rvMd);
        rvTable.setLayoutManager(new LinearLayoutManager(activity));
        tables = getTable();
        showTables(tables);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                tables = getTable();
                showTables(tables);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private List<Table> getTable() {
        List<Table> tables = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/TableServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllOrdId");
            String jsonOut = jsonObject.toString();
            tableGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = tableGetAllTask.execute().get();
                Type listType = new TypeToken<List<Table>>() {

                }.getType();
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
            String url = Common.URL_SERVER + "/TableServlet";
            int tableId = table.getTableId();
            int OrdId = table.getORD_ID();
            tableTask = new ImageTask(url, String.valueOf(tableId));
            tableTask.execute();
            holder.tvTableNo.setText(String.valueOf(table.getTableId()));
            if (OrdId > 0){
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (OrdId > 0){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("table", tableId);
                        Navigation.findNavController(v).navigate(R.id.waiterMenuDetailFragment, bundle);

//                    }
                }
            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (tableGetAllTask != null){
            tableGetAllTask.cancel(true);
            tableGetAllTask = null;
        }
        if (tableTask != null){
            tableTask.cancel(true);
            tableTask = null;
        }
    }
}
