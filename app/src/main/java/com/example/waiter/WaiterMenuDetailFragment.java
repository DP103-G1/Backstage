package com.example.waiter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.example.Common;
import com.example.g1.MenuDetail;
import com.example.g1.Order;
import com.example.g1.R;
import com.example.manager.table.Table;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class WaiterMenuDetailFragment extends Fragment {
    private static  final String TAG = "TAG_WaiterMenuDetailFragment";
    private Activity activity;
    private RecyclerView rvMd;
    private Button btBill;
    private List<MenuDetail> menuDetails;
    private CommonTask OrderGetAllTask;
    private ImageTask OrderTask;
//    private Table table;
    private int tableId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        tableId = (int) (getArguments() != null ? getArguments().getSerializable("table") : null);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_waiter_menu_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMd = view.findViewById(R.id.rvMd);
        btBill = view.findViewById(R.id.btBill);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("table") == null) {
            Common.showToast(activity, R.string.textNoOrder);
            navController.popBackStack();
            return;
        }
        tableId = (int) bundle.getSerializable("table");

        rvMd.setLayoutManager(new LinearLayoutManager(activity));
        menuDetails = getMenuDetail();
        showmenudetail(menuDetails);
    }

    private List<MenuDetail> getMenuDetail() {
        List<MenuDetail> menuDetails = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "MenuDetailServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllByTableId");
            jsonObject.addProperty("tableId", tableId);
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
        }else {
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

    private class MenuDetailAdapter extends RecyclerView.Adapter<MenuDetailAdapter.OrderViewHolder>{
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
            TextView tvName, tvAmount, tvkitchStatus;
            Button btStatus;
            boolean arrival;

            OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvkitchStatus = itemView.findViewById(R.id.tvkitchStatus);
                btStatus = itemView.findViewById(R.id.btStatus);
                btStatus.setOnClickListener(v -> {
                    if (!arrival) {
                        
                    }
                });
            }

            public void setStatus(boolean food_arrival) {
                this.arrival = food_arrival;
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
            String url = Common.URL_SERVER + "/MenuDetailServlet";
//            Bitmap bitmap = null;
//            try {
//                bitmap = new ImageTask(url, String.valueOf(tableId)).execute().get();
//            } catch (Exception e){
//                Log.e(TAG,e.toString());
//            }

//            OrderTask = new ImageTask(url, String.valueOf(tableId));
//            OrderTask.execute();
            holder.tvName.setText(menuDetail.getFOOD_NAME());
            holder.tvAmount.setText(String.valueOf(menuDetail.getFOOD_AMOUNT()));
            if (menuDetail.isFOOD_STATUS()) {
                holder.tvkitchStatus.setText("已出餐");
            } else {
                holder.tvkitchStatus.setText("未出餐");
            }
            holder.setStatus(menuDetail.isFOOD_ARRIVAL());
            if (menuDetail.isFOOD_ARRIVAL()) {
                holder.btStatus.setText("已送達");
            } else {
                holder.btStatus.setText("未送出");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (OrderTask != null){
            OrderTask.cancel(true);
            OrderTask = null;
        }
        if (OrderGetAllTask != null){
            OrderGetAllTask.cancel(true);
            OrderGetAllTask = null;
        }
    }
}