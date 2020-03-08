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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class WaiterMenuDetailFragment extends Fragment {
    private static final String TAG = "TAG_WaiterMenuDetailFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private RecyclerView rvMd;
    private List<MenuDetail> menuDetails;
    private CommonTask OrderGetAllTask;
    private ImageTask OrderTask;
    private int bkId;

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
        return inflater.inflate(R.layout.fragment_waiter_menu_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private List<MenuDetail> getMenuDetail() {
        List<MenuDetail> menuDetails = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "MenuDetailServlet";
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
            TextView tvName, tvAmount, tvkitchStatus;
            Button btStatus;
            int ordId, amount, total;
            String menuId;
            boolean arrival, status;

            OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvkitchStatus = itemView.findViewById(R.id.tvkitchStatus);
                btStatus = itemView.findViewById(R.id.btStatus);
                btStatus.setOnClickListener(v -> {
                    if (status) {
                        if (!arrival) {
                            arrival = true;
                            if (Common.networkConnected(activity)) {
                                String url = Common.URL_SERVER + "MenuDetailServlet";
                                MenuDetail menuDetails = new MenuDetail(ordId, menuId, amount, arrival, total, status);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "update");
                                jsonObject.addProperty("menuDetail", new Gson().toJson(menuDetails));
                                int count = 0;
                                try {
                                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                    count = Integer.valueOf(result);
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                                if (count != 0) {
                                    Common.showToast(getActivity(), R.string.textUpdateSuccess);
                                    if (arrival) {
                                        btStatus.setBackgroundColor(Color.parseColor("#424242"));
                                        btStatus.setText("已送達");
                                    }
                                } else {
                                    Common.showToast(getActivity(), R.string.textUpdateFail);
                                }
                            }
                        } else {
                            Common.showToast(activity, R.string.falseStatus);
                        }
                    } else {
                        Common.showToast(activity, R.string.textNoStatus);
                    }
                });
            }

            public void setArrival(boolean food_arrival) {
                this.arrival = food_arrival;
            }

            public void setOrdId(int ord_id) {
                this.ordId = ord_id;
            }

            public void setMenuId(String menu_id) {
                this.menuId = menu_id;
            }

            public void setAmount(int food_amount) {
                this.amount = food_amount;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public void setStatus(boolean food_status) {
                this.status = food_status;
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
            holder.setOrdId(menuDetail.getORD_ID());
            holder.setMenuId(menuDetail.getMENU_ID());
            holder.setAmount(menuDetail.getFOOD_AMOUNT());
            holder.setArrival(menuDetail.isFOOD_ARRIVAL());
            holder.setTotal(menuDetail.getTOTAL());
            holder.setStatus(menuDetail.isFOOD_STATUS());
            if (menuDetail.isFOOD_ARRIVAL()) {
                holder.btStatus.setText("已送達");
                holder.btStatus.setBackgroundColor(Color.parseColor("#424242"));
            } else {
                holder.btStatus.setText("未送出");
                holder.btStatus.setBackgroundColor(Color.parseColor("#222222"));
            }
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
