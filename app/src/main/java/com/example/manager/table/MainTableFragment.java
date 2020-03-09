package com.example.manager.table;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class MainTableFragment extends Fragment {
    private static final String TAG = "TAG_Table";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvTable;
    private Activity activity;
    private CommonTask tableGetAllTask;
    private CommonTask tableDeleteTask;
    private List<Table> tables;
    private ImageTask tableImageTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvTable = view.findViewById(R.id.rvMd);
        rvTable.setLayoutManager(new LinearLayoutManager(activity));
        tables = getTables();
        showTables(tables);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showTables(tables);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        FloatingActionButton btAdd = view.findViewById(R.id.btAdd);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_mainTableFragment_to_insertFragment);
            }
        });
    }

    private void showTables(List<Table> tables) {
        if (tables == null || tables.isEmpty()){
            Common.showToast(activity,R.string.textNoTablesFound);
        }
        TableAdapter tableAdapter = (TableAdapter) rvTable.getAdapter();
        if (tableAdapter == null){
            rvTable.setAdapter(new TableAdapter(activity,tables));
        }else {
            tableAdapter.setTables(tables);
            tableAdapter.notifyDataSetChanged();
        }
    }

    private List<Table> getTables() {
        List<Table> tables = null;
        if (Common.networkConnected(activity)){
            String url = Url.URL_SERVER + "TableServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            tableGetAllTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = tableGetAllTask.execute().get();
                Type listType = new TypeToken<List<Table>>(){}.getType();
                tables = new Gson().fromJson(jsonIn,listType);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }else{
            Common.showToast(activity,R.string.textNoNetwork);
        }
        return tables;
    }


    private class TableAdapter extends RecyclerView.Adapter<TableAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Table> tables ;


        TableAdapter(Context context, List<Table> tables) {
            layoutInflater = LayoutInflater.from(context);
            this.tables = tables;
        }

         void setTables(List<Table> tables) {
            this.tables = tables;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvTableNo;

             MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTableNo = itemView.findViewById(R.id.tvTableNo);
            }
        }

        @Override
        public int getItemCount() {
            if(tables == null)
                System.out.println("null tables");
                return this.tables.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_table,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                final Table table = tables.get(position);
                String url = Url.URL_SERVER + "TableServlet";
                int id = table.getTableId();
                tableImageTask = new ImageTask(url, String.valueOf(id));
                tableImageTask.execute();
                holder.tvTableNo.setText(String.valueOf(table.getTableId()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("table", table);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_mainTableFragment_to_detailFragment, bundle);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        PopupMenu popupMenu = new PopupMenu(activity,v, Gravity.END);
                        popupMenu.inflate(R.menu.menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.update:
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("table",table);
                                        Navigation.findNavController(v).navigate(R.id.action_mainTableFragment_to_updateFragment,bundle);
                                        break;
                                    case R.id.delete:
                                        if (Common.networkConnected(activity)){
                                            String url = Url.URL_SERVER +"/TableServlet";
                                            JsonObject jsonObject = new JsonObject();
                                            jsonObject.addProperty("action","tableDelete");
                                            jsonObject.addProperty("tableId",table.getTableId());
                                            int count = 0;
                                            try {
                                                tableDeleteTask = new CommonTask(url,jsonObject.toString());
                                                String result = tableDeleteTask.execute().get();
                                                count = Integer.valueOf(result);
                                            }catch (Exception e){
                                                Log.e(TAG,e.toString());
                                            }
                                            if (count == 0){
                                                Common.showToast(activity,R.string.textDeleteFail);
                                            }else {
                                                tables.remove(table);
                                                TableAdapter.this.notifyDataSetChanged();
                                                Common.showToast(activity,R.string.textDeleteSuccess);
                                            }
                                        }else {
                                            Common.showToast(activity,R.string.textNoNetwork);
                                        }
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                });
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (tableGetAllTask !=null){
            tableGetAllTask.cancel(true);
            tableGetAllTask = null;
        }
        if (tableImageTask!=null){
            tableImageTask.cancel(true);
            tableImageTask = null;
        }
        if (tableDeleteTask != null){
            tableDeleteTask.cancel(true);
            tableDeleteTask = null;
        }
    }
}
